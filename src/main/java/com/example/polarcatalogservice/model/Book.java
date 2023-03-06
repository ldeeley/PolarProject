package com.example.polarcatalogservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
@ToString
@Table
public class Book {
    @Id()
    String isbn;
    String title;
    String author;
    Double wholesalePrice;
    Double retailPrice;

}
