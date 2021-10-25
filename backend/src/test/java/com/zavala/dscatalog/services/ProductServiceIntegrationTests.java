package com.zavala.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.zavala.dscatalog.dto.ProductDTO;
import com.zavala.dscatalog.repositories.ProductRepository;
import com.zavala.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional   //para que os testes sejam independentes
public class ProductServiceIntegrationTests {

	@Autowired
	private ProductService service;		//injetamos, e não mockamos
	
	@Autowired
	private ProductRepository repository;	//podemos injetar este recurso pois é teste de integração
	
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;
	
	@BeforeEach
	void setup() throws Exception {
		existingId = 1L;
		nonExistingId = 9000L;		//id de fato não existe no banco
		countTotalProducts = 25L;   //numero real de registros
	}
	
	//---
	
	@Test
	public void findAllPagedShouldReturnSortedPageWhenSortByName() {
		
		//arrange
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
		//act
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		//assert 
		Assertions.assertFalse(result.isEmpty());  //pagina não vazia
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());    //primeiro produto 
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());       //segundo produto
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());  //terceiro produto
	}
	
	@Test
	public void findAllPagedShouldReturnEmptyWhenPageDouesNotExists() {
		
		//arrange
		PageRequest pageRequest = PageRequest.of(50, 10);
		//act
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		//assert 
		Assertions.assertTrue(result.isEmpty());  //como temos 25 registros, a pagina 50 não existe
		
	}

	
	@Test
	public void findAllPagedShouldReturnPageWhenPage0Size10() throws Exception {
		
		//arrange
		PageRequest pageRequest = PageRequest.of(0, 10);
		//act
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		//assert 
		Assertions.assertFalse(result.isEmpty());  //como temos 25 registros, esa pagina tem que  vir com dados
		Assertions.assertEquals(0, result.getNumber());  //verifica se é a pagina 0
		Assertions.assertEquals(10, result.getSize());   //verifica se retornaram 10 registros
		Assertions.assertEquals(countTotalProducts, result.getTotalElements());
	}
	
	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {
		
		service.delete(existingId);
		
		Assertions.assertEquals(countTotalProducts - 1, repository.count());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		} );
	}
	
}
