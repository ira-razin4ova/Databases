package ru.hogwarts.school.product;

import org.springframework.stereotype.Component;
import ru.hogwarts.school.product.dto.ProductDTO;

@Component
public class ProductMapper {
    public ProductDTO toDto(Product product) {
        if (product == null) return null;

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setArticle(product.getArticle());
        dto.setName(product.getProductName());
        //dto.setPrice(product.getPrice());
        dto.setMainPic(product.getMainPic());
        return dto;
    }

}
