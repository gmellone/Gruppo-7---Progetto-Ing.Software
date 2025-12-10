/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.service;
import com.mycompany.gestionebiblioteca.exceptions.ElementoNonPresenteinArchivioException;
import com.mycompany.gestionebiblioteca.exceptions.BibliotecaException;
import com.mycompany.gestionebiblioteca.model.Book;
import com.mycompany.gestionebiblioteca.repository.LibroRepository;
import java.util.List;
import java.util.Optional;


/**
 *
 * @author Utente
 */
public class LibroService {
    
   private final LibroRepository libroRepository;
   
   public LibroService(LibroRepository libroRepository){
   if (libroRepository == null) {
            throw new IllegalArgumentException("bookRepository must not be null");
        }
        this.libroRepository = libroRepository;
   }
   
   public Book addBook(String isbn,String titolo,List<String>autori,int annoDiPubblicazione,int copieDisponibili){
       return null;
   }
   
   public Book updateBook (String isbn,String titolo,List<String>autori,int annoDiPubblicazione,int copieDisponibili){
       return null;
   }
   
   public void deleteBook (String isbn){
   
   }
   
   public Optional<Book> getBookByIsbn(String isbn){
       return null;
   }
   
   public List<Book>searchByTitle(String Keyword){
       return null;
   }
   
   public List<Book>searchByAuthor(String Keyword){
       return null;
   }
   
   public List<Book>getAllBooksOrderedByTitle(){
   return null;
   }
   
   private void validateIsbn(String isbn){
   
   }
   
   private void validateTotalCopies(int totalCopies){
   
     }
   
   
    
    
    }
