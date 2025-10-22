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
        return null; // nếu không có
    }

    // ✅ Tạo Discount Policy mới
    public boolean createDiscountPolicy(DTODiscountPolicy dto) {
        String sql = "INSERT INTO DiscountPolicy " +
                "(DealerID, PolicyName, Description, StartDate, EndDate, HangPercent, DailyPercent, Status, CreationDate, LevelID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 🔹 Lấy LevelID dựa theo DealerID
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
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Lấy danh sách policy theo DealerID
    public List<DTODiscountPolicy> getPoliciesByDealer(int dealerID) {
        List<DTODiscountPolicy> list = new ArrayList<>();
        String sql = "SELECT * FROM DiscountPolicy WHERE DealerID = ? ORDER BY StartDate DESC";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dealerID);
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
                dto.setCreationDate(rs.getDate("CreationDate"));
                dto.setLevelID(rs.getInt("LevelID"));
                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Search theo tên policy và DealerID
    public List<DTODiscountPolicy> searchPolicyByName(String name, int dealerID) {
        List<DTODiscountPolicy> list = new ArrayList<>();
        String sql = "SELECT * FROM DiscountPolicy WHERE DealerID = ? AND PolicyName LIKE ?";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dealerID);
            ps.setString(2, "%" + name + "%");

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
                dto.setCreationDate(rs.getDate("CreationDate"));
                dto.setLevelID(rs.getInt("LevelID"));
                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
