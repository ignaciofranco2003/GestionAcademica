package com.poo.GestionAcademica.WebConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.poo.GestionAcademica.APILOGIN.LoginAPI;

@Configuration
@EnableWebSecurity
public class SecConf extends WebSecurityConfigurerAdapter {

    private final LoginAPI loginAPI;

    @Autowired
    private AccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    public SecConf(LoginAPI loginAPI) {
        this.loginAPI = loginAPI;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new CustomAuthenticationProvider(loginAPI));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests(requests -> requests
                .antMatchers(HttpMethod.POST).permitAll()
                .antMatchers(HttpMethod.DELETE).permitAll()
                .antMatchers( "/","/estudiantes/**", "/cursos/**").hasRole("ADMIN")
                .antMatchers("/courses/**","/students/**").permitAll())
                .formLogin(login -> login
                        .permitAll())
                .logout(logout -> logout
                        .permitAll()
                        .logoutUrl("/logout") // URL para cerrar sesión
                        .logoutSuccessUrl("/login") // Redirigir después de cerrar sesión
                        .invalidateHttpSession(true) // Invalidar la sesión HTTP
                        .deleteCookies()// Eliminar las cookies, si es necesario
                        .clearAuthentication(true) // Limpiar la autenticación actual
                        .permitAll())
                .exceptionHandling( exceptionHandling -> exceptionHandling
                    .accessDeniedHandler(customAccessDeniedHandler));
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/courses/*/enroll/*"); // La direccion de enroll con metodos post y delete
                                                                                          // ignora las autorizaciones anteriores
    }
}
