package com.microservices.apis.model;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestControllerAdvice
public class ControleExcecoes extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		String msg = "";
		if(ex instanceof MethodArgumentNotValidException) {
			List<ObjectError> list = ((MethodArgumentNotValidException)ex).getBindingResult().getAllErrors();
			list.stream()
			.distinct()
			.collect(Collectors.toList());
			
			
			
		} else {
			msg = ex.getMessage();
		}
		
		ObjetoError objetoErro = new ObjetoError();
		objetoErro.setError(msg);
		objetoErro.setCode(status.value() + " ==> " + status.getReasonPhrase());
		
				return new ResponseEntity<>(objetoErro, headers, status);
	}
	
	@ExceptionHandler({DataIntegrityViolationException.class, ConstraintViolationException.class,
		PSQLException.class, SQLException.class})
	private ResponseEntity<Object> handleExceptionDataIntegry(Exception ex) {

          String mcs = "";
          
          if(ex instanceof DataIntegrityViolationException) {
        	  mcs = ((DataIntegrityViolationException)ex).getCause().getCause().getMessage();
        	  
          } else if(ex instanceof ConstraintViolationException) {
        	  mcs = ((ConstraintViolationException)ex).getCause().getCause().getMessage();

           
          }else if(ex instanceof PSQLException) {
  	       mcs = ((PSQLException)ex).getCause().getCause().getMessage();

       
          }else if(ex instanceof SQLException) {
  	      mcs = ((SQLException)ex).getCause().getCause().getMessage();
 
          }else {
        	  mcs = ex.getMessage();
          }
          
          ObjetoError objetoErro = new ObjetoError();
          objetoErro.setError(mcs);
          objetoErro.setCode(HttpStatus.INTERNAL_SERVER_ERROR + " ==> " + HttpStatus.INTERNAL_SERVER_ERROR);
          
		
		return new ResponseEntity<>(objetoErro, HttpStatus.INTERNAL_SERVER_ERROR);	

	}

}
