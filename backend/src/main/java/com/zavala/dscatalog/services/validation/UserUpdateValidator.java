package com.zavala.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.zavala.dscatalog.dto.UserUpdateDTO;
import com.zavala.dscatalog.entities.User;
import com.zavala.dscatalog.repositories.UserRepository;
import com.zavala.dscatalog.resources.exceptions.FieldMessage;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

	@Autowired
	private HttpServletRequest request;	//guarda as informações da requisição
	
	@Autowired
	private UserRepository repository;
	
	@Override
	public void initialize(UserUpdateValid ann) {
	}
	
	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
		
		//pega um mapa com os atributos da url
		@SuppressWarnings("unchecked")
		var uriVars = (Map<String,String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		//pega o ID informado na url
		Long userId = Long.parseLong(uriVars.get("id"));
		
		List<FieldMessage> list = new ArrayList<>();
		
		//-- aqui os testes de validação
		User user = repository.findByEmail(dto.getEmail());
		
		// se existe usuário no banco com o e-mail informado, 
		// e esse usuário não é o mesmo que estou tentando atualizar
		// então erro!
		if (user != null && userId != user.getId() ) {
			list.add(new FieldMessage("email","E-mail já existe"));
		}
		
		//--
		
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage())
				.addPropertyNode(e.getFieldName())
				.addConstraintViolation();
		}
		
		return list.isEmpty();
	}
}
