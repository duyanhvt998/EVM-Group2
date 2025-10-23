package com.dealermanagementsysstem.project.Model;

import java.sql.Timestamp;
import java.util.List;

public class DTOQuotation {
    private int quotationID;
    private DTODealer dealer;
    private DTOCustomer customer;
    private DTOVehicle vehicle;
    private Timestamp createdAt;
    private String status;
    private DTODiscountPolicy discountPolicy;
    private double totalPrice;
    private List<DTOQuotationDetail> quotationDetails;
    // New fields for extended pricing logic
    private int quantity = 1; // default single unit
    private Double extraDiscountPercent; // optional extra discount chosen in UI
    private DTODealerStaff staff; // staff who created the quotation

    public DTOQuotation() {
    }

    public DTOQuotation(int quotationID, DTODealer dealer, DTOCustomer customer,
                        DTOVehicle vehicle, Timestamp createdAt, String status,
                        DTODiscountPolicy discountPolicy, double totalPrice) {
        this.quotationID = quotationID;
        this.dealer = dealer;
        this.customer = customer;
        this.vehicle = vehicle;
        this.createdAt = createdAt;
        this.status = status;
        this.discountPolicy = discountPolicy;
        this.totalPrice = totalPrice;
    }

    public int getQuotationID() {
        return quotationID;
    }

    public void setQuotationID(int quotationID) {
        this.quotationID = quotationID;
    }

    public DTODealer getDealer() {
        return dealer;
    }

    public void setDealer(DTODealer dealer) {
        this.dealer = dealer;
    }

    public DTOCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(DTOCustomer customer) {
        this.customer = customer;
    }

    public DTOVehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(DTOVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DTODiscountPolicy getDiscountPolicy() {
        return discountPolicy;
    }

    public void setDiscountPolicy(DTODiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<DTOQuotationDetail> getQuotationDetails() {
        return quotationDetails;
    }

    public void setQuotationDetails(List<DTOQuotationDetail> quotationDetails) {
        this.quotationDetails = quotationDetails;
    }

    // Extended fields accessors
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getExtraDiscountPercent() {
        return extraDiscountPercent;
    }

    public void setExtraDiscountPercent(Double extraDiscountPercent) {
        this.extraDiscountPercent = extraDiscountPercent;
    }

    public DTODealerStaff getStaff() {
        return staff;
    }

    public void setStaff(DTODealerStaff staff) {
        this.staff = staff;
    }
}
