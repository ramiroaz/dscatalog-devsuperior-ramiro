package com.zavala.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zavala.dscatalog.dto.ProductDTO;
import com.zavala.dscatalog.tests.Factory;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIntegrationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
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
	public void updateShouldNotFoundWhenIDDoesNotExists() throws Exception {
		
		ProductDTO productDTO = Factory.createProductDTO();
		String jsonBoby = objectMapper.writeValueAsString(productDTO);  //converte objeto java em string
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", nonExistingId)
						.content(jsonBoby)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIDExists() throws Exception {
		
		ProductDTO productDTO = Factory.createProductDTO();
		String jsonBoby = objectMapper.writeValueAsString(productDTO);  //converte objeto java em string
		String expectedName = productDTO.getName();
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", existingId)
						.content(jsonBoby)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(existingId));   //verifica se o ID foi salvo
		result.andExpect(jsonPath("$.name").value(expectedName)); //verifica se o nome foi salvo no banco
	}
	
	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
		
		ResultActions result = 
				mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
		result.andExpect(jsonPath("$.content").exists());
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
	}
	
}
