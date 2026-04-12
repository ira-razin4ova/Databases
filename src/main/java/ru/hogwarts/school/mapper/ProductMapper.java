package ru.hogwarts.school.mapper;

import org.springframework.stereotype.Component;
import ru.hogwarts.school.dto.product.ProductDTO;
import ru.hogwarts.school.dto.size.ProductSizeDTO;
import ru.hogwarts.school.model.Product;
import ru.hogwarts.school.model.ProductSize;
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
//        dto.setImages(product.getImages());
//
//        if (product.getFaculties() != null) {
//            dto.setFacultyIds(product.getFaculties().stream()
//                    .map(Faculty::getId)
//                    .toList());
//        }
//
//        // --- ДОБАВЛЯЕМ САЙЗЫ ---
//        if (product.getSizes() != null) {
//            dto.setSizes(product.getSizes().stream()
//                    .map(this::toSizeDto) // Используем вспомогательный метод ниже
//                    .toList());
//        }

        return dto;
    }

    private ProductSizeDTO toSizeDto(ProductSize size) {
        if (size == null) return null;
        ProductSizeDTO sizeDto = new ProductSizeDTO();
        sizeDto.setId(size.getId());
        sizeDto.setSize(size.getSizeType());
        sizeDto.setQuantity(size.getQuantity());
        sizeDto.setSortOrder(size.getSortOrder());
        return sizeDto;
    }
}
