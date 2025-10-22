package com.dealermanagementsysstem.project.Model;

import utils.DBUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAODealerInventory {

    public List<DTODealerInventory> getVehiclesByDealerID(int dealerID) {
        List<DTODealerInventory> list = new ArrayList<>();
        String sql = "SELECT DealerID, VIN, ReceivedDate, Status FROM DealerInventory WHERE DealerID = ?";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dealerID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DTODealerInventory dto = new DTODealerInventory(
                        rs.getInt("DealerID"),
                        rs.getString("VIN"),
                        rs.getDate("ReceivedDate"),
                        rs.getString("Status")
                );
                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
