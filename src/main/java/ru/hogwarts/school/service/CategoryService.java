package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.model.Category;
import ru.hogwarts.school.repository.CategoryRepository;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService (CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Категория с" + id + "не найдена"));
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category editCategory(Long id,Category category) {
        Category existingCategory = getCategoryById(id);
        existingCategory.setName(category.getName());
        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory (Long id) {
        Category deletCategory = getCategoryById(id);
        categoryRepository.deleteById(id);
    }
}
