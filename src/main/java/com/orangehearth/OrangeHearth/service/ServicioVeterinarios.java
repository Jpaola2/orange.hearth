package com.orangehearth.OrangeHearth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orangehearth.OrangeHearth.dto.request.SolicitudActualizacionVeterinario;
import com.orangehearth.OrangeHearth.dto.request.SolicitudCreacionVeterinario;
import com.orangehearth.OrangeHearth.dto.request.SolicitudInicioSesionVeterinario;
import com.orangehearth.OrangeHearth.dto.response.RespuestaVeterinario;
import com.orangehearth.OrangeHearth.exception.ExcepcionRecursoNoEncontrado;
import com.orangehearth.OrangeHearth.exception.ExcepcionValidacion;
import com.orangehearth.OrangeHearth.model.entity.CuentaUsuario;
import com.orangehearth.OrangeHearth.model.entity.Veterinarian;
import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;
import com.orangehearth.OrangeHearth.model.enums.Rol;
import com.orangehearth.OrangeHearth.repository.RepositorioCuentasUsuario;
import com.orangehearth.OrangeHearth.repository.RepositorioVeterinarios;

@Service
public class ServicioVeterinarios {

    private final ServicioPoliticaContrasena passwordPolicyService;
    private final JdbcTemplate jdbcTemplate;
    private final RepositorioCuentasUsuario userAccountRepository;
    private final RepositorioVeterinarios veterinarianRepository;
    private final PasswordEncoder passwordEncoder;

    public ServicioVeterinarios(
        ServicioPoliticaContrasena passwordPolicyService,
        JdbcTemplate jdbcTemplate,
        RepositorioCuentasUsuario userAccountRepository,
        RepositorioVeterinarios veterinarianRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.passwordPolicyService = passwordPolicyService;
        this.jdbcTemplate = jdbcTemplate;
        this.userAccountRepository = userAccountRepository;
        this.veterinarianRepository = veterinarianRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RespuestaVeterinario create(SolicitudCreacionVeterinario request) {
        // Validaciones mínimas
        passwordPolicyService.validateOrThrow(request.password());

        Integer correoDup = jdbcTemplate.query(
            "SELECT 1 FROM medico_veterinario WHERE LOWER(correo)=LOWER(?) LIMIT 1",
            ps -> ps.setString(1, request.correo().toLowerCase()),
            rs -> rs.next() ? 1 : 0
        );
        if (correoDup != null && correoDup == 1)
            throw new ExcepcionValidacion("El correo ya está asociado a un veterinario (legacy).");

        Integer tarjetaDup = jdbcTemplate.query(
            "SELECT 1 FROM medico_veterinario WHERE tarjeta_profesional_mv=? LIMIT 1",
            ps -> ps.setString(1, request.tarjetaProfesional()),
            rs -> rs.next() ? 1 : 0
        );
        if (tarjetaDup != null && tarjetaDup == 1)
            throw new ExcepcionValidacion("La tarjeta profesional ya está registrada (legacy).");

        String correoLower = request.correo().toLowerCase();
        if (userAccountRepository.existsByEmailIgnoreCase(correoLower)) {
            throw new ExcepcionValidacion("El correo ya está asociado a una cuenta de usuario.");
        }

        String fullName = request.nombreCompleto() == null ? "" : request.nombreCompleto().trim();
        String nombre = fullName;
        String apellido = "";
        if (!fullName.isEmpty() && fullName.contains(" ")) {
            int idx = fullName.lastIndexOf(' ');
            nombre = fullName.substring(0, idx);
            apellido = fullName.substring(idx + 1);
        }

        // Crear cuenta de usuario y entidad Veterinarian (nuevo modelo)
        CuentaUsuario account = CuentaUsuario.builder()
            .fullName(fullName.isEmpty() ? request.nombreCompleto() : fullName)
            .email(correoLower)
            .password(passwordEncoder.encode(request.password()))
            .securityQuestion(request.securityQuestion())
            .securityAnswerHash(passwordEncoder.encode(request.securityAnswer()))
            .role(Rol.VETERINARIO)
            .status(EstadoCuenta.ACTIVE)
            .passwordChangeRequired(false)
            .passwordUpdatedAt(java.time.LocalDateTime.now())
            .build();
        account = userAccountRepository.save(account);

        Veterinarian vetEntity = Veterinarian.builder()
            .userAccount(account)
            .professionalLicense(request.tarjetaProfesional())
            .speciality(request.especialidad())
            .yearsExperience(request.aniosExperiencia())
            .phoneNumber(request.telefono())
            .status(EstadoCuenta.ACTIVE)
            .build();
        veterinarianRepository.save(vetEntity);

        String estadoEs = toEstadoEs(EstadoCuenta.ACTIVE);
        final String nombreFinal = nombre;
        final String apellidoFinal = apellido;
        org.springframework.jdbc.support.GeneratedKeyHolder kh = new org.springframework.jdbc.support.GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            java.sql.PreparedStatement ps = con.prepareStatement(
                "INSERT INTO medico_veterinario (nombre_mv, apell_mv, telefono, especialidad, tarjeta_profesional_mv, correo, estado, cedu_mv) VALUES (?,?,?,?,?,?,?,?)",
                java.sql.Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, nombreFinal);
            ps.setString(2, apellidoFinal);
            ps.setString(3, request.telefono());
            ps.setString(4, request.especialidad());
            ps.setString(5, request.tarjetaProfesional());
            ps.setString(6, request.correo().toLowerCase());
            ps.setString(7, estadoEs);
            // Persistir cédula (requerida en la tabla legacy)
            ps.setString(8, request.cedula());
            return ps;
        }, kh);

