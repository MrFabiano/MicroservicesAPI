package com.microservices.apis.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class ServiceSendEmail {

    private final String userName = "fabianotestemail@gmail.com";
    private final String senha = "//*//*";

    public void sendEmail(String assunto, String emailDestino, String mensagem) throws MessagingException {

        Properties properties = new Properties();
        properties.put("mail.smtp.ssl.trust", "*");
        properties.put("mail.smtp.auth", "true"); //Autorizaçao
        properties.put("mail.smtp.starttls", "true"); //Autenticação
        properties.put("mail.smtp.host", "smtp.gmail.com"); // servidor google
        properties.put("mail.smtp.port", "465"); //Porta servidor
        properties.put("mail.smtp.socketFactory.port", "465"); //Especifica porta
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //Classe de sockect

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, senha);
            }
        });

        Address[] toUser = InternetAddress.parse(emailDestino);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(userName)); //Quem esta enviando - Nos
        message.setRecipients(Message.RecipientType.TO, toUser); //Pra quem vai receber o email
        message.setSubject(assunto); //Assunto do email
        message.setText(mensagem);

        Transport.send(message);
    }
}
