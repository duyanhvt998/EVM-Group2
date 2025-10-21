package com.dealermanagementsysstem.project.Model;

import java.time.LocalDate;

public class DTODealerPriceAdjustment {

    private int adjustmentID;
    private int dealerID;
    private int modelID;
    private Double discountAmount;
    private Double discountPercent;
    private LocalDate startDate;
    private LocalDate endDate;
    private String notes;
    private String promotionName;


    public DTODealerPriceAdjustment() {}

    public DTODealerPriceAdjustment(int adjustmentID, int dealerID, int modelID,
                                    Double discountAmount, Double discountPercent,
                                    LocalDate startDate, LocalDate endDate,
                                    String notes, String promotionName) {
        this.adjustmentID = adjustmentID;
        this.dealerID = dealerID;
        this.modelID = modelID;
        this.discountAmount = discountAmount;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
        this.promotionName = promotionName;
    }

    public int getAdjustmentID() {
        return adjustmentID;
    }

    public void setAdjustmentID(int adjustmentID) {
        this.adjustmentID = adjustmentID;
    }

    public int getDealerID() {
        return dealerID;
    }

    public void setDealerID(int dealerID) {
        this.dealerID = dealerID;
    }

    public int getModelID() {
        return modelID;
    }

    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }
}
