package com.dealermanagementsysstem.project.Model;

import java.math.BigDecimal;

public class DTOEVMVehicleModel {

    private int modelID;
    private String modelName;
    private String brand;
    private String bodyType;
    private int year;
    private String description;
    private int evmID;
    private BigDecimal basePrice; // ✅ Đổi từ double -> BigDecimal
    private byte[] modelImage;
    private Integer mergedToID;

    public DTOEVMVehicleModel() {
    }

    public DTOEVMVehicleModel(int modelID, String modelName, String brand, String bodyType, int year,
                              String description, int evmID, BigDecimal basePrice, byte[] modelImage, Integer mergedToID) {
        this.modelID = modelID;
        this.modelName = modelName;
        this.brand = brand;
        this.bodyType = bodyType;
        this.year = year;
        this.description = description;
        this.evmID = evmID;
        this.basePrice = basePrice;
        this.modelImage = modelImage;
        this.mergedToID = mergedToID;
    }

    public int getModelID() {
        return modelID;
    }

    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEvmID() {
        return evmID;
    }

    public void setEvmID(int evmID) {
        this.evmID = evmID;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public byte[] getModelImage() {
        return modelImage;
    }

    public void setModelImage(byte[] modelImage) {
        this.modelImage = modelImage;
    }

    public Integer getMergedToID() {
        return mergedToID;
    }

    public void setMergedToID(Integer mergedToID) {
        this.mergedToID = mergedToID;
    }

    @Override
    public String toString() {
        return "DTOEVMVehicleModel{" +
                "modelID=" + modelID +
                ", modelName='" + modelName + '\'' +
                ", brand='" + brand + '\'' +
                ", bodyType='" + bodyType + '\'' +
                ", year=" + year +
                ", description='" + description + '\'' +
                ", evmID=" + evmID +
                ", basePrice=" + basePrice +
                ", mergedToID=" + mergedToID +
                '}';
    }
}
