package com.dealermanagementsysstem.project.Model;

public class DTOEVMVehicleColor {
    private int colorID;
    private int modelID;
    private String colorName;

    public DTOEVMVehicleColor() {
    }

    public DTOEVMVehicleColor(int colorID, int modelID, String colorName) {
        this.colorID = colorID;
        this.modelID = modelID;
        this.colorName = colorName;
    }

    public int getColorID() {
        return colorID;
    }

    public void setColorID(int colorID) {
        this.colorID = colorID;
    }

    public int getModelID() {
        return modelID;
    }

    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    @Override
    public String toString() {
        return "VehicleColor{" +
                "colorID=" + colorID +
                ", modelID=" + modelID +
                ", colorName='" + colorName + '\'' +
                '}';
    }
}

