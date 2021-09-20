package com.zavala.dscatalog.dto;

import java.io.Serializable;

import com.zavala.dscatalog.entities.Category;

public class CategoryDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String nome;
	
	public CategoryDTO() {
	}

	public CategoryDTO(Long id, String nome) {
		super();
		this.id = id;
		this.nome = nome;
	}

	public CategoryDTO(Category entity) {
		this.id = entity.getId();
		this.nome = entity.getName();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
}
