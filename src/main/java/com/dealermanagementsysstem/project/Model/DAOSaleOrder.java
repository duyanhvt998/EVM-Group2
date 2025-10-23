package com.dealermanagementsysstem.project.Model;

import utils.DBUtils;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DAOSaleOrder {

    private static final Logger log = LoggerFactory.getLogger(DAOSaleOrder.class);

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
            log.debug("Creating SaleOrder for customerId={} dealerId={} staffId={}",
                    saleOrder.getCustomer().getCustomerID(),
                    saleOrder.getDealer().getDealerID(),
                    saleOrder.getStaff().getStaffID());

            // === Insert SaleOrder ===
            psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, saleOrder.getCustomer().getCustomerID());
            psOrder.setInt(2, saleOrder.getDealer().getDealerID());
            psOrder.setInt(3, saleOrder.getStaff().getStaffID());
            psOrder.setString(4, saleOrder.getStatus());
        int totalQuantity = saleOrder.getDetail().stream().mapToInt(DTOSaleOrderDetail::getQuantity).sum();
        psOrder.setInt(5, totalQuantity);
        BigDecimal totalAmount = saleOrder.getDetail().stream()
                    .map(d -> d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            psOrder.setBigDecimal(6, totalAmount);

            psOrder.executeUpdate();
            log.trace("Inserted SaleOrder main row");

            // === Get generated SaleOrderID ===
            rs = psOrder.getGeneratedKeys();
            int saleOrderID = 0;
            if (rs.next()) {
                saleOrderID = rs.getInt(1);
                log.info("Generated SaleOrderID={}", saleOrderID);
            }

            // Update aggregated fields on DTO for downstream view usage
            saleOrder.setSaleOrderID(saleOrderID);
            saleOrder.setTotalQuantity(totalQuantity);
            saleOrder.setTotalAmount(totalAmount);

            // === Insert SaleOrderDetails ===
            psDetail = conn.prepareStatement(sqlDetail);
            for (DTOSaleOrderDetail detail : saleOrder.getDetail()) {
                psDetail.setInt(1, saleOrderID);
                psDetail.setString(2, detail.getVehicle().getVIN());
                psDetail.setBigDecimal(3, detail.getPrice());
                psDetail.setInt(4, saleOrder.getDealer().getPolicyID()); // lấy từ dealer
                psDetail.setInt(5, detail.getQuantity());
                psDetail.addBatch();
                log.trace("Queued SaleOrderDetail VIN={} qty={} price={}", detail.getVehicle().getVIN(), detail.getQuantity(), detail.getPrice());
            }
            psDetail.executeBatch();
            log.debug("Inserted {} SaleOrderDetail rows", saleOrder.getDetail().size());

            conn.commit();
            log.info("SaleOrder committed id={}", saleOrderID);
            return true;

        } catch (SQLException e) {
            log.error("Error creating SaleOrder - performing rollback", e);
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                log.error("Rollback failed", ex);
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (psOrder != null) psOrder.close();
                if (psDetail != null) psDetail.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                log.error("Error closing resources", ex);
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
                order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                order.setTotalQuantity(rs.getInt("TotalQuantity"));

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
            log.error("Error retrieving all SaleOrders", e);
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
                order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                order.setTotalQuantity(rs.getInt("TotalQuantity"));

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
            log.error("Error retrieving SaleOrder by id={}", id, e);
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
                   v.ManufactureYear, v.ColorID, vm.ModelID, vm.ModelName, vm.BasePrice,
                   vc.ColorName
            FROM SaleOrderDetail sod
            JOIN Vehicle v ON sod.VIN = v.VIN
            LEFT JOIN VehicleModel vm ON v.ModelID = vm.ModelID
            LEFT JOIN VehicleColor vc ON v.ColorID = vc.ColorID
            WHERE sod.SaleOrderID = ?
        """;

       try (Connection conn = DBUtils.getConnection();
           PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, saleOrderID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DTOVehicle vehicle = new DTOVehicle();
                vehicle.setVIN(rs.getString("VIN"));
                vehicle.setManufactureYear(rs.getInt("ManufactureYear"));
                vehicle.setColorID(rs.getInt("ColorID"));
                vehicle.setColorName(rs.getString("ColorName"));
                vehicle.setModelID(rs.getInt("ModelID"));
                vehicle.setModelName(rs.getString("ModelName"));
                vehicle.setBasePrice(rs.getBigDecimal("BasePrice"));

                DTOSaleOrderDetail detail = new DTOSaleOrderDetail();
                detail.setSoDetailID(rs.getInt("SODetailID"));
                detail.setSaleOrderID(rs.getInt("SaleOrderID"));
                detail.setVehicle(vehicle);
                detail.setPrice(rs.getBigDecimal("Price"));
                detail.setQuantity(rs.getInt("Quantity"));

                details.add(detail);
            }

        } catch (SQLException e) {
            log.error("Error retrieving SaleOrder details saleOrderID={}", saleOrderID, e);
        }
        return details;
    }
}
