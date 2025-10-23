package com.dealermanagementsysstem.project.Model;

import java.math.BigDecimal;

public class DTOSaleOrderDetail {
    private int soDetailID;
    private int saleOrderID;
    private DTOVehicle vehicle;
    private BigDecimal price;
    private int quantity;

    public DTOSaleOrderDetail() {
    }

    public DTOSaleOrderDetail(int soDetailID, int saleOrderID, DTOVehicle vehicle, BigDecimal price, int quantity) {
        this.soDetailID = soDetailID;
        this.saleOrderID = saleOrderID;
        this.vehicle = vehicle;
        this.price = price;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSoDetailID() {
        return soDetailID;
    }

    public void setSoDetailID(int soDetailID) {
        this.soDetailID = soDetailID;
    }

    public int getSaleOrderID() {
        return saleOrderID;
    }

    public void setSaleOrderID(int saleOrderID) {
        this.saleOrderID = saleOrderID;
    }

    public DTOVehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(DTOVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


}
