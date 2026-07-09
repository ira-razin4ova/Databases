package ru.hogwarts.school.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.product.dto.ProductDTO;
import ru.hogwarts.school.product.size.ProductSize;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable @Positive Long id) {
        return productService.getProductOrThrow(id);
    }

    @PostMapping
    public Product createProduct(@RequestBody @Valid Product product) {
        return productService.createProduct(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable @Positive Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("size-product/{id}")
    public List<ProductSize> getProductSize(@PathVariable @Positive Long id) {
        return productService.findAllSizesByProductId(id);
    }

    @GetMapping("/dto/{id}")
    public ResponseEntity<ProductDTO> getProductByIdDTO(@PathVariable Long id) {
        ProductDTO productDto = productService.getProductDtoById(id);
        return ResponseEntity.ok(productDto);
    }

}
