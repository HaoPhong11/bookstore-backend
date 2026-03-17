package com.example.bookstore_db.controller;

import com.example.bookstore_db.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String q) {
        return ResponseEntity.ok(bookService.searchBooks(q));
    }
}