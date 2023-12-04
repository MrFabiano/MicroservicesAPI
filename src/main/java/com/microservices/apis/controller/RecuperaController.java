package com.microservices.apis.controller;

import com.microservices.apis.error.ObjetoError;
import com.microservices.apis.model.Usuario;
import com.microservices.apis.repository.UsuarioRepository;
import com.microservices.apis.service.ServiceEnviaEmial;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@RestController
@RequestMapping(value = "/recuperar")
@CrossOrigin(origins = "*")
public class RecuperaController {

    private final UsuarioRepository usuarioRepository;

    private final ServiceEnviaEmial serviceEnviaEmial;

    public RecuperaController(UsuarioRepository usuarioRepository, ServiceEnviaEmial serviceEnviaEmial) {
        this.usuarioRepository = usuarioRepository;
        this.serviceEnviaEmial = serviceEnviaEmial;
    }

    @ResponseBody
    @PostMapping(value = "/")
    public ResponseEntity<ObjetoError> recuperar(@RequestBody Usuario login) throws MessagingException {
        ObjetoError objetoError = new ObjetoError();

        Usuario user = usuarioRepository.findUserByLogin(login.getLogin());

        if(user == null){
            objetoError.setCode("404");
            objetoError.setError("Usuario não encontrado");
        }else{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String senhaNova = dateFormat.format(Calendar.getInstance().getTime());

            String senhaCriptografada = new BCryptPasswordEncoder().encode(senhaNova);
            usuarioRepository.updateSenha(senhaCriptografada, user.getId());

            serviceEnviaEmial.enviarEmail("Recuperação de senha",
                    user.getLogin(),
                    "Sua nova senha é: " + senhaNova);


            objetoError.setCode("200");
            objetoError.setError("Usuario encontrado");
        }

        return new ResponseEntity<>(objetoError, HttpStatus.OK);
    }
}
