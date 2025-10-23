package com.dealermanagementsysstem.project.Model;

import java.math.BigDecimal;

public class DTOQuotationDetail {
    private int quotationDetailID;
    private int quotationID;
    private String VIN;
    private BigDecimal unitPrice;
    private int quantity;
    private int colorID;
    private String colorName;
    private String modelName;

    public DTOQuotationDetail() {
    }

    public DTOQuotationDetail(int quotationDetailID, int quotationID, String VIN, 
                             BigDecimal unitPrice, int quantity, int colorID, 
                             String colorName, String modelName) {
        this.quotationDetailID = quotationDetailID;
        this.quotationID = quotationID;
        this.VIN = VIN;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.colorID = colorID;
        this.colorName = colorName;
        this.modelName = modelName;
    }

    public int getQuotationDetailID() {
        return quotationDetailID;
    }

    public void setQuotationDetailID(int quotationDetailID) {
        this.quotationDetailID = quotationDetailID;
    }

    public int getQuotationID() {
        return quotationID;
    }

    public void setQuotationID(int quotationID) {
        this.quotationID = quotationID;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getColorID() {
        return colorID;
    }

    public void setColorID(int colorID) {
        this.colorID = colorID;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
