package br.org.games_brazil.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import br.org.games_brazil.model.Produto;
import br.org.games_brazil.repository.CategoriaRepository;
import br.org.games_brazil.repository.ProdutoRepository;
import jakarta.validation.Valid;

@RestController									   // Informar ao Spring que essa classe será um controlador da nossa API Rest
@RequestMapping("/produtos")					   // HTTP
@CrossOrigin(origins = "*", allowedHeaders = "*")  // Deixar que todas as origens de fora consigam acessar
public class ProdutoController {

	@Autowired			
	private ProdutoRepository produtoRepository;   // Injeção de dependência
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@GetMapping
	public ResponseEntity<List<Produto>> getAll(){
		return ResponseEntity.ok(produtoRepository.findAll()); // Primeiro método de acesso. Será responsável por listar todas as postagens que terrmos no banco de dados no futuro
	}
													//Busca no banco de dados pelo ID
	@GetMapping("/{id}")
	public ResponseEntity<Produto> getById(@PathVariable Long id){
		return produtoRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/nome/{nome}")
	public ResponseEntity<List<Produto>> getByNome(@PathVariable String nome){
		return ResponseEntity.ok(produtoRepository.findAllByNomeContainingIgnoreCase(nome));
	}
	
	@PostMapping
	public ResponseEntity<Produto> post(@Valid @RequestBody Produto produto){
		if (categoriaRepository.existsById(produto.getCategoria().getId())) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(produtoRepository.save(produto));
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome não existe!", null);
	}
	
	@PutMapping
	public ResponseEntity<Produto> put(@Valid @RequestBody Produto produto){
		if (produtoRepository.existsById(produto.getId())) {
			if (categoriaRepository.existsById(produto.getCategoria().getId())) {
				return ResponseEntity.status(HttpStatus.OK)
					.body(produtoRepository.save(produto));
			}															// verificar chaves do if
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome não existe!", null);
			}
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		Optional<Produto> produto = produtoRepository.findById(id);
		
		if(produto.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		produtoRepository.deleteById(id);
	}
	
}