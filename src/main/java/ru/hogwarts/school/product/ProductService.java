package ru.hogwarts.school.product;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.faculty.Faculty;
import ru.hogwarts.school.product.dto.ProductDTO;
import ru.hogwarts.school.exception.notfound.EntityNotFoundException;
import ru.hogwarts.school.category.Category;
import ru.hogwarts.school.category.CategoryRepository;
import ru.hogwarts.school.faculty.FacultyRepository;
import ru.hogwarts.school.product.size.ProductSize;

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
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Продукт", id));
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
                    .orElseThrow(() -> new EntityNotFoundException("Категория", id));

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
                throw new EntityNotFoundException("факультеты", ids.size(), existingFaculties.size());
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
