package com.example.catalog_service.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record Book(
                @Id Long id,
                @NotBlank(message = "{spring.book.isbn.notblank}") @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "{spring.book.isbn.format}") String isbn,
                @NotBlank(message = "{spring.book.title.notblank}") String title,
                @NotBlank(message = "{spring.book.author.notblank}") String author,
                @NotNull(message = "{spring.book.price.notblank}") @Positive(message = "{spring.book.price.format}") Double price,
                @CreatedDate Instant createdDate,
                @LastModifiedDate Instant lastModifiedDate,
                @Version Integer version) {

        public static Book build(String isbn, String title, String author, Double price) {
                return new Book(null, isbn, title, author, price, null, null, null);
        }

}
