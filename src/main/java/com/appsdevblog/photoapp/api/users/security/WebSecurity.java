package com.appsdevblog.photoapp.api.users.security;

import com.appsdevblog.photoapp.api.users.service.UsersService;
import com.appsdevblog.photoapp.api.users.service.UsersServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UsersService usersService;
	private Environment env;
	
	public WebSecurity (Environment env, UsersServiceImpl usersService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.env = env;
		this.usersService = usersService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		// AuthenticationManager: el componente central de Spring Security para validar usuarios.
		AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		
		// Configuracion de UserDetailsService de Spring Security
		// Se indica clase que implementa el servicio de usuarios (metodo loadUserByUsername)
		// y el algoritmo de encriptacion de contraseñas a usar.
		authManagerBuilder.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);


		AuthenticationManager authenticationManager = authManagerBuilder.build();

		// Obtenemos la variable de entorno con la direccion ip del API Gateway
		String ipApiGateway = env.getProperty("gateway.ip");
		
		System.out.println("ipApiGateway: " + ipApiGateway);
		
		// Deshabilitar proteccion CSRF 
		http.csrf((csrf) -> csrf.disable());
		
		// Paths
		AntPathRequestMatcher pathUsers = new AntPathRequestMatcher("/users", "POST");
		AntPathRequestMatcher pathH2Console = new AntPathRequestMatcher("/h2-console/**");
		
		// Filtros
		AuthenticationFilter authFilter = new AuthenticationFilter(authenticationManager, env, usersService);
		// Indicamos el path de login para el filtro de autenticacion de la aplicacion
		authFilter.setFilterProcessesUrl(env.getProperty("login.url.path"));

		// Creamos WebAuth para la IP del servicio API Gateway
		String expressionWebAuthApiGateway = "hasIpAddress('" + ipApiGateway + "')"; 
		WebExpressionAuthorizationManager webAuthApiGateway = new WebExpressionAuthorizationManager(expressionWebAuthApiGateway);
		
		// Permitir todos los accesos (no autenticar) al path H2Console
		// Accesos al path Users unicamente permitidos desde la ip del API Gateway 
		http.authorizeHttpRequests( (authz) -> authz.requestMatchers(pathUsers).permitAll()
		//http.authorizeHttpRequests( (authz) -> authz.requestMatchers(pathUsers).access(webAuthApiGateway)
													.requestMatchers(pathH2Console).permitAll() )
				.addFilter(authFilter)
				.authenticationManager(authenticationManager)
				// Configurar politica de sesion como STATELESS: no se crea ni mantiene ninguna sesion en el servidor.
				.sessionManagement( (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) );
		
		// Permitir que H2-console se muestre en un iframe y que no sea bloqueada por Security
		http.headers( (headers) -> headers.frameOptions( (frameOptions) -> frameOptions.sameOrigin() ) );
		
		// Construccion del filtro de seguridad
		SecurityFilterChain sfc = http.build();
		
		return sfc;
		
	}
	
}
