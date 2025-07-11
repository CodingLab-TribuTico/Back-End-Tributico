package com.project.demo.logic.entity.detailsBill;

import jakarta.persistence.*;
import com.project.demo.logic.entity.electronicBill.ElectronicBill;

@Entity
@Table(name = "details_bill")
public class DetailsBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int detailCode;
    private String detailDescription;
    private double quantity;
    private double unitPrice;
    private double unit;
    private double tax;
    private double taxAmount;
    private double total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "electronic_bill_id", nullable = false)
    private ElectronicBill electronicBill;

    public DetailsBill() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDetailCode() {
        return detailCode;
    }

    public void setDetailCode(int detailCode) {
        this.detailCode = detailCode;
    }

    public String getDetailDescription() {
        return detailDescription;
    }

    public void setDetailDescription(String detailDescription) {
        this.detailDescription = detailDescription;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getUnit() {
        return unit;
    }

    public void setUnit(double unit) {
        this.unit = unit;
    }

    public ElectronicBill getElectronicBill() {
        return electronicBill;
    }

    public void setElectronicBill(ElectronicBill electronicBill) {
        this.electronicBill = electronicBill;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
