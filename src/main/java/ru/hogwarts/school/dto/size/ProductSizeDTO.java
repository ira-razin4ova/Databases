package ru.hogwarts.school.dto.size;

import lombok.Data;
import ru.hogwarts.school.constant.SizeType;
@Data
public class ProductSizeDTO {
    private Long id;
    private SizeType size; // Твой Enum
    private Integer quantity;
    private Integer sortOrder;
}
