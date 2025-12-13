/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.repository;

/**
 *
 * @author Giovanni
 */
import com.mycompany.gestionebiblioteca.model.Book;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryBookRepository implements BookRepository {

    private final Map<String, Book> storage = new HashMap<>();

    @Override
    public Book save(Book entity) {
        if (entity == null) {
            throw new IllegalArgumentException("book must not be null");
        }
        String isbn = entity.getIsbn();
        if (isbn == null) {
            throw new IllegalArgumentException("isbn must not be null");
        }
        storage.put(isbn, entity);
        return entity;
    }

    @Override
    public Optional<Book> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(String id) {
        if (id == null) {
            return;
        }
        storage.remove(id);
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }

    @Override
    public boolean existsById(String id) {
        if (id == null) {
            return false;
        }
        return storage.containsKey(id);
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return findById(isbn);
    }

    @Override
    public List<Book> findByTitleContaining(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String lowerKeyword = keyword.toLowerCase();
        List<Book> result = new ArrayList<>();
        for (Book book : storage.values()) {
            String title = book.getTitle();
            if (title != null && title.toLowerCase().contains(lowerKeyword)) {
                result.add(book);
            }
        }
        result.sort(Comparator.comparing(Book::getTitle, Comparator.nullsLast(String::compareToIgnoreCase)));
        return result;
    }

    @Override
    public List<Book> findByAuthorContaining(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String lowerKeyword = keyword.toLowerCase();
        List<Book> result = new ArrayList<>();
        for (Book book : storage.values()) {
            String author = book.getAuthor();
            if (author != null && author.toLowerCase().contains(lowerKeyword)) {
                result.add(book);
            }
        }
        result.sort(Comparator.comparing(Book::getAuthor, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(Book::getTitle, Comparator.nullsLast(String::compareToIgnoreCase)));
        return result;
    }

    @Override
    public List<Book> findAllOrderByTitle() {
        List<Book> result = new ArrayList<>(storage.values());
        result.sort(Comparator.comparing(Book::getTitle, Comparator.nullsLast(String::compareToIgnoreCase)));
        return result;
    }
}
