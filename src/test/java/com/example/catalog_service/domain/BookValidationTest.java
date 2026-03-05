package com.example.catalog_service.domain;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;

public class BookValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test validation success when all fields are correct")
    void whenAllFieldsCorrectThenValidationSucceeds() {
        var book = Book.build("1234567890", "Title", "Author", 9.90);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Test when isbn is defined but incorrect fails validation")
    void whenIsbnDefinedButIncorrectThenValidationFails() {
        var book = Book.build("12345fdsf678932320", "Title", "Author", 9.90);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        String expectedMessage = "The ISBN format must follow the standards ISBN-10 or ISBN-13.";
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(expectedMessage);

    }
}
