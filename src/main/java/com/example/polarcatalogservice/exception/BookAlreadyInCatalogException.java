package com.example.polarcatalogservice.exception;

public class BookAlreadyInCatalogException extends Exception{

    public BookAlreadyInCatalogException(String message) {
        super(message);
    }
}
