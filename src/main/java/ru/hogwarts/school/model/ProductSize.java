package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import ru.hogwarts.school.constant.SizeType;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductSize that = (ProductSize) o;
        return Objects.equals(getId(), that.getId()) && getSizeType() == that.getSizeType() && Objects.equals(getQuantity(), that.getQuantity()) && Objects.equals(getProduct(), that.getProduct());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSizeType(), getQuantity(), getProduct());
    }

    @Override
    public String toString() {
        return "ProductSize{" +
                "id=" + id +
                ", sizeType=" + sizeType +
                ", quantity=" + quantity +
                ", product=" + product +
                '}';
    }
}
