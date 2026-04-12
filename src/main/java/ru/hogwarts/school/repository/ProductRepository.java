package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Product;
import ru.hogwarts.school.model.ProductSize;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT s FROM Product p JOIN p.sizes s WHERE p.id = :id")
    List<ProductSize> findAllSizesByProductId(Long id);
}