package com.orangehearth.OrangeHearth.service;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orangehearth.OrangeHearth.dto.request.SolicitudActualizacionVeterinario;
import com.orangehearth.OrangeHearth.dto.request.SolicitudCreacionVeterinario;
import com.orangehearth.OrangeHearth.dto.response.RespuestaVeterinario;
import com.orangehearth.OrangeHearth.exception.ExcepcionRecursoNoEncontrado;
import com.orangehearth.OrangeHearth.exception.ExcepcionValidacion;
import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;

@Service
public class ServicioVeterinarios {

    private final ServicioPoliticaContrasena passwordPolicyService;
    private final JdbcTemplate jdbcTemplate;

    public ServicioVeterinarios(ServicioPoliticaContrasena passwordPolicyService, JdbcTemplate jdbcTemplate) {
        this.passwordPolicyService = passwordPolicyService;
        this.jdbcTemplate = jdbcTemplate;
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

        String fullName = request.nombreCompleto() == null ? "" : request.nombreCompleto().trim();
        String nombre = fullName;
        String apellido = "";
        if (!fullName.isEmpty() && fullName.contains(" ")) {
            int idx = fullName.lastIndexOf(' ');
            nombre = fullName.substring(0, idx);
            apellido = fullName.substring(idx + 1);
        }

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
            EstadoCuenta.ACTIVE
        );
    }

    @Transactional(readOnly = true)
    public List<RespuestaVeterinario> findAll() {
        String sql = "SELECT mv.id_mv, mv.nombre_mv, mv.apell_mv, mv.telefono, mv.especialidad, mv.tarjeta_profesional_mv, mv.correo, mv.estado FROM medico_veterinario mv";
        return jdbcTemplate.query(sql, (rs, i) -> new RespuestaVeterinario(
            rs.getLong("id_mv"),
            (rs.getString("nombre_mv") + " " + rs.getString("apell_mv")).trim(),
            rs.getString("correo"),
            rs.getString("telefono"),
            rs.getString("tarjeta_profesional_mv"),
            rs.getString("especialidad"),
            null,
            mapEstadoEs(rs.getString("estado"))
        ));
    }

    @Transactional
    public RespuestaVeterinario update(Long id, SolicitudActualizacionVeterinario request) {
        Integer dup = jdbcTemplate.query(
            "SELECT 1 FROM medico_veterinario WHERE tarjeta_profesional_mv=? AND id_mv<>? LIMIT 1",
            ps -> { ps.setString(1, request.tarjetaProfesional()); ps.setLong(2, id); },
            rs -> rs.next() ? 1 : 0
        );
        if (dup != null && dup == 1) throw new ExcepcionValidacion("La tarjeta profesional ya está registrada.");

        String nombre = request.nombreCompleto();
        String apellido = "";
        if (nombre != null && nombre.trim().contains(" ")) {
            int idx = nombre.trim().lastIndexOf(' ');
            apellido = nombre.trim().substring(idx + 1);
            nombre = nombre.trim().substring(0, idx);
        }
        jdbcTemplate.update(
            "UPDATE medico_veterinario SET nombre_mv=?, apell_mv=?, telefono=?, especialidad=?, tarjeta_profesional_mv=?, correo=? WHERE id_mv=?",
            nombre, apellido, request.telefono(), request.especialidad(), request.tarjetaProfesional(), request.correo().toLowerCase(), id
        );
        return getById(id);
    }

    @Transactional
    public RespuestaVeterinario updateStatus(Long id, EstadoCuenta status) {
        jdbcTemplate.update("UPDATE medico_veterinario SET estado=? WHERE id_mv=?", toEstadoEs(status), id);
        return getById(id);
    }

    @Transactional(readOnly = true)
    public RespuestaVeterinario getById(Long id) {
        String sql = "SELECT mv.id_mv, mv.nombre_mv, mv.apell_mv, mv.telefono, mv.especialidad, mv.tarjeta_profesional_mv, mv.correo, mv.estado FROM medico_veterinario mv WHERE mv.id_mv=?";
        return jdbcTemplate.query(sql, ps -> ps.setLong(1, id), rs -> {
            if (rs.next()) {
                return new RespuestaVeterinario(
                    rs.getLong("id_mv"),
                    (rs.getString("nombre_mv") + " " + rs.getString("apell_mv")).trim(),
                    rs.getString("correo"),
                    rs.getString("telefono"),
                    rs.getString("tarjeta_profesional_mv"),
                    rs.getString("especialidad"),
                    null,
                    mapEstadoEs(rs.getString("estado"))
                );
            }
            throw new ExcepcionRecursoNoEncontrado("Veterinario no encontrado");
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
