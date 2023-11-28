package com.microservices.apis.controller;


import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import com.microservices.apis.repository.TelefoneRepository;
import com.microservices.apis.service.ImplementacaoUserDetailsService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.microservices.apis.model.Usuario;
import com.microservices.apis.repository.UsuarioRepository; 

//@CrossOrigin(origins = "https://www.minhaapi.com.br")
@RestController
@CrossOrigin
@RequestMapping("/api/usuario")
public class IndexController {

	private final UsuarioRepository usuarioRepository;
	private final TelefoneRepository telefoneRepository;

	private final ImplementacaoUserDetailsService implementacaoUserDetailsService;

	public IndexController(UsuarioRepository usuarioRepository, TelefoneRepository telefoneRepository, ImplementacaoUserDetailsService implementacaoUserDetailsService) {
		this.usuarioRepository = usuarioRepository;
		this.telefoneRepository = telefoneRepository;
		this.implementacaoUserDetailsService = implementacaoUserDetailsService;
	}
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> postCadastro(@RequestBody @Valid Usuario usuario){

		for(int pos = 0; pos < usuario.getTelefones().size(); pos ++){
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}

		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		implementacaoUserDetailsService.insereAcessoPadrao(usuarioSalvo.getId());

		return new ResponseEntity<>(usuarioSalvo, HttpStatus.OK);
	}

	@GetMapping(value = "/{id}", produces = "application/json")
	@CachePut("cacheuser")
	public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
	}

	@GetMapping(produces = "application/json")
	public ResponseEntity<List<Usuario>> usuarioGet() {

	    List<Usuario> usuario = (List<Usuario>) usuarioRepository.findAll();
		
		//Thread.sleep(6000); segura o codigo por 6 segundos, define o tempo do carregamento do sistema
		return new ResponseEntity<>(usuario, HttpStatus.OK);
	}

	@GetMapping(value = "/", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>>usuarios() {

		List<Usuario> usuario = (List<Usuario>) usuarioRepository.findAll();

		//Thread.sleep(6000); segura o codigo por 6 segundos, define o tempo do carregamento do sistema
		return new ResponseEntity<List<Usuario>>(usuario, HttpStatus.OK);
	}

	@GetMapping(value = "/userByName/{nome}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> userByName(@PathVariable("nome") String nome) {

		List<Usuario> usuario = (List<Usuario>) usuarioRepository.findUserByName(nome);

		//Thread.sleep(6000); segura o codigo por 6 segundos, define o tempo do carregamento do sistema
		return new ResponseEntity<>(usuario, HttpStatus.OK);
	}

	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {

	     for(int pos = 0; pos < usuario.getTelefones().size(); pos++){
			 usuario.getTelefones().get(pos).setUsuario(usuario);
		 }

		Usuario userTemporario = usuarioRepository.findById(usuario.getId()).get();

		if(!userTemporario.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		   }
			Usuario usuarioSalvo = usuarioRepository.save(usuario);
			return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.CREATED);
		 }

	@DeleteMapping(value = "/{id}", produces = "application/text")
	public ResponseEntity<Usuario> delete(@PathVariable("id") Long id) {

		usuarioRepository.deleteById(id);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(value = "/removePhone/{id}", produces = "application/text")
	public String removePhone(@PathVariable("id") Long id){
		telefoneRepository.deleteById(id);
		return "ok";
	}
}
