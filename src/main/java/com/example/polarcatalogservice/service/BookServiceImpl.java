package com.example.polarcatalogservice.service;

import com.example.polarcatalogservice.dto.BookRequest;
import com.example.polarcatalogservice.dto.BookResponse;
import com.example.polarcatalogservice.exception.BookAlreadyInCatalogException;
import com.example.polarcatalogservice.exception.BookNotFoundException;
import com.example.polarcatalogservice.model.Book;

import com.example.polarcatalogservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;



import java.util.List;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService{

    private final BookRepository bookRepository;

    private final ModelMapper modelMapper;

    public BookResponse addNewBook(BookRequest bookRequest) throws BookAlreadyInCatalogException {

        if (bookRepository.existsByIsbn(bookRequest.getIsbn())) {
            throw new BookAlreadyInCatalogException("Couldn't add book with ISBN " + bookRequest.getIsbn() + " It already exists in Catalog");
        } else
        {
                Book book = Book.build(
                bookRequest.getIsbn(),
                bookRequest.getTitle(),
                bookRequest.getAuthor(),
                bookRequest.getRetailPrice() * 1.2,
                bookRequest.getRetailPrice());

            return convertBookToDTO(bookRepository.saveAndFlush(book));
        }
    }

    public ResponseEntity<BookResponse> updateBook(BookRequest bookRequest,String updateISBN) {

        Book updateBook = bookRepository.findByIsbn(updateISBN).orElse(new Book());

        updateBook.setIsbn(bookRequest.getIsbn());
        updateBook.setTitle(bookRequest.getTitle());
        updateBook.setAuthor(bookRequest.getAuthor());
        updateBook.setWholesalePrice(bookRequest.getRetailPrice()/1.2);
        updateBook.setRetailPrice(bookRequest.getRetailPrice());

        return bookRepository.existsByIsbn(bookRequest.getIsbn()) ?

           new ResponseEntity<>(convertBookToDTO(bookRepository.saveAndFlush(updateBook)),HttpStatusCode.valueOf(200))

           :

           new ResponseEntity<>(convertBookToDTO(bookRepository.saveAndFlush(updateBook)),HttpStatusCode.valueOf(201));

    }

    public List<BookResponse> getAllBooks(){
        return bookRepository.findAll().stream().map(this::convertBookToDTO).collect(Collectors.toList());
    }

    public BookResponse findBookByISBN(String bookISBN) throws BookNotFoundException {

        if(bookRepository.existsByIsbn(bookISBN)){
            return convertBookToDTO(bookRepository.findByIsbn(bookISBN).orElseThrow());
        } else {
            throw new BookNotFoundException("Couldn't find a book with ISBN " + bookISBN);
        }

    }

    public BookResponse convertBookToDTO(Book book){
        return modelMapper.map(book, BookResponse.class);
    }

    public Book convertBookDTOtoBook(BookRequest bookRequest){
        return modelMapper.map(bookRequest, Book.class);
    }

    public void deleteBookByISBN(String isbn) {
        bookRepository.deleteByIsbn(isbn);
    }
}
