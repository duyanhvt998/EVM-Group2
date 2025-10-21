package com.dealermanagementsysstem.project.Model;

import java.math.BigDecimal;

public class DTOEVMVehicleVersion {

    private int versionID;
    private int modelID;
    private String versionName;
    private String engine;
    private String transmission;
    private BigDecimal price; // ✅ đổi từ double → BigDecimal

    public DTOEVMVehicleVersion() {
    }

    public DTOEVMVehicleVersion(int versionID, int modelID, String versionName, String engine, String transmission, BigDecimal price) {
        this.versionID = versionID;
        this.modelID = modelID;
        this.versionName = versionName;
        this.engine = engine;
        this.transmission = transmission;
        this.price = price;
    }

    public int getVersionID() {
        return versionID;
    }

    public void setVersionID(int versionID) {
        this.versionID = versionID;
    }

    public int getModelID() {
        return modelID;
    }

    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "VehicleVersion{" +
                "versionID=" + versionID +
                ", modelID=" + modelID +
                ", versionName='" + versionName + '\'' +
                ", engine='" + engine + '\'' +
                ", transmission='" + transmission + '\'' +
                ", price=" + (price != null ? price.toPlainString() : "null") +
                '}';
    }
}
