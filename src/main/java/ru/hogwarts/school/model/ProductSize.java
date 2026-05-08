package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import ru.hogwarts.school.constant.SizeProduct;

import java.util.Objects;

@Entity
@Table (name = "product_size")
public class ProductSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SizeProduct sizeProduct;

    @Column (name = "quantity")
    private Integer quantity;


    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    public ProductSize(Long id, Product product, Integer quantity, SizeProduct sizeProduct, Integer sortOrder) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.sizeProduct = sizeProduct;
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

    public void setSizeType(SizeProduct sizeProduct) {
        this.sizeProduct = sizeProduct;
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

    public SizeProduct getSizeType() {
        return sizeProduct;
    }
    public int getSortOrder() {
        return sizeProduct != null ? sizeProduct.getSortOrder() : 0;
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
                ", sizeType=" + sizeProduct +
                ", quantity=" + quantity +
                ", product=" + product +
                '}';
    }
}
