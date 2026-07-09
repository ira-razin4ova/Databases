package ru.hogwarts.school.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductDto(String article,
                               @NotNull (message = "У продукта должно быть название")
                               String productName,
                               @NotNull (message = "Не указана категория")
                               Long categoryId,
                               List<Long> facultiesId,
                               String mainPic,
                               List<String> images,
                               @NotNull (message = "Не указана цена")
                               BigDecimal price,
                               String teaser,
                               boolean tempOut,
                               @PositiveOrZero (message = "Не может быть отрицательно количество")
                               int quantity) {


}
