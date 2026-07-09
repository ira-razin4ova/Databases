package ru.hogwarts.school.task.dto;

import java.math.BigDecimal;

public record TaskDto (
         Long id,
         String title,
         BigDecimal award,
         Boolean archive,
         Long questId)
{ }
