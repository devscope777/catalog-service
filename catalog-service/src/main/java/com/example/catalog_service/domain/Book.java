package com.example.catalog_service.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record Book(
        @NotBlank(message = "{spring.book.isbn.notblank}") @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "{spring.book.isbn.format}") String isbn,
        @NotBlank(message = "{spring.book.title.notblank}") String title,
        @NotBlank(message = "{spring.book.author.notblank}") String author,
        @NotNull(message = "{spring.book.price.notblank}") @Positive(message = "{spring.book.price.format}") Double price) {

}
