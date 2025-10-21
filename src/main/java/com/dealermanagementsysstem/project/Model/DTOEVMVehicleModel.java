package com.dealermanagementsysstem.project.Model;

import java.util.Base64;

public class DTOEVMVehicleModel {

    private int modelID;
    private String modelName;
    private String brand;
    private String bodyType;
    private int year;
    private String description;
    private int evmID;
    private double basePrice;
    private byte[] modelImage; // ✅ đúng kiểu byte[]
    private Integer mergedToID; // có thể null

    public DTOEVMVehicleModel() {
    }

    public DTOEVMVehicleModel(int modelID, String modelName, String brand, String bodyType, int year,
                              String description, int evmID, double basePrice, byte[] modelImage, Integer mergedToID) {
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

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    // ✅ getter/setter đúng kiểu byte[]
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

    // ✅ Dùng cho Thymeleaf hiển thị ảnh
    public String getModelImageBase64() {
        if (modelImage == null || modelImage.length == 0) return null;
        return Base64.getEncoder().encodeToString(modelImage);
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
                ", modelImage=" + (modelImage != null ? "[BLOB]" : "null") +
                ", mergedToID=" + mergedToID +
                '}';
    }
}
