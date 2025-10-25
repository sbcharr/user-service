//package com.github.sbcharr.user_service.integration_tests;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.github.sbcharr.user_service.AbstractContainerIntegrationTest;
//import com.github.sbcharr.user_service.dtos.request.ProductRequestDto;
//import com.github.sbcharr.user_service.models.User;
//import com.github.sbcharr.user_service.models.Product;
//import com.github.sbcharr.user_service.repositories.ProductRepository;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@ActiveProfiles("integration")
//@AutoConfigureMockMvc
//@Transactional
//public class ProductControllerIntegrationTest extends AbstractContainerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    private Product existingProduct;
//    private User electronicsCategory;
//
//    @BeforeEach
//    void setup() {
//        productRepository.deleteAll();
//
//        // Setup category
//        electronicsCategory = new User();
//        electronicsCategory.setName("Electronics");
//        electronicsCategory.setDescription("Electronic items");
//
//        // Setup product
//        existingProduct = new Product();
//        existingProduct.setName("Laptop");
//        existingProduct.setDescription("MSI Laptop");
//        existingProduct.setPrice(1999.99);
//        existingProduct.setImageUrl("https://example.com/existing.jpg");
//        existingProduct.setCategory(electronicsCategory);
//
//        existingProduct = productRepository.save(existingProduct);
//    }
//
//    @Test
//    void shouldGetProductById() throws Exception {
//        mockMvc.perform(get("/products/{id}", existingProduct.getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(existingProduct.getId()))
//                .andExpect(jsonPath("$.name").value("Laptop"))
//                .andExpect(jsonPath("$.description").value("MSI Laptop"))
//                .andExpect(jsonPath("$.price").value(1999.99))
//                .andExpect(jsonPath("$.imageurl").value("https://example.com/existing.jpg"))
//                .andExpect(jsonPath("$.category.name").value("Electronics"));
//    }
//
//    @Test
//    void shouldGetAllProducts() throws Exception {
//        mockMvc.perform(get("/products"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].name").value("Laptop"));
//    }
//
////    @Test
////    void shouldCreateProduct() throws Exception {
////        ProductRequestDto requestDto = new ProductRequestDto();
////        requestDto.setName("Phone");
////        requestDto.setDescription("Samsung Galaxy");
////        requestDto.setPrice(799.99);
////        requestDto.setImageurl("https://example.com/phone.jpg");
////        requestDto.setCategory(electronicsCategory);
////
////        mockMvc.perform(post("/products")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(requestDto)))
////                .andExpect(status().isCreated())
////                .andExpect(jsonPath("$.id").exists())
////                .andExpect(jsonPath("$.name").value("Phone"))
////                .andExpect(jsonPath("$.description").value("Samsung Galaxy"))
////                .andExpect(jsonPath("$.price").value(799.99))
////                .andExpect(jsonPath("$.imageurl").value("https://example.com/phone.jpg"))
////                .andExpect(jsonPath("$.category.name").value("Electronics"));
////    }
//
//    @Test
//    void shouldUpdateProductById() throws Exception {
//        ProductRequestDto updatedDto = new ProductRequestDto();
//        updatedDto.setName("Laptop Pro");
//        updatedDto.setDescription("MSI Laptop Pro");
//        updatedDto.setPrice(2499.99);
//        updatedDto.setImageurl("https://example.com/existing.jpg");
//        updatedDto.setCategory(electronicsCategory);
//
//        mockMvc.perform(put("/products/{id}", existingProduct.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(existingProduct.getId()))
//                .andExpect(jsonPath("$.name").value("Laptop Pro"))
//                .andExpect(jsonPath("$.description").value("MSI Laptop Pro"))
//                .andExpect(jsonPath("$.price").value(2499.99))
//                .andExpect(jsonPath("$.category.name").value("Electronics"));
//    }
//
//    @Test
//    void shouldDeleteProductById() throws Exception {
//        mockMvc.perform(delete("/products/{id}", existingProduct.getId()))
//                .andExpect(status().isNoContent());
//
//        // Verify product is deleted
//        List<Product> products = productRepository.findAll();
//        assert (products.isEmpty());
//    }
//
//    @Test
//    void shouldReturn404ForNonExistingProduct() throws Exception {
//        mockMvc.perform(get("/products/{id}", 9999L))
//                .andExpect(status().isNotFound());
//    }
//}
