package com.zavala.dscatalog.dto;

import com.zavala.dscatalog.services.validation.UserInsertValid;

@UserInsertValid  //validação personalizada (exemplo, se o e-mail já existe no banco)
public class UserInsertDTO extends UserDTO {
	private static final long serialVersionUID = 1L;

	private String password;
	
	public UserInsertDTO() {
		super();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	
}
