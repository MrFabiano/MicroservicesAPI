package com.microservices.apis.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.microservices.apis.dto.UsuarioDTO;
import com.microservices.apis.model.Usuario;
import com.microservices.apis.repository.UsuarioRepository; 

//@CrossOrigin(origins = "https://www.minhaapi.com.br")
@RestController
@CrossOrigin
@RequestMapping("/api/usuario")
public class IndexController {

	private final UsuarioRepository usuarioRepository;

	public IndexController(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	// Passando mais de um parametro
	@GetMapping(value = "/{id}/codigo/{codigoVenda}", produces = "application/json")
	public ResponseEntity<Usuario> initParametro(@PathVariable(value = "id") Long id,
			@PathVariable(value = "codigoVenda") Long codigo) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

        return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
	}

	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<UsuarioDTO> init(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
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
		return new ResponseEntity<>(usuario, HttpStatus.OK);
	}

	@GetMapping(value = "/userByName/{nome}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>>userByNome(@PathVariable("nome") String nome) {

		List<Usuario> usuario = (List<Usuario>) usuarioRepository.findUserByNome(nome);

		//Thread.sleep(6000); segura o codigo por 6 segundos, define o tempo do carregamento do sistema
		return new ResponseEntity<>(usuario, HttpStatus.OK);
	}


	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> usuarios(@RequestBody @Valid Usuario usuario) throws IOException {

		for(int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		//List<Usuario> usuarioTelefone = new ArrayList<>();
		
		 //usuarioTelefone.stream()
		 //.distinct()
		 //.collect(Collectors.toList())
		 //.toString()
		 //.collect(Collectors.groupingBy(t -> t.getTelefones().get(0).getUsuario()));
		//System.out.println(usu); 
		 
		 //Consumindo API externa
		 URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
		 URLConnection connection = url.openConnection();
		 InputStream is = connection.getInputStream();
		 BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		 
		 String cep = "";
		 StringBuilder jsonCep = new StringBuilder();
		 while((cep = br.readLine()) != null) {
			 jsonCep.append(cep);
		 }
		 
		 Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
		 usuario.setCep(userAux.getCep());
		 usuario.setLogradouro(userAux.getLogradouro());
		 usuario.setComplemento(userAux.getComplemento());
		 usuario.setBairro(userAux.getBairro());
		 usuario.setLocalidade(userAux.getLocalidade());
		 usuario.setUf(userAux.getUf());
		
		//List<Telefone> telefones = new ArrayList<>();
		//for (Telefone telefone : telefones) {
			//usuarioTelefone.getTelefones().toArray();
			//telefone.setUsuario(usuario);
			// telefones.forEach(t ->
			// usuarioTelefone.getTelefones(t.getId(),t.getNumero()));
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
			Usuario usuarioSalvo = usuarioRepository.save(usuario);
			
			return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.CREATED);

		}
		
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping(value = "/{venda}/cadastro/{codigo}", produces = "application/json")
	public ResponseEntity<?> cadastroVenda(@PathVariable Long venda, @PathVariable Long codigo) {

		return new ResponseEntity("cadastro venda" + venda + " cadastro codigo: " + codigo, HttpStatus.CREATED);

	}

	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		

	     List<Usuario> usuList = new ArrayList<>();
        new ArrayList<>(usuList);
	     	
		   usuarioRepository.findUserByLogin(usuario.getLogin());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
			return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.CREATED);

		 }

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PutMapping(value = "/{usuarioId}/venda{vendaId}", produces = "application/json")
	public ResponseEntity<?> atualizarVenda(@PathVariable Long usuarioId, @PathVariable Long vendaId) {

		return new ResponseEntity("Usuario ID: " + usuarioId + " Venda: " + vendaId, HttpStatus.OK);

	}

	@DeleteMapping(value = "/{id}", produces = "application/text")
	public ResponseEntity<Usuario> delete(@PathVariable("id") Long id) {

		usuarioRepository.deleteById(id);

		return ResponseEntity.noContent().build();
	}
}
