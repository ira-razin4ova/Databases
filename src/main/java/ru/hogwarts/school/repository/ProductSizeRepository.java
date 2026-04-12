package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.ProductSize;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {
}