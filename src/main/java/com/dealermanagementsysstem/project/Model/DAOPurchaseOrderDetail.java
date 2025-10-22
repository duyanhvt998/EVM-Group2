package com.dealermanagementsysstem.project.Model;

import org.springframework.stereotype.Repository;
import utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class DAOPurchaseOrderDetail {

    public boolean insertOrderDetail(int purchaseOrderId, int modelId, int colorId, int quantity, String version) {
        String sql = "INSERT INTO PurchaseOrderDetail (PurchaseOrderID, ModelID, ColorID, Quantity, Version) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, purchaseOrderId);
            ps.setInt(2, modelId);
            ps.setInt(3, colorId);
            ps.setInt(4, quantity);
            ps.setString(5, version);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
