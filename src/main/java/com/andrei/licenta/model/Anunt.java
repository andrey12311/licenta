package com.andrei.licenta.model;

import com.andrei.licenta.model.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Anunt implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    @Column(nullable = false)
    private LocalDate dateAdded;
    @Column(nullable = false, length = 1000)
    private String description;
    @Column(nullable = false, length = 50)
    private String title;
    @Column(nullable = false, length = 20)
    private String city;
    @Column(nullable = false)
    private String species;
    @Column(nullable = false, length = 20)
    private String county;
    @Column(nullable = false)
    private String phoneNumber;
    @Column
    private String image;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false)
    private String anuntId;
    @Column(nullable = false)
    private boolean isAccepted;


    public Anunt() {
    }

    public Anunt(Long id, LocalDate dateAdded, String description, String title, String city, String species,
                 String county, String phoneNumber, String image, User user, String anuntId, boolean isAccepted) {
        this.id = id;
        this.dateAdded = dateAdded;
        this.description = description;
        this.title = title;
        this.city = city;
        this.species = species;
        this.county = county;
        this.phoneNumber = phoneNumber;
        this.image = image;
        this.user = user;
        this.anuntId = anuntId;
        this.isAccepted = isAccepted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAnuntId() {
        return anuntId;
    }

    public void setAnuntId(String anuntId) {
        this.anuntId = anuntId;
    }

    public boolean getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}
