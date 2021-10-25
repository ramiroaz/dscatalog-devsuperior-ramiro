package com.zavala.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.zavala.dscatalog.dto.ProductDTO;
import com.zavala.dscatalog.entities.Category;
import com.zavala.dscatalog.entities.Product;
import com.zavala.dscatalog.repositories.CategoryRepository;
import com.zavala.dscatalog.repositories.ProductRepository;
import com.zavala.dscatalog.services.exceptions.DataBaseException;
import com.zavala.dscatalog.services.exceptions.ResourceNotFoundException;
import com.zavala.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private Category category;
	
	@BeforeEach
	void setup() {
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		product = Factory.createProduct();
		category = Factory.createCategory();
		page = new PageImpl<>(List.of(product));
		
		//comportamento simulado do save
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		//comportamento simulado do findById
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		//getOne do ProductRepository
		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		//getOne do categoryRepository
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		//comportamento simulado do findAll
		Mockito.when(repository.findAll( (Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		
		//comportamento simulado do deleteById (sucesso)
		Mockito.doNothing().when(repository).deleteById(existingId);
		
		
		//comportamento simulado do deleteById (erro EmptyResultDataAccessException)
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		
		//comportamento simulado do deleteById (erro DataIntegrityViolationException)
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		
		
	}
	
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		//assert
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			//arrange
			ProductDTO productDTO = Factory.createProductDTO();
			//act
			service.update(nonExistingId,productDTO);
		} );
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {
		//arrange
		ProductDTO productDTO = Factory.createProductDTO();
		//act
		ProductDTO result = service.update(existingId,productDTO);
		//assert
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		//assert
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			//act
			service.findById(nonExistingId);
		} );
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenProductExists() {
		//act
		ProductDTO result = service.findById(existingId);
		//assert
		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).findById(existingId);
	}
	
	@Test
	public void findAllByPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);	//pagina 0, tamanho 10
		//Act
		Page<ProductDTO> result = service.findAllPaged(pageable);
		//Assert
		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).findAll(pageable);	//verifica se chamou 1 vez ao findAll
	}
	
	@Test
	public void deleteShouldThrowDataBaseExceptionWhenIdHasDependences() {
		//assert
		Assertions.assertThrows(DataBaseException.class, () -> {
			//act
			service.delete(dependentId);
		} );
		
		//o Mockito foi chamado?
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		//assert
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			//act
			service.delete(nonExistingId);
		} );
		
		//o Mockito foi chamado?
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		//assert
		Assertions.assertDoesNotThrow( () -> {
			//act
			service.delete(existingId);
		} );
		
		//o Mockito foi chamado?
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}
}
