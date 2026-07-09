package ru.hogwarts.school.product.dto;

public record ProductFullDto(
        Long id,
        String article,
        String name,
        Integer price,
        String mainPic) {
}
