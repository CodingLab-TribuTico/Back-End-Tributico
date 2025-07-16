package com.project.demo.logic.entity.xml;

import jakarta.persistence.*;

@Table(name = "bill")
@Entity
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nombreReceptor;

    public Bill() {
    }

    public Bill(long id, String nombreReceptor) {
        this.id = id;
        this.nombreReceptor = nombreReceptor;
    }

    public Bill(String nombreReceptor) {
        this.nombreReceptor = nombreReceptor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombreReceptor() {
        return nombreReceptor;
    }

    public void setNombreReceptor(String nombreReceptor) {
        this.nombreReceptor = nombreReceptor;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", nombreReceptor='" + nombreReceptor + '\'' +
                '}';
    }
}
