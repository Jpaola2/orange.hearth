package com.orangehearth.OrangeHearth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfiguracionWeb implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Página de inicio
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");

        // Autenticación por rol
        registry.addViewController("/login-rol").setViewName("login-rol");
        registry.addViewController("/login-rol.html").setViewName("login-rol");

        // Registro de usuario (tutor)
        registry.addViewController("/registro").setViewName("registro");
        registry.addViewController("/registro.html").setViewName("registro");

        // Otras vistas usadas en el frontend (dashboards y páginas auxiliares)
        registry.addViewController("/dashboardVeterinario").setViewName("dashboardVeterinario");
        registry.addViewController("/dashboardVeterinario.html").setViewName("dashboardVeterinario");

        registry.addViewController("/AgendaMedica").setViewName("AgendaMedica");
        registry.addViewController("/AgendaMedica.html").setViewName("AgendaMedica");

        registry.addViewController("/AgendaCitaTutor").setViewName("AgendaCitaTutor");
        registry.addViewController("/AgendaCitaTutor.html").setViewName("AgendaCitaTutor");

        registry.addViewController("/usuario").setViewName("usuario");
        registry.addViewController("/usuario.html").setViewName("usuario");

        registry.addViewController("/veterinario").setViewName("veterinario");
        registry.addViewController("/veterinario.html").setViewName("veterinario");

        registry.addViewController("/registro_mascota").setViewName("registro_mascota");
        registry.addViewController("/registro_mascota.html").setViewName("registro_mascota");

        registry.addViewController("/index1").setViewName("index1");
        registry.addViewController("/index1.html").setViewName("index1");

        registry.addViewController("/test_conexion").setViewName("test_conexion");
        registry.addViewController("/test_conexion.html").setViewName("test_conexion");
    }
}
