package com.dio.beerstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BeerNotFoundException extends Exception {
    
    // Construtor para busca por ID ou por Nome
    public BeerNotFoundException(Object identifier) {
        super(String.format("Beer with identifier %s not found in the system.", identifier.toString()));
    }
}
