package com.orangehearth.OrangeHearth.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.orangehearth.OrangeHearth.dto.response.RespuestaTutor;
import com.orangehearth.OrangeHearth.dto.response.RespuestaMascota;
import com.orangehearth.OrangeHearth.dto.response.RespuestaVeterinario;
import com.orangehearth.OrangeHearth.model.enums.EstadoCuenta;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicioDatosLegacy {

    private final JdbcTemplate jdbcTemplate;

    public List<RespuestaTutor> listarTutoresLegacy() {
        // Usar la columna 'estado' recientemente agregada en la tabla legacy (valores: 'Activo','Inactivo','Pendiente')
        String sqlTutores = "SELECT t.id_tutor, t.nomb_tutor, t.apell_tutor, t.tel_tutor, t.correo_tutor, t.ced_tutor, t.direc_tutor, t.estado AS estado FROM tutor t";
        List<Map<String,Object>> tutores = jdbcTemplate.queryForList(sqlTutores);

        // Cargar todas las mascotas y agrupar por tutor
        String sqlMasc = "SELECT id_masc, nom_masc, espe_masc, gene_masc, edad_masc, unidad_edad, id_tutor FROM mascota";
        Map<Long, List<RespuestaMascota>> mascotasPorTutor = jdbcTemplate.query(sqlMasc, (rs) -> {
            Map<Long, List<RespuestaMascota>> map = tutores.stream()
                .collect(Collectors.toMap(r -> ((Number)r.get("id_tutor")).longValue(), r -> new java.util.ArrayList<RespuestaMascota>()));
            while (rs.next()) {
                Long tutorId = ((Number)rs.getObject("id_tutor")).longValue();
                RespuestaMascota m = new RespuestaMascota(
                    ((Number)rs.getObject("id_masc")).longValue(),
                    rs.getString("nom_masc"),
                    rs.getString("espe_masc"),
                    "", // raza no disponible en legado; se deja vacío
                    rs.getObject("edad_masc") == null ? null : ((Number)rs.getObject("edad_masc")).intValue(),
                    rs.getString("unidad_edad")
                );
                map.computeIfAbsent(tutorId, k -> new java.util.ArrayList<>()).add(m);
            }
            return map;
        });

        return tutores.stream().map(row -> {
            Long id = ((Number)row.get("id_tutor")).longValue();
            List<RespuestaMascota> mascotas = mascotasPorTutor.getOrDefault(id, List.of());
            com.orangehearth.OrangeHearth.model.enums.EstadoCuenta estado = mapEstadoEs(String.valueOf(row.get("estado")));
            return new RespuestaTutor(
                id,
                ((row.get("nomb_tutor")+" "+row.get("apell_tutor")).trim()),
                (String)row.get("correo_tutor"),
                (String)row.get("tel_tutor"),
                "CC " + (String)row.get("ced_tutor"),
                (String)row.get("direc_tutor"),
                mascotas,
                estado
            );
        }).toList();
    }

    public List<RespuestaVeterinario> listarVeterinariosLegacy() {
        // Usar la columna 'estado' recientemente agregada (valores: 'Activo','Inactivo','Pendiente')
        String sql = "SELECT mv.id_mv, mv.nombre_mv, mv.apell_mv, mv.telefono, mv.especialidad, mv.tarjeta_profesional_mv, COALESCE(mv.correo, mv.correo) AS email, mv.estado AS estado FROM medico_veterinario mv";
        return jdbcTemplate.query(sql, (rs, i) -> new RespuestaVeterinario(
            rs.getLong("id_mv"),
            (rs.getString("nombre_mv") + " " + rs.getString("apell_mv")).trim(),
            rs.getString("email"),
            rs.getString("telefono"),
            rs.getString("tarjeta_profesional_mv"),
            rs.getString("especialidad"),
            null,
            mapEstadoEs(rs.getString("estado"))
        ));
    }

    public RespuestaVeterinario buscarVeterinarioLegacyPorId(Long id) {
        String sql = "SELECT mv.id_mv, mv.nombre_mv, mv.apell_mv, mv.telefono, mv.especialidad, mv.tarjeta_profesional_mv, COALESCE(mv.correo, ua.email) AS email, ua.status AS estado FROM medico_veterinario mv LEFT JOIN user_accounts ua ON ua.id = mv.user_id WHERE mv.id_mv=?";
        return jdbcTemplate.query(sql, ps -> ps.setLong(1, id), rs -> {
            if (rs.next()) {
                return new RespuestaVeterinario(
                    rs.getLong("id_mv"),
                    (rs.getString("nombre_mv") + " " + rs.getString("apell_mv")).trim(),
                    rs.getString("email"),
                    rs.getString("telefono"),
                    rs.getString("tarjeta_profesional_mv"),
                    rs.getString("especialidad"),
                    null,
                    mapEstadoEs(rs.getString("estado"))
                );
            }
            return null;
        });
    }

    public RespuestaVeterinario actualizarVeterinarioLegacy(Long id, String nombreCompleto, String correo, String telefono, String especialidad, String tarjeta, String cedula) {
        String nombre = nombreCompleto;
        String apellido = "";
        if (nombreCompleto != null && nombreCompleto.trim().contains(" ")) {
            int idx = nombreCompleto.trim().lastIndexOf(' ');
            nombre = nombreCompleto.trim().substring(0, idx);
            apellido = nombreCompleto.trim().substring(idx + 1);
        }
        jdbcTemplate.update(
            "UPDATE medico_veterinario SET nombre_mv=?, apell_mv=?, telefono=?, especialidad=?, tarjeta_profesional_mv=?, correo=?, cedu_mv=? WHERE id_mv=?",
            nombre, apellido, telefono, especialidad, tarjeta, correo, cedula, id
        );
        // Devolver registro actualizado
        return listarVeterinariosLegacy().stream().filter(v -> v.id().equals(id)).findFirst().orElse(null);
    }

    public RespuestaVeterinario actualizarEstadoVeterinarioLegacy(Long id, EstadoCuenta estado) {
        // Cambia el estado en user_accounts vía user_id asociado y en la tabla legacy
        Long userId = jdbcTemplate.query(
            "SELECT user_id FROM medico_veterinario WHERE id_mv=?",
            ps -> ps.setLong(1, id),
            rs -> rs.next() ? rs.getLong(1) : null
        );
        if (userId != null) {
            jdbcTemplate.update("UPDATE user_accounts SET status=? WHERE id=?", estado.name(), userId);
        }
        // Actualizar columna 'estado' (texto en español) en medico_veterinario
        jdbcTemplate.update("UPDATE medico_veterinario SET estado=? WHERE id_mv=?", toEstadoEs(estado), id);
        return listarVeterinariosLegacy().stream().filter(v -> v.id().equals(id)).findFirst().orElse(null);
    }

    public com.orangehearth.OrangeHearth.dto.response.RespuestaTutor actualizarEstadoTutorLegacy(Long idTutor, EstadoCuenta estado) {
        String correoTutor = jdbcTemplate.query(
            "SELECT correo_tutor FROM tutor WHERE id_tutor=?",
            ps -> ps.setLong(1, idTutor),
            rs -> rs.next() ? rs.getString(1) : null
        );
        if (correoTutor == null || correoTutor.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "El tutor no tiene correo en la tabla legacy"
            );
        }
        jdbcTemplate.update(
            "UPDATE user_accounts SET status=? WHERE LOWER(email)=LOWER(?)",
            estado.name(), correoTutor
        );
        // Actualizar columna 'estado' (texto en español) en tutor
        jdbcTemplate.update("UPDATE tutor SET estado=? WHERE LOWER(correo_tutor)=LOWER(?)", toEstadoEs(estado), correoTutor);
        return listarTutoresLegacy().stream().filter(t -> t.id().equals(idTutor)).findFirst().orElse(null);
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
