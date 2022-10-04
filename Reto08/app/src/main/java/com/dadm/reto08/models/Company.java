package com.dadm.reto08.models;

import java.util.List;

public class Company {

    private long id;
    private String name;
    private String webPage;
    private String phoneNumber;
    private String email;
    private String productsServices;
    private String classification;

    public Company(String name, String classification) {
        this.name = name;
        this.classification = classification;
    }

    public Company(long id, String name, String webPage, String phoneNumber, String email, String productsServices, String classification) {
        this.id = id;
        this.name = name;
        this.webPage = webPage;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.productsServices = productsServices;
        this.classification = classification;
    }

    public Company(String name, String webPage, String phoneNumber, String email, String productsServices, String classification) {
        this.name = name;
        this.webPage = webPage;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.productsServices = productsServices;
        this.classification = classification;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebPage() {
        return webPage;
    }

    public void setWebPage(String webPage) {
        this.webPage = webPage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProductsServices() {
        return productsServices;
    }

    public void setProductsServices(String productsServices) {
        this.productsServices = productsServices;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", webPage='" + webPage + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", productsServices='" + productsServices + '\'' +
                ", classification='" + classification + '\'' +
                '}';
    }
}
