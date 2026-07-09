package ru.hogwarts.school.product.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String article;
    private String name;
    private Integer price;
    private String mainPic;


    // private List<String> images;
    //private List<Long> facultyIds;
    // private List<ProductSizeDTO> sizes;
}
