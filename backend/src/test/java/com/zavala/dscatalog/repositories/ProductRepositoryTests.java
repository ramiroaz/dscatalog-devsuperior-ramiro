package com.zavala.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.zavala.dscatalog.entities.Product;
import com.zavala.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;
	
	@Autowired
	private ProductRepository repository;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;   //o arquivo "data.sql" tem 25 produtos
	}
	
	@Test
	public void findByIdShoulReturnNonEmptyOptionalWhenIdExists() {
		//Act
		Optional<Product> result = repository.findById(existingId);
		//Assert
		Assertions.assertTrue(result.isPresent());
	}
	
	@Test
	public void findByIdShoulReturnEmptyOptionalWhenIdDoesNotExists() {
		//Act
		Optional<Product> result = repository.findById(nonExistingId);
		//Assert
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		Product product = Factory.createProduct();
		product.setId(null);
		//Act
		product = repository.save(product);
		//Assert
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts+1, product.getId());
	}
	
	@Test
	public void deleteShouldDelteObjectWhenIdExists() {
		//1. Arrange
		
		//2. act
		repository.deleteById(existingId);
		//3. Assert
		Optional<Product> result = repository.findById(existingId);
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {
		//3. Assert
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			//1. Arrange
			
			//2. Act 
			repository.deleteById(nonExistingId);
		} );
	}

}
