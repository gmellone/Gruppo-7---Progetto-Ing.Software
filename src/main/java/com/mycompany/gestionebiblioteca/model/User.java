/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gestionebiblioteca.model;

/**
 *
 * @author Giovanni
 */
import java.util.Objects;

public class User {

    private String matricola;
    private String firstName;
    private String lastName;
    private String email;

    public User(String matricola, String firstName, String lastName, String email) {
        this.matricola = Objects.requireNonNull(matricola);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    //GETTER
    public String getMatricola() { return matricola; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }

    //SETTER
    public void setMatricola(String matricola) { this.matricola = matricola; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return matricola.equals(user.matricola);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matricola);
    }
}
