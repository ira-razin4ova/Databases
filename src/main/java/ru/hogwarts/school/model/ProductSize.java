package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import ru.hogwarts.school.constant.SizeType;

@Entity
@Table (name = "product_size")
public class ProductSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SizeType sizeType;

    @Column (name = "quantity")
    private Integer quantity;


    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    public ProductSize(Long id, Product product, Integer quantity, SizeType sizeType, Integer sortOrder) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.sizeType = sizeType;
    }
    public ProductSize() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setSizeType(SizeType sizeType) {
        this.sizeType = sizeType;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public SizeType getSizeType() {
        return sizeType;
    }
    public int getSortOrder() {
        return sizeType != null ? sizeType.getSortOrder() : 0;
    }
}
