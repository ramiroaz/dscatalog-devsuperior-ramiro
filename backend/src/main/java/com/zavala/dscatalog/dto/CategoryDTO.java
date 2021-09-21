package com.zavala.dscatalog.dto;

import java.io.Serializable;

import com.zavala.dscatalog.entities.Category;

public class CategoryDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	
	public CategoryDTO() {
	}

	public CategoryDTO(Long id, String nome) {
		super();
		this.id = id;
		this.name = nome;
	}

	public CategoryDTO(Category entity) {
		this.id = entity.getId();
		this.name = entity.getName();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String nome) {
		this.name = nome;
	}
	
	
}
