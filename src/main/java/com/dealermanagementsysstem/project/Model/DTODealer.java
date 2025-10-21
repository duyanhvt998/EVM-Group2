package com.dealermanagementsysstem.project.Model;

public class DTODealer {
    private int dealerID;
    private String dealerName;
    private String address;
    private String phone;
    private String email;
    private int evmID;
    private int levelID;
    private int policyID;
    private String status;

    public DTODealer() {}

    public DTODealer(int dealerID, String dealerName, String address, String phone, String email,
                     int evmID, int levelID, int policyID, String status) {
        this.dealerID = dealerID;
        this.dealerName = dealerName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.evmID = evmID;
        this.levelID = levelID;
        this.policyID = policyID;
        this.status = status;
    }

    public int getDealerID() { return dealerID; }
    public void setDealerID(int dealerID) { this.dealerID = dealerID; }

    public String getDealerName() { return dealerName; }
    public void setDealerName(String dealerName) { this.dealerName = dealerName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getEvmID() { return evmID; }
    public void setEvmID(int evmID) { this.evmID = evmID; }

    public int getLevelID() { return levelID; }
    public void setLevelID(int levelID) { this.levelID = levelID; }

    public int getPolicyID() { return policyID; }
    public void setPolicyID(int policyID) { this.policyID = policyID; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
