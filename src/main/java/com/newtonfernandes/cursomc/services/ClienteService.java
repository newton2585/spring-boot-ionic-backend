package com.newtonfernandes.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newtonfernandes.cursomc.domain.Cidade;
import com.newtonfernandes.cursomc.domain.Cliente;
import com.newtonfernandes.cursomc.domain.Endereco;
import com.newtonfernandes.cursomc.domain.enums.TipoCliente;
import com.newtonfernandes.cursomc.dto.ClienteDTO;
import com.newtonfernandes.cursomc.dto.ClienteNewDTO;
import com.newtonfernandes.cursomc.repositories.CidadeRepository;
import com.newtonfernandes.cursomc.repositories.ClienteRepository;
import com.newtonfernandes.cursomc.repositories.EnderecoRepository;
import com.newtonfernandes.cursomc.services.exceptions.DataIntegrityException;
import com.newtonfernandes.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	public List<Cliente> findAll() {
		return clienteRepository.findAll();
	}
		
	public Cliente find(Integer id) {
		Optional<Cliente> obj = clienteRepository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}
	
	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = clienteRepository.save(obj);
	    enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}
	
	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj, obj);
		return clienteRepository.save(newObj);
	}

	public void delete(Integer id) {
		find(id);
		try {
			clienteRepository.deleteById(id);
		}catch(DataIntegrityViolationException e){
			throw new DataIntegrityException("Não é possível excluir porque há pedidos "
					+ "relacionados");
		}	
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, 
			String direction){		
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), 
				orderBy);
		return clienteRepository.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null);
	}
	
	public Cliente fromDTO(ClienteNewDTO objDto) {
		Cliente cli = new Cliente (null, objDto.getNome(), objDto.getEmail(), 
				objDto.getCpfOuCnpj(), TipoCliente.toEnum(objDto.getTipo()));
		
		Cidade cid = cidadeRepository.findById(objDto.getCidadeId()).get();
		
		Endereco end = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), 
				objDto.getComplemento(), objDto.getBairro(), objDto.getCep(), cli, cid);
		
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDto.getTelefone1());
		
		if(objDto.getTelefone2()!=null) {
			cli.getTelefones().add(objDto.getTelefone2());
		}
		
		if(objDto.getTelefone3()!=null) {
			cli.getTelefones().add(objDto.getTelefone3());
		}
		return cli;
	}
	
	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}
}