        Long id = null;
        try { id = kh.getKey() == null ? null : kh.getKey().longValue(); } catch (Exception ignore) { id = null; }
        if (id == null) {
            id = jdbcTemplate.query(
                "SELECT id_mv FROM medico_veterinario WHERE LOWER(correo)=LOWER(?) ORDER BY id_mv DESC LIMIT 1",
                ps -> ps.setString(1, request.correo().toLowerCase()),
                rs -> rs.next() ? rs.getLong(1) : null
            );
        }

        return new RespuestaVeterinario(
            id,
            fullName,
            request.correo().toLowerCase(),
            request.telefono(),
            request.tarjetaProfesional(),
            request.especialidad(),
            request.aniosExperiencia(),
            request.cedula(),
            EstadoCuenta.ACTIVE,
            account.getSecurityQuestion()
        );
    }

    @Transactional(readOnly = true)
    public List<RespuestaVeterinario> findAll() {
        String sql = "SELECT mv.id_mv, mv.nombre_mv, mv.apell_mv, mv.telefono, mv.especialidad, mv.tarjeta_profesional_mv, mv.correo, mv.cedu_mv, mv.estado FROM medico_veterinario mv";
        return jdbcTemplate.query(sql, (rs, i) -> new RespuestaVeterinario(
            rs.getLong("id_mv"),
            (rs.getString("nombre_mv") + " " + rs.getString("apell_mv")).trim(),
            rs.getString("correo"),
            rs.getString("telefono"),
            rs.getString("tarjeta_profesional_mv"),
            rs.getString("especialidad"),
            null,
            rs.getString("cedu_mv"),
            mapEstadoEs(rs.getString("estado")),
            null
        ));
    }

    @Transactional
    public RespuestaVeterinario update(Long id, SolicitudActualizacionVeterinario request) {
        String nombre = request.nombreCompleto();
        String apellido = "";
        if (nombre != null && nombre.trim().contains(" ")) {
            int idx = nombre.trim().lastIndexOf(' ');
            apellido = nombre.trim().substring(idx + 1);
            nombre = nombre.trim().substring(0, idx);
        }
        jdbcTemplate.update(
            "UPDATE medico_veterinario SET nombre_mv=?, apell_mv=?, telefono=?, especialidad=?, correo=? WHERE id_mv=?",
            nombre, apellido, request.telefono(), request.especialidad(), request.correo().toLowerCase(), id
        );

        // Sincronizar datos básicos y seguridad con CuentaUsuario y entidad Veterinarian.
        // Si la cuenta no existe (veterinarios legacy), se crea y se vincula.
        jdbcTemplate.query(
            "SELECT nombre_mv, apell_mv, telefono, especialidad, tarjeta_profesional_mv, correo, estado FROM medico_veterinario WHERE id_mv=?",
            ps -> ps.setLong(1, id),
            rs -> {
                if (rs.next()) {
                    String nombreLegacy = rs.getString("nombre_mv");
                    String apellidoLegacy = rs.getString("apell_mv");
                    String telefonoLegacy = rs.getString("telefono");
                    String especialidadLegacy = rs.getString("especialidad");
                    String tarjeta = rs.getString("tarjeta_profesional_mv");
                    String correo = rs.getString("correo");
                    EstadoCuenta estadoCuenta = mapEstadoEs(rs.getString("estado"));

                    String correoLower = correo == null ? null : correo.toLowerCase();

                    // 1. Obtener o crear la CuentaUsuario asociada para el veterinario
                    CuentaUsuario account = null;
                    if (correoLower != null) {
                        account = userAccountRepository.findByEmailIgnoreCase(correoLower)
                            .filter(u -> u.getRol() == Rol.VETERINARIO)
                            .orElse(null);
                    }

                    if (account == null && correoLower != null && tarjeta != null) {
                        String fullNameLegacy = ((nombreLegacy == null ? "" : nombreLegacy) + " " + (apellidoLegacy == null ? "" : apellidoLegacy)).trim();
                        if (fullNameLegacy.isEmpty()) {
                            fullNameLegacy = correoLower;
                        }

                        account = CuentaUsuario.builder()
                            .fullName(fullNameLegacy)
                            .email(correoLower)
                            .password(null)
                            .securityQuestion(null)
                            .securityAnswerHash(null)
                            .role(Rol.VETERINARIO)
                            .status(estadoCuenta)
                            .passwordChangeRequired(false)
                            .build();
                        account = userAccountRepository.save(account);

                        // Crear entidad Veterinarian si aún no existe (legacy)
                        boolean tieneVet = veterinarianRepository.findByProfessionalLicense(tarjeta).isPresent()
                            || veterinarianRepository.findByUserAccountId(account.getId()).isPresent();
                        if (!tieneVet) {
                            Veterinarian vetEntity = Veterinarian.builder()
                                .userAccount(account)
                                .professionalLicense(tarjeta)
                                .speciality(especialidadLegacy)
                                .yearsExperience(null)
                                .phoneNumber(telefonoLegacy)
                                .status(estadoCuenta)
                                .build();
                            veterinarianRepository.save(vetEntity);
                        }
                    }

                    if (account != null) {
                        // Actualizar datos básicos y de seguridad en user_accounts
                        if (request.nombreCompleto() != null && !request.nombreCompleto().isBlank()) {
                            account.setFullName(request.nombreCompleto().trim());
                        }
                        if (request.correo() != null && !request.correo().isBlank()) {
                            account.setEmail(request.correo().toLowerCase());
                        }
                        if (request.securityQuestion() != null && !request.securityQuestion().isBlank()) {
                            account.setSecurityQuestion(request.securityQuestion().trim());
                        }
                        if (request.securityAnswer() != null && !request.securityAnswer().isBlank()) {
                            account.setSecurityAnswerHash(passwordEncoder.encode(request.securityAnswer().trim()));
                        }
                        userAccountRepository.save(account);
                    }

                    // 2. Sincronizar datos básicos con entidad Veterinarian (si existe)
                    if (tarjeta != null) {
                        veterinarianRepository.findByProfessionalLicense(tarjeta)
                            .ifPresent(vet -> {
                                if (request.telefono() != null) {
                                    vet.setPhoneNumber(request.telefono());
                                }
                                if (request.especialidad() != null) {
                                    vet.setSpeciality(request.especialidad());
                                }
                                veterinarianRepository.save(vet);
                            });
                    }
                }
                return null;
            }
        );

        return getById(id);
    }

    @Transactional
    public RespuestaVeterinario updateStatus(Long id, EstadoCuenta status) {
        jdbcTemplate.update("UPDATE medico_veterinario SET estado=? WHERE id_mv=?", toEstadoEs(status), id);

        // Sincronizar estado con cuenta de usuario y entidad Veterinarian (si existen)
        jdbcTemplate.query(
            "SELECT correo, tarjeta_profesional_mv FROM medico_veterinario WHERE id_mv=?",
            ps -> ps.setLong(1, id),
            rs -> {
                if (rs.next()) {
                    String correo = rs.getString("correo");
                    String tarjeta = rs.getString("tarjeta_profesional_mv");
                    if (correo != null) {
                        userAccountRepository.findByEmailIgnoreCase(correo)
                            .filter(u -> u.getRol() == Rol.VETERINARIO)
                            .ifPresent(u -> {
                                u.setStatus(status);
                                userAccountRepository.save(u);
                            });
                    }
                    if (tarjeta != null) {
                        veterinarianRepository.findByProfessionalLicense(tarjeta)
                            .ifPresent(v -> {
                                v.setStatus(status);
                                veterinarianRepository.save(v);
                            });
                    }
                }
                return null;
            }
        );

        return getById(id);
    }

    @Transactional(readOnly = true)
    public RespuestaVeterinario getById(Long id) {
        String sql = "SELECT mv.id_mv, mv.nombre_mv, mv.apell_mv, mv.telefono, mv.especialidad, mv.tarjeta_profesional_mv, mv.correo, mv.cedu_mv, mv.estado FROM medico_veterinario mv WHERE mv.id_mv=?";
        return jdbcTemplate.query(sql, ps -> ps.setLong(1, id), rs -> {
            if (rs.next()) {
                String correo = rs.getString("correo");
                String securityQuestion = null;
                if (correo != null) {
                    securityQuestion = userAccountRepository.findByEmailIgnoreCase(correo)
                        .filter(a -> a.getRol() == Rol.VETERINARIO)
                        .map(CuentaUsuario::getSecurityQuestion)
                        .orElse(null);
                }
                return new RespuestaVeterinario(
                    rs.getLong("id_mv"),
                    (rs.getString("nombre_mv") + " " + rs.getString("apell_mv")).trim(),
                    correo,
                    rs.getString("telefono"),
                    rs.getString("tarjeta_profesional_mv"),
                    rs.getString("especialidad"),
                    null,
                    rs.getString("cedu_mv"),
                    mapEstadoEs(rs.getString("estado")),
                    securityQuestion
                );
            }
            throw new ExcepcionRecursoNoEncontrado("Veterinario no encontrado");
        });
    }

    /**
     * Asegura que exista una CuentaUsuario y una entidad Veterinarian asociadas
     * a un registro legacy de la tabla medico_veterinario, usando las credenciales
     * ingresadas en el formulario de login.
     */
    @Transactional
    public CuentaUsuario asegurarCuentaParaLoginDesdeLegacy(SolicitudInicioSesionVeterinario request) {
        String correoLower = request.correo().toLowerCase();

        Optional<CuentaUsuario> existing = userAccountRepository.findByEmailIgnoreCase(correoLower)
            .filter(a -> a.getRol() == Rol.VETERINARIO);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Buscar en la tabla legacy por correo y tarjeta profesional
        String sql = """
            SELECT nombre_mv, apell_mv, telefono, especialidad, tarjeta_profesional_mv, correo, estado, cedu_mv
            FROM medico_veterinario
            WHERE LOWER(correo)=LOWER(?) AND tarjeta_profesional_mv=?
            """;

        return jdbcTemplate.query(sql,
            ps -> {
                ps.setString(1, correoLower);
                ps.setString(2, request.tarjetaProfesional());
            },
            rs -> {
                if (!rs.next()) {
                    throw new ExcepcionValidacion("Credenciales inválidas.");
                }

                EstadoCuenta estadoCuenta = mapEstadoEs(rs.getString("estado"));
                if (estadoCuenta != EstadoCuenta.ACTIVE) {
                    throw new ExcepcionValidacion("La cuenta del veterinario se encuentra inactiva.");
                }

                String nombre = rs.getString("nombre_mv");
                String apellido = rs.getString("apell_mv");
                String fullNameLegacy = ((nombre == null ? "" : nombre) + " " + (apellido == null ? "" : apellido)).trim();
                if (fullNameLegacy.isEmpty()) {
                    fullNameLegacy = correoLower;
                }
                String telefono = rs.getString("telefono");
                String especialidad = rs.getString("especialidad");

                // Validar la contraseña ingresada con la política actual
                passwordPolicyService.validateOrThrow(request.password());

                CuentaUsuario account = CuentaUsuario.builder()
                    .fullName(fullNameLegacy)
                    .email(correoLower)
                    .password(passwordEncoder.encode(request.password()))
                    .role(Rol.VETERINARIO)
                    .status(estadoCuenta)
                    .passwordChangeRequired(false)
                    .build();
                account = userAccountRepository.save(account);

                Veterinarian vetEntity = Veterinarian.builder()
                    .userAccount(account)
                    .professionalLicense(request.tarjetaProfesional())
                    .speciality(especialidad)
                    .yearsExperience(null)
                    .phoneNumber(telefono)
                    .status(estadoCuenta)
                    .build();
                veterinarianRepository.save(vetEntity);

                return account;
            });
    }

    private EstadoCuenta mapEstadoEs(String valor) {
        if (valor == null) return EstadoCuenta.ACTIVE;
        String v = valor.trim();
        if (v.equalsIgnoreCase("Activo") || v.equalsIgnoreCase("ACTIVE")) return EstadoCuenta.ACTIVE;
        if (v.equalsIgnoreCase("Inactivo") || v.equalsIgnoreCase("INACTIVE")) return EstadoCuenta.INACTIVE;
        if (v.equalsIgnoreCase("Pendiente") || v.equalsIgnoreCase("PENDING")) return EstadoCuenta.PENDING;
        try { return EstadoCuenta.valueOf(v.toUpperCase()); } catch (Exception ignore) { return EstadoCuenta.ACTIVE; }
    }

    private String toEstadoEs(EstadoCuenta estado) {
        if (estado == null) return "Activo";
        return switch (estado) {
            case ACTIVE -> "Activo";
            case INACTIVE -> "Inactivo";
            case PENDING -> "Pendiente";
        };
    }
}
