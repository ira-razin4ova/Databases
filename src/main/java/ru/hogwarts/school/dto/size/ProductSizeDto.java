package ru.hogwarts.school.dto.size;

import ru.hogwarts.school.constant.SizeType;

public record ProductSizeDto(
        Long id,
        SizeType size,
        Integer quantity,
        Integer sortOrder) {
}
