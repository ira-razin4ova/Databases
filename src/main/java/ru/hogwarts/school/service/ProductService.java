package ru.hogwarts.school.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.product.ProductDTO;
import ru.hogwarts.school.exception.NotFoundException;
import ru.hogwarts.school.mapper.ProductMapper;
import ru.hogwarts.school.model.Category;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Product;
import ru.hogwarts.school.model.ProductSize;
import ru.hogwarts.school.repository.CategoryRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FacultyRepository facultyRepository;
    private final ProductMapper productMapper;

    public Product getProductOrThrow(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("Продукт с id " + id + " не найден"));
    }

    public Product createProduct(Product product) {
        resolveCategory(product);
        resolveFaculty(product);
        addSizeInProduct(product);
        return productRepository.save(product);
    }


    public void resolveCategory(Product product) {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Long id = product.getId();
            Category existingCategory = categoryRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Категория '" + id + "' не найдена"));

            product.setCategory(existingCategory);
        }
    }

    public void resolveFaculty(Product product) {
        if (product.getFaculties() != null && !product.getFaculties().isEmpty()) {
            List<Long> ids = product.getFaculties().stream()
                    .map(Faculty::getId)
                    .toList();
            List<Faculty> existingFaculties = facultyRepository.findAllById(ids);

            if (existingFaculties.size() != ids.size()) {
                throw new NotFoundException("Некоторые факультеты не были найдены");
            }

            product.setFaculties(existingFaculties);
        }
    }

    public void addSizeInProduct(Product product) {
        if (product.getSizes() != null) {
            product.getSizes().forEach(size -> size.setProduct(product));
        }
    }

    public List<ProductSize> findAllSizesByProductId(Long id) {
        return productRepository.findAllSizesByProductId(id);
    }

    public ProductDTO getProductDtoById(Long id) {
        Product product = getProductOrThrow(id);
        return productMapper.toDto(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product deleteProduct = getProductOrThrow(id);
        productRepository.delete(deleteProduct);
    }
}
