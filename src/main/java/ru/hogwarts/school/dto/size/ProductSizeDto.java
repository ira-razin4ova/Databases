package ru.hogwarts.school.dto.size;

import ru.hogwarts.school.constant.SizeProduct;

public record ProductSizeDto(
        Long id,
        SizeProduct size,
        Integer quantity,
        Integer sortOrder) {
}
