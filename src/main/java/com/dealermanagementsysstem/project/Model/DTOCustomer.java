package com.dealermanagementsysstem.project.Model;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DTOCustomer {
    private int customerID;
    private String fullName;
    private String phone;
    private String email;
    private String address;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String note;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime testDriveSchedule;

    private String vehicleInterest;

    // === GETTER / SETTER ===
    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public String getPhoneNumber() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getTestDriveSchedule() { return testDriveSchedule; }
    public void setTestDriveSchedule(LocalDateTime testDriveSchedule) { this.testDriveSchedule = testDriveSchedule; }

    public String getVehicleInterest() { return vehicleInterest; }
    public void setVehicleInterest(String vehicleInterest) { this.vehicleInterest = vehicleInterest; }
}
