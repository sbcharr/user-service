package com.github.sbcharr.user_service.unit_tests.controllers;

import com.github.sbcharr.user_service.dtos.request.ProductRequestDto;
import com.github.sbcharr.user_service.dtos.response.ProductResponseDto;
import com.github.sbcharr.user_service.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("unit")
public class ProductControllerTest {
    @InjectMocks
    private ProductController productController;
    @Mock
    private IProductService productService;

    private Product sampleProduct;
    private ProductRequestDto sampleProductRequestDto;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("iPhone");
        sampleProduct.setDescription("Latest iPhone");
        sampleProduct.setPrice(999.99);
        sampleProduct.setImageUrl("http://example.com/image.jpg");
        User sampleCategory = new User();
        sampleCategory.setId(1L);
        sampleCategory.setName("Electronics");
        sampleProduct.setCategory(sampleCategory);

        sampleProductRequestDto = new ProductRequestDto();
        sampleProductRequestDto.setName("iPhone");
        sampleProductRequestDto.setDescription("Latest iPhone");
        sampleProductRequestDto.setPrice(999.99);
        sampleProductRequestDto.setImageurl("http://image");

        User sampleCategoryDto = new User();
        sampleCategoryDto.setId(1L);
        sampleCategoryDto.setName("Electronics");
        sampleProduct.setCategory(sampleCategory);
    }

    @Test
    void testCreateProduct_success() {
        when(productService.createProduct(any(Product.class))).thenReturn(sampleProduct);
        ResponseEntity<ProductResponseDto> response = productController.createProduct(sampleProductRequestDto);
        assertNotNull(response.getBody());

        ProductResponseDto responseBody = response.getBody();
        assertEquals(sampleProduct.getId(), responseBody.getId());
        assertEquals(sampleProduct.getName(), responseBody.getName());
        assertEquals(sampleProduct.getDescription(), responseBody.getDescription());
        assertEquals(sampleProduct.getPrice(), responseBody.getPrice());
        assertEquals(sampleProduct.getImageUrl(), responseBody.getImageurl());
        //assertEquals(sampleProduct.getCategory(), responseDto.getCategory());
        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    void testCreateProduct_failure_returns500() {
        when(productService.createProduct(any(Product.class))).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> productController.createProduct(sampleProductRequestDto));

        assertEquals(500, ex.getStatusCode().value());
    }

    @Test
    void testUpdateProduct_success() {
        when(productService.updateProduct(any(Product.class), eq(1L))).thenReturn(sampleProduct);

        ResponseEntity<ProductResponseDto> response = productController.updateProduct(sampleProductRequestDto,
                1L);

        assertNotNull(response);

        ProductResponseDto responseBody = response.getBody();
        assertEquals("iPhone", responseBody.getName());
        verify(productService).updateProduct(any(Product.class), eq(1L));
    }

    @Test
    void testUpdateProduct_NullId_throws400() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> productController.updateProduct(sampleProductRequestDto, null));

        assertEquals(400, ex.getStatusCode().value());
        verify(productService, never()).updateProduct(any(), any());
    }

    @Test
    void testUpdateProduct_invalidId_throws400() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> productController.updateProduct(sampleProductRequestDto, 0L));

        assertEquals(400, ex.getStatusCode().value());
        verify(productService, never()).updateProduct(any(), any());
    }

    @Test
    void testUpdateProduct_failure_returns404() {
        when(productService.updateProduct(any(Product.class), eq(1L))).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> productController.updateProduct(sampleProductRequestDto, 1L));

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testGetAllProducts_success() {
        when(productService.getAllProducts()).thenReturn(Arrays.asList(sampleProduct));

        List<ProductResponseDto> response = productController.getAllProducts();

        assertEquals(1, response.size());
        assertEquals("iPhone", response.get(0).getName());
        verify(productService).getAllProducts();
    }

    @Test
    void testGetAllProducts_emptyList() {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        List<ProductResponseDto> response = productController.getAllProducts();

        assertTrue(response.isEmpty());
    }

    @Test
    void testGetProductById_success() {
        when(productService.getProductById(1L)).thenReturn(sampleProduct);

        ProductResponseDto response = productController.getProductById(1L);

        assertNotNull(response);
        assertEquals("iPhone", response.getName());
        verify(productService).getProductById(1L);
    }

    @Test
    void testGetProductById_notFound_throws404() {
        when(productService.getProductById(1L)).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> productController.getProductById(1L));

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testDeleteProductById_success() {
        doNothing().when(productService).deleteProductById(1L);

        ResponseEntity<Void> response = productController.deleteProductById(1L);

        assertNull(response.getBody());
        verify(productService).deleteProductById(1L);
    }
}
