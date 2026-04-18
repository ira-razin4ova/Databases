package ru.hogwarts.school.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.hogwarts.school.dto.category.CategoryDto;
import ru.hogwarts.school.dto.student.CreateStudentDto;
import ru.hogwarts.school.dto.student.StudentDTO;
import ru.hogwarts.school.model.Category;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.DataCodecService;

import java.util.List;

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
