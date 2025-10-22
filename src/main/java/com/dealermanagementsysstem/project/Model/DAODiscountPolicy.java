package com.dealermanagementsysstem.project.Model;

import utils.DBUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAODiscountPolicy {

    // ✅ Lấy LevelID theo DealerID
    private Integer getLevelIdByDealerId(int dealerId) {
        String sql = "SELECT LevelID FROM Dealer WHERE DealerID = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dealerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("LevelID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ✅ Tạo Discount Policy mới
    public boolean createDiscountPolicy(DTODiscountPolicy dto) {
        String sql = "INSERT INTO DiscountPolicy " +
                "(DealerID, PolicyName, Description, StartDate, EndDate, HangPercent, DailyPercent, Status, CreatedAt, LevelID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            Integer levelID = getLevelIdByDealerId(dto.getDealerID());
            if (levelID == null) {
                System.out.println("⚠️ Không tìm thấy LevelID cho DealerID: " + dto.getDealerID());
                return false;
            }

            ps.setInt(1, dto.getDealerID());
            ps.setString(2, dto.getPolicyName());
            ps.setString(3, dto.getDescription());
            ps.setDate(4, Date.valueOf(dto.getStartDate()));
            ps.setDate(5, Date.valueOf(dto.getEndDate()));
            ps.setDouble(6, dto.getHangPercent());
            ps.setDouble(7, dto.getDailyPercent());
            ps.setString(8, dto.getStatus());
            ps.setInt(9, levelID);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("❌ Lỗi khi thêm Discount Policy: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Lấy tất cả policy (không cần DealerID)
    public List<DTODiscountPolicy> getAllPolicies() {
        List<DTODiscountPolicy> list = new ArrayList<>();
        String sql = "SELECT * FROM DiscountPolicy ORDER BY CreatedAt DESC";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DTODiscountPolicy dto = new DTODiscountPolicy();
                dto.setPolicyID(rs.getInt("PolicyID"));
                dto.setDealerID(rs.getInt("DealerID"));
                dto.setPolicyName(rs.getString("PolicyName"));
                dto.setDescription(rs.getString("Description"));
                dto.setStartDate(rs.getDate("StartDate").toLocalDate());
                dto.setEndDate(rs.getDate("EndDate").toLocalDate());
                dto.setHangPercent(rs.getDouble("HangPercent"));
                dto.setDailyPercent(rs.getDouble("DailyPercent"));
                dto.setStatus(rs.getString("Status"));
                dto.setCreationDate(rs.getDate("CreatedAt")); // ✅ đúng tên cột
                dto.setLevelID(rs.getInt("LevelID"));
                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Search theo tên Policy
    public List<DTODiscountPolicy> searchPolicyByName(String keyword) {
        List<DTODiscountPolicy> list = new ArrayList<>();
        String sql = "SELECT * FROM DiscountPolicy WHERE PolicyName LIKE ? ORDER BY CreatedAt DESC";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DTODiscountPolicy dto = new DTODiscountPolicy();
                dto.setPolicyID(rs.getInt("PolicyID"));
                dto.setDealerID(rs.getInt("DealerID"));
                dto.setPolicyName(rs.getString("PolicyName"));
                dto.setDescription(rs.getString("Description"));
                dto.setStartDate(rs.getDate("StartDate").toLocalDate());
                dto.setEndDate(rs.getDate("EndDate").toLocalDate());
                dto.setHangPercent(rs.getDouble("HangPercent"));
                dto.setDailyPercent(rs.getDouble("DailyPercent"));
                dto.setStatus(rs.getString("Status"));
                dto.setCreationDate(rs.getDate("CreatedAt"));
                dto.setLevelID(rs.getInt("LevelID"));
                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
