package com.andrei.licenta;

import com.andrei.licenta.constants.FileConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

@EnableAsync
@SpringBootApplication
public class LicentaApplication {

	public static void main(String[] args) {
		SpringApplication.run(LicentaApplication.class, args);
		//noinspection ResultOfMethodCallIgnored
		new File(FileConstant.ANUNT_FOLDER).mkdirs();

	}

	@Bean
	public CorsFilter corsFilter() {

		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
				"Accept", "Origin, Accept", "X-Requested-With",
				"Access-Control-Request-Method", "Access-Control-Request-Headers","Set-Cookie","Cookie"));
		corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Jwt-Token", "Authorization",
				"Access-Control-Allow-Origin", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials","Set-Cookie","Cookie"));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH"));
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

		return new CorsFilter(urlBasedCorsConfigurationSource);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}
}
