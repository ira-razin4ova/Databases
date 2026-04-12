package ru.hogwarts.school.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.product.ProductDTO;
import ru.hogwarts.school.model.Product;
import ru.hogwarts.school.model.ProductSize;
import ru.hogwarts.school.service.ProductService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable @Positive Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public Product createProduct(@RequestBody @Valid Product product) {
        return productService.createProduct(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable @Positive Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Продукт с id " + id + " успешно удален");
    }
    @GetMapping("size-product/{id}")
    public List<ProductSize> getProductSize(@PathVariable @Positive Long id) {
        return productService.findAllSizesByProductId(id);
    }

    @GetMapping("/dto/{id}")
    public ResponseEntity<ProductDTO> getProductByIdDTO(@PathVariable Long id) {
        ProductDTO productDto = productService.getProductDtoById(id);

        // 2. Отдаем его клиенту
        return ResponseEntity.ok(productDto);
    }

}
