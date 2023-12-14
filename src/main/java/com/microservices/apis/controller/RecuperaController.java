package com.microservices.apis.controller;

import com.microservices.apis.error.ObjetoError;
import com.microservices.apis.model.Usuario;
import com.microservices.apis.repository.UsuarioRepository;
import com.microservices.apis.service.ServiceSendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@RestController
@RequestMapping(value = "/recover")
@CrossOrigin(origins = "*")
public class RecuperaController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ServiceSendEmail serviceSendEmail;

    @ResponseBody
    @PostMapping(value = "/")
    public ResponseEntity<ObjetoError> recuperar(@RequestBody Usuario login) throws MessagingException {
        ObjetoError objetoError = new ObjetoError();

        Usuario user = usuarioRepository.findUserByLogin(login.getLogin());

        if(user == null){
            objetoError.setCode("404");
            objetoError.setError("User Not-found");
        }else{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String senhaNova = dateFormat.format(Calendar.getInstance().getTime());

            String senhaCriptografada = new BCryptPasswordEncoder().encode(senhaNova);
            usuarioRepository.updateSenha(senhaCriptografada, user.getId());

            serviceSendEmail.sendEmail("Password recovery",
                    user.getLogin(),
                    "Your new password is: " + senhaNova);


            objetoError.setCode("200");
            objetoError.setError("User Not-found");
        }

        return new ResponseEntity<>(objetoError, HttpStatus.OK);
    }
}
