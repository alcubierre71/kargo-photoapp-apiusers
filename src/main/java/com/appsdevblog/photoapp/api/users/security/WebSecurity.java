package com.appsdevblog.photoapp.api.users.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurity {

	private Environment env;
	
	public WebSecurity (Environment env) {
		this.env = env;
	}
	
	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		// Obtenemos la variable de entorno con la direccion ip del API Gateway
		String ipApiGateway = env.getProperty("gateway.ip");
		
		System.out.println("ipApiGateway: " + ipApiGateway);
		
		// Deshabilitar proteccion CSRF 
		http.csrf((csrf) -> csrf.disable());
		
		AntPathRequestMatcher pathUsers = new AntPathRequestMatcher("/users");
		AntPathRequestMatcher pathH2Console = new AntPathRequestMatcher("/h2-console/**");
		
		// Creamos WebAuth para la IP del servicio API Gateway
		String expressionWebAuthApiGateway = "hasIpAddress('" + ipApiGateway + "')"; 
		WebExpressionAuthorizationManager webAuthApiGateway = new WebExpressionAuthorizationManager(expressionWebAuthApiGateway);
		
		// Permitir todos los accesos (no autenticar) al path H2Console
		// Accesos al path Users unicamente permitidos desde la ip del API Gateway 
		//http.authorizeHttpRequests( (authz) -> authz.requestMatchers(pathUsers).permitAll()
		http.authorizeHttpRequests( (authz) -> authz.requestMatchers(pathUsers).access(webAuthApiGateway)
													.requestMatchers(pathH2Console).permitAll() )
				// Configurar politica de sesion como STATELESS: no se crea ni mantiene ninguna sesion en el servidor.
				.sessionManagement( (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) );
		
		// Permitir que H2-console se muestre en un iframe y que no sea bloqueada por Security
		http.headers( (headers) -> headers.frameOptions( (frameOptions) -> frameOptions.sameOrigin() ) );
		
		// Construccion del filtro de seguridad
		SecurityFilterChain sfc = http.build();
		
		return sfc;
		
	}
	
}
