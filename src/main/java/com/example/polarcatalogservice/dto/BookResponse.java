package com.example.polarcatalogservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class BookResponse {

        String isbn;
        String title;
        String author;
        Double retailPrice;

}
