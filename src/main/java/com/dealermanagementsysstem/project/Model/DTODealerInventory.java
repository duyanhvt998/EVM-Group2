package com.dealermanagementsysstem.project.Model;

import java.time.LocalDate;
import java.util.Date;

public class DTODealerInventory {
    private int dealerId;
    private String vin;
    private Date receivedDate;
    private String status;

    // ✅ Constructor mặc định
    public DTODealerInventory() {}

    // ✅ Constructor đầy đủ
    public DTODealerInventory(int dealerId, String vin, Date receivedDate, String status) {
        this.dealerId = dealerId;
        this.vin = vin;
        this.receivedDate = receivedDate;
        this.status = status;
    }

    // ✅ Getters & Setters
    public int getDealerId() {
        return dealerId;
    }

    public void setDealerId(int dealerId) {
        this.dealerId = dealerId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
