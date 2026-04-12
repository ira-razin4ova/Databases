package ru.hogwarts.school.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping ("/{id}")
    public Category categoryById (@PathVariable @Positive Long id) {
return categoryService.getCategoryById(id);
    }

    @PostMapping
    public Category createCategory (@RequestBody @Valid Category category) {
        return categoryService.createCategory(category);
    }

    @PutMapping("/{id}")
    public Category editCategory (@PathVariable @Positive Long id,
                                  @RequestBody Category category) {
        return categoryService.editCategory(id, category);
    }

    @DeleteMapping ("/{id}")

    public ResponseEntity <String> deleteCategory (@PathVariable @Positive Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Категория с id " + id + " успешно удалена");
    }
}
