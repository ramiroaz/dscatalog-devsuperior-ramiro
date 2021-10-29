package com.zavala.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zavala.dscatalog.dto.RoleDTO;
import com.zavala.dscatalog.dto.UserDTO;
import com.zavala.dscatalog.dto.UserInsertDTO;
import com.zavala.dscatalog.dto.UserUpdateDTO;
import com.zavala.dscatalog.entities.Role;
import com.zavala.dscatalog.entities.User;
import com.zavala.dscatalog.repositories.RoleRepository;
import com.zavala.dscatalog.repositories.UserRepository;
import com.zavala.dscatalog.services.exceptions.DataBaseException;
import com.zavala.dscatalog.services.exceptions.ResourceNotFoundException;


@Service
public class UserService implements UserDetailsService {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserRepository repository;

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private BCryptPasswordEncoder passEncoder;
	
	@Transactional(readOnly = true)
	public List<UserDTO> findAll() {
		List<User> list = repository.findAll();
		return list.stream().map(x -> new UserDTO(x)).collect(Collectors.toList());
	}

	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> list = repository.findAll(pageable);
		return list.map(x -> new UserDTO(x));
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> obj = repository.findById(id);
		User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entidade não encontrada"));
		//return new UserDTO(entity);
		return new UserDTO(entity); 
	}

	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		copyDtoToEntity(dto, entity);
		entity.setPassword(passEncoder.encode(dto.getPassword()));
		
		entity = repository.save(entity);
		return new UserDTO(entity);
	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO dto) {
		try {
			User entity = repository.getOne(id);  //não usamos findById para não acessar ao banco duas vezes
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new UserDTO(entity);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("ID não encontrado : "+id);
		}
	}


	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}
		catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id não encontrado : "+id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DataBaseException("Violação de integridade");
		}
	}

	private void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setFirstName(dto.getFirstName());;
		entity.setEmail(dto.getEmail());
		entity.setLastName(dto.getLastName());
		//password
		
		entity.getRoles().clear();
		
		for(RoleDTO roleDTO : dto.getRoles() ) {
			Role role = roleRepository.getOne(roleDTO.getId());
			entity.getRoles().add(role);
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// retorna os dados do user, dado um e-mail (email no caso é username
		User user = repository.findByEmail(username);
		if (user == null ) {
			logger.error("Usuário não encontrado :" + username);
			throw new UsernameNotFoundException("E-mail não encontrado");
		}
		logger.info("Usuário encontrado : " + username);
		return user;
	}
	
}
