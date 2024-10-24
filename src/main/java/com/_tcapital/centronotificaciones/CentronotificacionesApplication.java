package com._tcapital.centronotificaciones;

import com._tcapital.centronotificaciones.application.ProcesamientoServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class CentronotificacionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CentronotificacionesApplication.class, args);
	}

	@Bean
	CommandLineRunner run(ApplicationContext context) {
		return args -> {
			ProcesamientoServiceImpl procesamientoService =
					context.getBean(ProcesamientoServiceImpl.class);
			procesamientoService.procesarArchivosCorreo();
		};
	}

}
