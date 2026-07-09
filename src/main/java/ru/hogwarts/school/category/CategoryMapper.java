package ru.hogwarts.school.category;

import org.springframework.stereotype.Component;
import ru.hogwarts.school.category.dto.CategoryDto;

@Component
public class CategoryMapper {

    public CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }

    public Category toEntity(CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }

}
