package com.microservices.apis.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.microservices.apis.service.ImplementacaoUserDetailsService;

/*
Mapeia a URI 
*/
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{

    private final ImplementacaoUserDetailsService implementacaoUserDetailsService;

    public WebConfigSecurity(ImplementacaoUserDetailsService implementacaoUserDetailsService) {
        this.implementacaoUserDetailsService = implementacaoUserDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Configura as solicitações de acesso por http
        //Ativando a proteção contra usuario que não está validado por tokent

        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

        //Ativando a permissão para o acesso a pagina inicial do sistema EX: sistema.com.br/index

        .disable().authorizeRequests().antMatchers("/").permitAll()

        .antMatchers("/index").permitAll()

        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

        //URL de Logout - Redireciona apos o user deslogar do sistema
        .anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")

        //Mapeia URL de Logout e invalida o usuario

        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))

        //Filtra requisições de login para

        .and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
                         UsernamePasswordAuthenticationFilter.class)
         .cors().and().csrf().disable()


        //Filtra demais requisições para verificar a presença do TOKEN JWT no HEADER HTTP
        .addFilterBefore(new JWTAPIAutenticacaoFilter(),
               UsernamePasswordAuthenticationFilter.class);

        //http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //Service que ira consultar o usuario no banco de dados
        auth.userDetailsService(implementacaoUserDetailsService)
        .passwordEncoder(new BCryptPasswordEncoder());
    }
}
