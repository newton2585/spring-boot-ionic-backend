package com.newtonfernandes.cursomc.resources;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newtonfernandes.cursomc.domain.Cliente;
import com.newtonfernandes.cursomc.dto.ClienteDTO;
import com.newtonfernandes.cursomc.dto.ClienteNewDTO;
import com.newtonfernandes.cursomc.services.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteResource {

	@Autowired
	private ClienteService clienteService;
			
	@GetMapping()
	public ResponseEntity<List<ClienteDTO>> findAll() {
		List<Cliente> list = clienteService.findAll();
		List<ClienteDTO> listDto = list.stream()
				.map(obj -> new ClienteDTO(obj)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listDto);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Cliente> find(@PathVariable Integer id) {
		Cliente obj = clienteService.find(id);
		return ResponseEntity.ok().body(obj);
	}
	
	@PostMapping
	public ResponseEntity<Cliente> insert(@Valid @RequestBody ClienteNewDTO objDto){
		Cliente obj = clienteService.fromDTO(objDto);
		obj = clienteService.insert(obj);
		return ResponseEntity.status(HttpStatus.CREATED).body(obj);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Cliente> update(@Valid @RequestBody ClienteDTO objDto,
			@PathVariable Integer id){
		Cliente obj = clienteService.fromDTO(objDto);
		obj.setId(id);
		obj = clienteService.update(obj);
		return ResponseEntity.status(HttpStatus.OK).body(obj);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Cliente> delete(@PathVariable Integer id) {
		clienteService.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/page")
	public ResponseEntity<Page<ClienteDTO>> findPage(
			@RequestParam(value="page", defaultValue="0") Integer page, 
			@RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage, 
			@RequestParam(value="orderBy", defaultValue="nome") String orderBy, 
			@RequestParam(value="direction", defaultValue="ASC") String direction) {
		Page<Cliente> list = clienteService.findPage(page, linesPerPage, orderBy, direction);
		Page<ClienteDTO> listDto = list.map(obj -> new ClienteDTO(obj));
		return ResponseEntity.ok().body(listDto);
	}
}
