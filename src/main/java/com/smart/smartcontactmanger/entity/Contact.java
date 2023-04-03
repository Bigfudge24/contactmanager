package com.smart.smartcontactmanger.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "contact")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cId;
    @NotBlank(message = "Please Enter your name")
    @Size(min = 5,max = 20,message = "Name should be between 5 to 20 characters")
    private String Name;
    @NotBlank(message = "Please Enter your nickname name")
    @Size(min = 5,max = 20,message = "Nick Name should be between 5 to 20 characters")
    private String secondName;
    @NotBlank(message = "Please Enter your work(occupation)")
    @Size(min = 3,max = 30,message = "Work should be between 3 to 30 characters")
    private String work;
    @Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$",message = "Invalid Email!!")
    @Column(unique = true)
    private String email;
    @NotBlank(message = "Please enter your phone number")
    private String phoneNumber;
    private String image;
    @Column(length = 5000)
    @NotBlank(message = "Please enter description")
    @Size(min = 5,max = 1000,message = "Description shoukd be between 5 to 1000 characters")
    private String description;
    @ManyToOne
    @JsonIgnore
    private User user;

    public Contact(){

    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Contact [cId=" + cId + ", Name=" + Name + ", secondName=" + secondName + ", work=" + work + ", email="
                + email + ", phoneNumber=" + phoneNumber + ", image=" + image + ", description=" + description + "]";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
}
