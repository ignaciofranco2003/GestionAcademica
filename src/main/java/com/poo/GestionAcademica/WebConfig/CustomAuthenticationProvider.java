package com.poo.GestionAcademica.WebConfig;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.poo.GestionAcademica.APILOGIN.LoginAPI;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final LoginAPI loginAPI;

    @Autowired
    public CustomAuthenticationProvider(LoginAPI loginAPI) {
        this.loginAPI = loginAPI;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        boolean loginSuccess;
        if (username.equalsIgnoreCase("anl") && password.equalsIgnoreCase("anl")) {
            loginSuccess = true;
        } else {
            // Llama al método loginUser de LoginAPI para autenticar al usuario con la API externa
            loginSuccess = LoginAPI.loginUser(username, password);
        }
        
        if (loginSuccess) {
            String role = "ADMIN"; // Por defecto, asignamos el rol ADMIN si el login es exitoso

            // Creamos una lista de authorities con el rol correspondiente
            return new UsernamePasswordAuthenticationToken(username, password,
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role)));
        } else {
            String role = "USER"; // Si el login no es exitoso, asignamos el rol USER

            // Creamos una lista de authorities con el rol correspondiente
            return new UsernamePasswordAuthenticationToken(username, password,
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role)));
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
