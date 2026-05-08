package ru.hogwarts.school.mapper;

import org.springframework.stereotype.Component;
import ru.hogwarts.school.dto.product.ProductDTO;
import ru.hogwarts.school.model.Product;

@Component
public class ProductMapper {
    public ProductDTO toDto(Product product) {
        if (product == null) return null;

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setArticle(product.getArticle());
        dto.setName(product.getProductName());
        dto.setPrice(product.getPrice());
        dto.setMainPic(product.getMainPic());
        return dto;
    }

}
