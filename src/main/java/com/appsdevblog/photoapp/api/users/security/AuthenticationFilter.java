package com.appsdevblog.photoapp.api.users.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.appsdevblog.photoapp.api.users.ui.model.LoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;

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
 *    + Genera un JWT con userId como subject.
 *    + Devuelve el token en la cabecera HTTP.
 * 
 */
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	// Constructor
	// AuthenticationManager: el componente central de Spring Security para validar usuarios.
	public AuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
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
	@Override
	protected void successfulAuthentication (HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) 
			throws IOException, ServletException {
		
	}
	
	
}
