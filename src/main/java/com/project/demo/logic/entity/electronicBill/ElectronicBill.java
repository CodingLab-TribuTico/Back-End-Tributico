package com.project.demo.logic.entity.electronicBill;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDate;

@Table(name = "electronic_bill")
@Entity
public class ElectronicBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty("invoice_Number")
    private String invoiceNumber;
    @JsonProperty("invoice_Date")
    private LocalDate issueDate;
    private String type;
    @Column(unique = true, length = 9, nullable = false)
    private String identification;
    private String name;
    private String lastname;
    private String lastname2;
    @JsonProperty("taxable_Amount")
    private Double taxableAmount; //Monto agravado
    @JsonProperty("exempt_Amount")
    private Double exemptAmount; //Monto exento
    @JsonProperty("vat_Amount")
    private Double vatAmount; // Monto IVA
    @JsonProperty("total_Amount")
    private Double totalAmount; // Monto total

    public ElectronicBill() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLastname2() {
        return lastname2;
    }

    public void setLastname2(String lastname2) {
        this.lastname2 = lastname2;
    }

    public Double getTaxableAmount() {
        return taxableAmount;
    }

    public void setTaxableAmount(Double taxableAmount) {
        this.taxableAmount = taxableAmount;
    }

    public Double getExemptAmount() {
        return exemptAmount;
    }

    public void setExemptAmount(Double exemptAmount) {
        this.exemptAmount = exemptAmount;
    }

    public Double getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(Double vatAmount) {
        this.vatAmount = vatAmount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
