package com.dealermanagementsysstem.project.Model;

import java.util.Date;

public class DTOEVMVehicle {
    private String VIN;
    private int modelID;
    private int versionID;
    private int colorID;
    private Date manufactureDate;
    private String status;
    private int evmID;

    // Thêm các đối tượng con
    private DTOEVMVehicleModel model;
    private DTOEVMVehicleVersion version;
    private DTOEVMVehicleColor color;

    public DTOEVMVehicle() {
    }

    public DTOEVMVehicle(String VIN, int modelID, int versionID, int colorID,
                         Date manufactureDate, String status, int evmID,
                         DTOEVMVehicleModel model, DTOEVMVehicleVersion version, DTOEVMVehicleColor color) {
        this.VIN = VIN;
        this.modelID = modelID;
        this.versionID = versionID;
        this.colorID = colorID;
        this.manufactureDate = manufactureDate;
        this.status = status;
        this.evmID = evmID;
        this.model = model;
        this.version = version;
        this.color = color;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public int getModelID() {
        return modelID;
    }

    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    public int getVersionID() {
        return versionID;
    }

    public void setVersionID(int versionID) {
        this.versionID = versionID;
    }

    public int getColorID() {
        return colorID;
    }

    public void setColorID(int colorID) {
        this.colorID = colorID;
    }

    public Date getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(Date manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getEvmID() {
        return evmID;
    }

    public void setEvmID(int evmID) {
        this.evmID = evmID;
    }

    public DTOEVMVehicleModel getModel() {
        return model;
    }

    public void setModel(DTOEVMVehicleModel model) {
        this.model = model;
    }

    public DTOEVMVehicleVersion getVersion() {
        return version;
    }

    public void setVersion(DTOEVMVehicleVersion version) {
        this.version = version;
    }

    public DTOEVMVehicleColor getColor() {
        return color;
    }

    public void setColor(DTOEVMVehicleColor color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "DTOEVMVehicle{" +
                "VIN='" + VIN + '\'' +
                ", modelID=" + modelID +
                ", versionID=" + versionID +
                ", colorID=" + colorID +
                ", manufactureDate=" + manufactureDate +
                ", status='" + status + '\'' +
                ", evmID=" + evmID +
                ", model=" + model +
                ", version=" + version +
                ", color=" + color +
                '}';
    }
}
