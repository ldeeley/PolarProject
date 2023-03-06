package com.example.polarcatalogservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.ISBN;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {

        @NotNull(message="Book ISBN cannot be null")
        @ISBN
        String isbn;
        @NotNull(message="Book title cannot be null")
        String title;
        @NotNull(message="Book Author cannot be null")
        String author;
        @PositiveOrZero
        Double retailPrice;

}
