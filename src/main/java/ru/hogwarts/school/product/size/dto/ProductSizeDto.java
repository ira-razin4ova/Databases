package ru.hogwarts.school.product.size.dto;

import ru.hogwarts.school.product.enums.SizeProduct;

public record ProductSizeDto(
        Long id,
        SizeProduct size,
        Integer quantity,
        Integer sortOrder) {
}
