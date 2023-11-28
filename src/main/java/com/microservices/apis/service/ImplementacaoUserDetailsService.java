package com.microservices.apis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.microservices.apis.model.Usuario;
import com.microservices.apis.repository.UsuarioRepository;

@Service
public class ImplementacaoUserDetailsService implements UserDetailsService{

    private final UsuarioRepository usuarioRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ImplementacaoUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //Consultar no banco o usuario

        Usuario usuario = usuarioRepository.findUserByLogin(username);
        if(usuario == null){
             throw new UsernameNotFoundException("Usuario não encontrado");
        }
//        return new User(usuario.getLogin(),
//                     usuario.getPassword(),
//                  usuario.getAuthorities());

        return new org.springframework.security.core.userdetails.User(usuario.getLogin(),
                new BCryptPasswordEncoder().encode(usuario.getPassword()),
                usuario.getAuthorities());
     }

    public void insereAcessoPadrao(Long id) {

        //Descobre qual a constraint de restrição
        String constraint = usuarioRepository.cosultaConstraintRole();
        if(constraint != null){
            jdbcTemplate.execute(" ALTER TABLE usuarios_role DROP CONSTRAINT " + constraint);
        }
        //Remove constraint
        //usuarioRepository.removeConstraintRole(constraint);

        //INsere o acesso padrão
        usuarioRepository.insertAccessRolePadrao(id);

    }
}
