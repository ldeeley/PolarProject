package com.example.polarcatalogservice.repository;

import com.example.polarcatalogservice.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface BookRepository extends JpaRepository<Book, Integer> {

    Boolean existsByIsbn(String bookISBN);

    Optional<Book> findByIsbn(String bookISBN);

    void deleteByIsbn(String isbn);

}
