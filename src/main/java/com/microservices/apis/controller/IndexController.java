package com.microservices.apis.controller;


import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.microservices.apis.repository.TelefoneRepository;
import com.microservices.apis.service.ImplementacaoUserDetailsService;
import com.microservices.apis.service.RelatorioService;
import net.sf.jasperreports.util.Base64Util;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
	private final RelatorioService relatorioService;

	private final ImplementacaoUserDetailsService implementacaoUserDetailsService;

	public IndexController(UsuarioRepository usuarioRepository, TelefoneRepository telefoneRepository, RelatorioService relatorioService, RelatorioService relatorioService1, ImplementacaoUserDetailsService implementacaoUserDetailsService) {
		this.usuarioRepository = usuarioRepository;
		this.telefoneRepository = telefoneRepository;
		this.relatorioService = relatorioService1;
		this.implementacaoUserDetailsService = implementacaoUserDetailsService;
	}

	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> postCadastro(@RequestBody @Valid Usuario usuario) {

		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
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
	public ResponseEntity<Page<Usuario>> usuarios() {

		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));

		Page<Usuario> list = usuarioRepository.findAll(page);

		//List<Usuario> usuario = (List<Usuario>) usuarioRepository.findAll();

		//Thread.sleep(6000); segura o codigo por 6 segundos, define o tempo do carregamento do sistema
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/page/{pagina}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuarioPage(@PathVariable("pagina") int pagina) {

		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));

		Page<Usuario> list = usuarioRepository.findAll(page);

		//List<Usuario> usuario = (List<Usuario>) usuarioRepository.findAll();

		//Thread.sleep(6000); segura o codigo por 6 segundos, define o tempo do carregamento do sistema
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/userByName/{nome}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> userByName(@PathVariable("nome") String nome) {


		PageRequest pageRequest = null;
		Page<Usuario> list = null;

		if (nome == null || (nome != null && nome.trim().isEmpty()
				|| nome.equalsIgnoreCase("undefined"))) {

			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			list = usuarioRepository.findAll(pageRequest);
		} else {
			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}

		//List<Usuario> usuario = (List<Usuario>) usuarioRepository.findUserByName(nome);

		//Thread.sleep(6000); segura o codigo por 6 segundos, define o tempo do carregamento do sistema
		return new ResponseEntity<>(list, HttpStatus.OK);
	}


	@GetMapping(value = "/userByName/{nome}/page/{page}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> userByNamePage(@PathVariable("nome") String nome,
														@PathVariable("page") int page) {


		PageRequest pageRequest = null;
		Page<Usuario> list = null;

		if (nome == null || (nome != null && nome.trim().isEmpty()
				|| nome.equalsIgnoreCase("undefined"))) {

			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			list = usuarioRepository.findAll(pageRequest);
		} else {
			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome, pageRequest);
		}

		//List<Usuario> usuario = (List<Usuario>) usuarioRepository.findUserByName(nome);

		//Thread.sleep(6000); segura o codigo por 6 segundos, define o tempo do carregamento do sistema
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {

		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}

		Usuario userTemporario = usuarioRepository.findById(usuario.getId()).get();

		if (!userTemporario.getSenha().equals(usuario.getSenha())) {
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
	public String removePhone(@PathVariable("id") Long id) {
		telefoneRepository.deleteById(id);
		return "ok";
	}

	@GetMapping(value = "/relatorio", produces = "application/json")
	public ResponseEntity<String> downloadRelatrio(HttpServletRequest request) throws Exception {
		byte[] pdf = relatorioService.gerarRelatorio("relatorio-usuario",
				request.getServletContext());

		String base64 = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);

		return new ResponseEntity<>(base64, HttpStatus.OK);

	}
}

