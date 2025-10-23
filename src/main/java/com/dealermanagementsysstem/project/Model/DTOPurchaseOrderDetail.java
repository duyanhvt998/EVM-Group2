package com.dealermanagementsysstem.project.Model;

public class DTOPurchaseOrderDetail {

    private int poDetailId;
    private int purchaseOrderId;
    private int colorId;
    private int quantity;
    private int modelId;
    private String version;
    // thêm để hiển thị tên model / color nếu cần
    private String modelName;
    private String colorName;

    public DTOPurchaseOrderDetail() {}

    // getters / setters - ví dụ:
    public int getPoDetailId() { return poDetailId; }
    public void setPoDetailId(int poDetailId) { this.poDetailId = poDetailId; }

    public int getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(int purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }

    public int getColorId() { return colorId; }
    public void setColorId(int colorId) { this.colorId = colorId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getModelId() { return modelId; }
    public void setModelId(int modelId) { this.modelId = modelId; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getColorName() { return colorName; }
    public void setColorName(String colorName) { this.colorName = colorName; }
}
