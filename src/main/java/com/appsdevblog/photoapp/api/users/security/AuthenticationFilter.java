package com.appsdevblog.photoapp.api.users.security;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.appsdevblog.photoapp.api.users.service.UsersService;
import com.appsdevblog.photoapp.api.users.shared.UserDto;
import com.appsdevblog.photoapp.api.users.ui.model.LoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Esta clase extiende UsernamePasswordAuthenticationFilter, lo que significa que esta interceptando peticiones al endpoint /login por defecto, 
 * y procesando la autenticacion personalizada.
 * 
 * 1. Recibe login con email y password en JSON.
 * 2. Usa Spring Security para verificar las credenciales.
 * 3. Si son válidas:
 *    + Busca detalles adicionales del usuario.
 *    + Genera un token JWT con userId como subject y con una clave generada con el algoritmo HMAC.
 *    + Devuelve el token en la cabecera HTTP.
 * 
 */
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private UsersService usersService;
	private Environment env;

	// Constructor
	// AuthenticationManager: el componente central de Spring Security para validar usuarios.
	public AuthenticationFilter(AuthenticationManager authenticationManager, Environment env, UsersService usersService) {
		super(authenticationManager);
		this.env = env;	
		this.usersService = usersService;
	}

	// Este metodo se ejecuta automaticamente cuando el usuario intenta hacer un login 
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
		
		try {
			
			// Cargamos nuestra clase del modelo LoginRequestModel
			// Las credenciales las mapeamos de la peticion de entrada a la entidad LoginRequestModel (mail + password)
			LoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(), LoginRequestModel.class);
			
			// Nos creamos un token de autenticacion de usuario/password a partir de mail + password de la peticion de entrada
			UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken( creds.getEmail(), creds.getPassword(), new ArrayList<>() );
			
			// Autenticamos el token con el manager de Autenticacion 
			// Si las credenciales son correctas, retorna una Authentication valida.
			Authentication auth = getAuthenticationManager().authenticate(upat);
			
			return auth;
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	// Metodo invocado si la Autenticacion del login es exitosa 
	// Si la autenticacion es exitosa, se genera un Token JWT 
	// Se agrega el token y el userId asociado a la respuesta HTTP
	@Override
	protected void successfulAuthentication (HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) 
			throws IOException, ServletException {
	
		// Se obtiene el usuario autenticado
		User user = (User) auth.getPrincipal();			

		// En la app se esta utilizando como Username el email del usuario
		String userNameMail = user.getUsername();

		UserDto userDto = usersService.getUserDetailsByEmail(userNameMail);
		String userId = userDto.getUserId();

		// Recuperamos la propiedad del token.secret del archivo application.properties
		// Se utiliza el algoritmo HMAC SHA-256 para firmar el token
		String tokenSecret = env.getProperty("token.secret");
		byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
		SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);		

		Instant now = Instant.now();
		Long millisExpirationTime = Long.parseLong(env.getProperty("token.expiration_time"));
		Date dateExp = Date.from(now.plusMillis(millisExpirationTime));		
		Date dateIss = Date.from(now);

		JwtBuilder tokenBuilder = Jwts.builder();	
		
		// Se construye el token JWT con los datos del usuario y la fecha de expiracion
		// Se firma el token con la clave secreta
		String tokenJwt = tokenBuilder.subject(userId)
				.expiration(dateExp)
				.issuedAt(dateIss)
				.signWith(secretKey)
				.compact();

		// Se agrega el token y el userId a la respuesta HTTP
		res.addHeader("token", tokenJwt);
		res.addHeader("userId", userId);

	}
	
	
}
