package ru.hogwarts.school.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.category.dto.CategoryDto;
import ru.hogwarts.school.exception.notfound.EntityNotFoundException;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Категория" , id));
    }

    public CategoryDto getByIdCategory(Long id) {
        return categoryMapper.toDto(getCategoryOrThrow(id));
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto dto) {
        Category category = categoryMapper.toEntity(dto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category category = getCategoryOrThrow(id);
        category.setName(dto.getName());
        return categoryMapper.toDto(categoryRepository.save(category));
    }
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.delete(getCategoryOrThrow(id));
    }
}
