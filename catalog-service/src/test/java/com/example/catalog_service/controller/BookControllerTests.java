package com.example.catalog_service.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;

import com.example.catalog_service.exception.BookNotFoundException;
import com.example.catalog_service.service.BookService;

@WebMvcTest
public class BookControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void whenGetBookNotExistingThenShouldReturn404() throws Exception {
        String isbn = "4343345r234532";
        given(bookService.viewBookDetails(isbn)).willThrow(BookNotFoundException.class);

        mvc.perform(get("/book/" + isbn)).andExpect(status().isNotFound());
    }

}
