package ru.hogwarts.school.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.category.CategoryDto;
import ru.hogwarts.school.model.Category;
import ru.hogwarts.school.service.CategoryService;

@Validated
@RestController
@RequestMapping("/categories")

public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getByIdCategory(id));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            @RequestBody @Valid CategoryDto dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.createCategory(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> editCategory(
            @PathVariable @Positive Long id,
            @RequestBody @Valid CategoryDto dto) {

        return ResponseEntity.ok(
                categoryService.editCategory(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable @Positive Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Категория с id " + id + " успешно удалена");
    }
}
