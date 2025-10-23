package com.dealermanagementsysstem.project.Model;

import utils.DBUtils;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class DAOSaleOrder {

    // ======================================================
    // 1️⃣  TẠO SALE ORDER MỚI
    // ======================================================
    public boolean createSaleOrder(DTOSaleOrder saleOrder) {
        String sqlOrder = "INSERT INTO SaleOrder (CustomerID, DealerID, StaffID, CreatedAt, Status, TotalQuantity, TotalAmount) "
                + "VALUES (?, ?, ?, GETDATE(), ?, ?, ?)";
        String sqlDetail = "INSERT INTO SaleOrderDetail (SaleOrderID, VIN, Price, PolicyID, Quantity) "
                + "VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // ⚙️ Transaction start

            // === Insert SaleOrder ===
            psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, saleOrder.getCustomer().getCustomerID());
            psOrder.setInt(2, saleOrder.getDealer().getDealerID());
            psOrder.setInt(3, saleOrder.getStaff().getStaffID());
            psOrder.setString(4, saleOrder.getStatus());
            psOrder.setInt(5, saleOrder.getDetail().stream().mapToInt(DTOSaleOrderDetail::getQuantity).sum());
            BigDecimal totalAmount = saleOrder.getDetail().stream()
                    .map(d -> d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            psOrder.setBigDecimal(6, totalAmount);

            psOrder.executeUpdate();

            // === Get generated SaleOrderID ===
            rs = psOrder.getGeneratedKeys();
            int saleOrderID = 0;
            if (rs.next()) {
                saleOrderID = rs.getInt(1);
            }

            // === Insert SaleOrderDetails ===
            psDetail = conn.prepareStatement(sqlDetail);
            for (DTOSaleOrderDetail detail : saleOrder.getDetail()) {
                psDetail.setInt(1, saleOrderID);
                psDetail.setString(2, detail.getVehicle().getVIN());
                psDetail.setBigDecimal(3, detail.getPrice());
                psDetail.setInt(4, saleOrder.getDealer().getPolicyID()); // lấy từ dealer
                psDetail.setInt(5, detail.getQuantity());
                psDetail.addBatch();
            }
            psDetail.executeBatch();

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (psOrder != null) psOrder.close();
                if (psDetail != null) psDetail.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    // ======================================================
    // 2️⃣  LẤY TOÀN BỘ SALE ORDERS
    // ======================================================
    public List<DTOSaleOrder> getAllSaleOrders() {
        List<DTOSaleOrder> list = new ArrayList<>();

        String sql = """
            SELECT so.SaleOrderID, so.CreatedAt, so.Status, so.TotalAmount, so.TotalQuantity,
                   c.CustomerID, c.FullName AS CustomerName,
                   d.DealerID, d.DealerName, d.PolicyID,
                   s.StaffID, s.FullName AS StaffName
            FROM SaleOrder so
            JOIN Customer c ON so.CustomerID = c.CustomerID
            JOIN Dealer d ON so.DealerID = d.DealerID
            JOIN DealerStaff s ON so.StaffID = s.StaffID
            ORDER BY so.SaleOrderID DESC
        """;

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DTOSaleOrder order = new DTOSaleOrder();
                order.setSaleOrderID(rs.getInt("SaleOrderID"));
                order.setCreatedAt(rs.getTimestamp("CreatedAt"));
                order.setStatus(rs.getString("Status"));

                // Customer
                DTOCustomer customer = new DTOCustomer();
                customer.setCustomerID(rs.getInt("CustomerID"));
                customer.setFullName(rs.getString("CustomerName"));
                order.setCustomer(customer);

                // Dealer
                DTODealer dealer = new DTODealer();
                dealer.setDealerID(rs.getInt("DealerID"));
                dealer.setDealerName(rs.getString("DealerName"));
                dealer.setPolicyID(rs.getInt("PolicyID"));
                order.setDealer(dealer);

                // Staff
                DTODealerStaff staff = new DTODealerStaff();
                staff.setStaffID(rs.getInt("StaffID"));
                staff.setFullName(rs.getString("StaffName"));
                order.setStaff(staff);

                // Total
                order.setDetail(getSaleOrderDetails(order.getSaleOrderID()));
                list.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ======================================================
    // 3️⃣  LẤY SALE ORDER THEO ID
    // ======================================================
    public DTOSaleOrder getSaleOrderById(int id) {
        DTOSaleOrder order = null;
        String sql = """
            SELECT so.SaleOrderID, so.CreatedAt, so.Status, so.TotalAmount, so.TotalQuantity,
                   c.CustomerID, c.FullName AS CustomerName,
                   d.DealerID, d.DealerName, d.PolicyID,
                   s.StaffID, s.FullName AS StaffName
            FROM SaleOrder so
            JOIN Customer c ON so.CustomerID = c.CustomerID
            JOIN Dealer d ON so.DealerID = d.DealerID
            JOIN DealerStaff s ON so.StaffID = s.StaffID
            WHERE so.SaleOrderID = ?
        """;

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                order = new DTOSaleOrder();
                order.setSaleOrderID(rs.getInt("SaleOrderID"));
                order.setCreatedAt(rs.getTimestamp("CreatedAt"));
                order.setStatus(rs.getString("Status"));

                DTOCustomer c = new DTOCustomer();
                c.setCustomerID(rs.getInt("CustomerID"));
                c.setFullName(rs.getString("CustomerName"));
                order.setCustomer(c);

                DTODealer d = new DTODealer();
                d.setDealerID(rs.getInt("DealerID"));
                d.setDealerName(rs.getString("DealerName"));
                d.setPolicyID(rs.getInt("PolicyID"));
                order.setDealer(d);

                DTODealerStaff s = new DTODealerStaff();
                s.setStaffID(rs.getInt("StaffID"));
                s.setFullName(rs.getString("StaffName"));
                order.setStaff(s);

                order.setDetail(getSaleOrderDetails(id));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    // ======================================================
    // 4️⃣  LẤY CHI TIẾT ĐƠN HÀNG
    // ======================================================
    public List<DTOSaleOrderDetail> getSaleOrderDetails(int saleOrderID) {
        List<DTOSaleOrderDetail> details = new ArrayList<>();

        String sql = """
            SELECT sod.SODetailID, sod.SaleOrderID, sod.VIN, sod.Price, sod.Quantity,
                   v.VehicleID, v.Model, v.Color, v.Brand
            FROM SaleOrderDetail sod
            JOIN Vehicle v ON sod.VIN = v.VIN
            WHERE sod.SaleOrderID = ?
        """;

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, saleOrderID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DTOVehicle vehicle = new DTOVehicle();
                vehicle.setVIN(rs.getString("VIN"));
                vehicle.setModelID(rs.getInt("Model"));
                vehicle.setModelName(rs.getString("Brand"));
                vehicle.setColorName(rs.getString("Color"));

                DTOSaleOrderDetail detail = new DTOSaleOrderDetail();
                detail.setSoDetailID(rs.getInt("SODetailID"));
                detail.setSaleOrderID(rs.getInt("SaleOrderID"));
                detail.setVehicle(vehicle);
                detail.setPrice(rs.getBigDecimal("Price"));
                detail.setQuantity(rs.getInt("Quantity"));

                details.add(detail);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
}
