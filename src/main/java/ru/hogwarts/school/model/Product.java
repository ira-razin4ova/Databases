package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "article")
    private String article;

    @Column(name = "archive")
    private boolean archive;

    @Column
    private String productName;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "product_faculties", // Название новой таблицы в базе
            joinColumns = @JoinColumn(name = "product_id"), // Ссылка на этот товар
            inverseJoinColumns = @JoinColumn(name = "faculty_id") // Ссылка на факультет
    )
    private List<Faculty> faculties = new ArrayList<>();

    @Column(name = "main_pic")
    private String mainPic;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();

    @Column(name = "popular")
    private Integer popular;

    @Column(name = "price")
    private Integer price;

    @Column(name = "teaser")
    private String teaser;

    @Column(name = "temp_out")
    private boolean tempOut;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductSize> sizes = new ArrayList<>();


}
