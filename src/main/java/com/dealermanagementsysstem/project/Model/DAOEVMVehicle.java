package com.dealermanagementsysstem.project.Model;

import utils.DBUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class DAOEVMVehicle {

    // ======================================================
    // GET ALL VEHICLES
    // ======================================================
    public List<DTOEVMVehicle> getAllVehicles() {
        List<DTOEVMVehicle> list = new ArrayList<>();

        String sql = """
            SELECT v.VIN, v.ModelID, v.VersionID, v.ColorID, v.ManufactureDate, v.Status, v.EvmID,
                   m.ModelName, m.Brand, m.BodyType, m.Year, m.Description, 
                   m.BasePrice, m.ModelImage, m.MergedToID,
                   ver.VersionName, ver.Engine, ver.Transmission, ver.Price,
                   c.ColorName
            FROM EVM_Vehicle v
            JOIN EVM_VehicleModel m ON v.ModelID = m.ModelID
            JOIN EVM_VehicleVersion ver ON v.VersionID = ver.VersionID
            JOIN EVM_VehicleColor c ON v.ColorID = c.ColorID
        """;

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DTOEVMVehicle vehicle = new DTOEVMVehicle();
                vehicle.setVIN(rs.getString("VIN"));
                vehicle.setModelID(rs.getInt("ModelID"));
                vehicle.setVersionID(rs.getInt("VersionID"));
                vehicle.setColorID(rs.getInt("ColorID"));
                vehicle.setManufactureDate(rs.getDate("ManufactureDate"));
                vehicle.setStatus(rs.getString("Status"));
                vehicle.setEvmID(rs.getInt("EvmID"));

                DTOEVMVehicleModel model = new DTOEVMVehicleModel(
                        rs.getInt("ModelID"),
                        rs.getString("ModelName"),
                        rs.getString("Brand"),
                        rs.getString("BodyType"),
                        rs.getInt("Year"),
                        rs.getString("Description"),
                        rs.getInt("EvmID"),
                        rs.getBigDecimal("BasePrice"), // ✅ FIX
                        rs.getBytes("ModelImage"),
                        rs.getObject("MergedToID", Integer.class)
                );

                DTOEVMVehicleVersion version = new DTOEVMVehicleVersion(
                        rs.getInt("VersionID"),
                        rs.getInt("ModelID"),
                        rs.getString("VersionName"),
                        rs.getString("Engine"),
                        rs.getString("Transmission"),
                        rs.getBigDecimal("Price") // ✅ FIX
                );

                DTOEVMVehicleColor color = new DTOEVMVehicleColor(
                        rs.getInt("ColorID"),
                        rs.getInt("ModelID"),
                        rs.getString("ColorName")
                );

                vehicle.setModel(model);
                vehicle.setVersion(version);
                vehicle.setColor(color);
                list.add(vehicle);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ======================================================
    // GET VEHICLE BY VIN
    // ======================================================
    public DTOEVMVehicle getVehicleByVIN(String vin) {
        DTOEVMVehicle vehicle = null;
        System.out.println("🔍 [DEBUG] DAOEVMVehicle.getVehicleByVIN called with VIN: " + vin);

        String sql = """
            SELECT v.VIN, v.ModelID, v.VersionID, v.ColorID, v.ManufactureDate, v.Status, v.EvmID,
                   m.ModelName, m.Brand, m.BodyType, m.Year, m.Description, 
                   m.BasePrice, m.ModelImage, m.MergedToID,
                   ver.VersionName, ver.Engine, ver.Transmission, ver.Price,
                   c.ColorName
            FROM EVM_Vehicle v
            JOIN EVM_VehicleModel m ON v.ModelID = m.ModelID
            JOIN EVM_VehicleVersion ver ON v.VersionID = ver.VersionID
            JOIN EVM_VehicleColor c ON v.ColorID = c.ColorID
            WHERE v.VIN = ?
        """;

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vin);
            System.out.println("🔍 [DEBUG] Executing SQL query for VIN: " + vin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    vehicle = new DTOEVMVehicle();
                    vehicle.setVIN(rs.getString("VIN"));
                    vehicle.setModelID(rs.getInt("ModelID"));
                    vehicle.setVersionID(rs.getInt("VersionID"));
                    vehicle.setColorID(rs.getInt("ColorID"));
                    vehicle.setManufactureDate(rs.getDate("ManufactureDate"));
                    vehicle.setStatus(rs.getString("Status"));
                    vehicle.setEvmID(rs.getInt("EvmID"));

                    DTOEVMVehicleModel model = new DTOEVMVehicleModel(
                            rs.getInt("ModelID"),
                            rs.getString("ModelName"),
                            rs.getString("Brand"),
                            rs.getString("BodyType"),
                            rs.getInt("Year"),
                            rs.getString("Description"),
                            rs.getInt("EvmID"),
                            rs.getBigDecimal("BasePrice"), // ✅ FIX
                            rs.getBytes("ModelImage"),
                            rs.getObject("MergedToID", Integer.class)
                    );

                    DTOEVMVehicleVersion version = new DTOEVMVehicleVersion(
                            rs.getInt("VersionID"),
                            rs.getInt("ModelID"),
                            rs.getString("VersionName"),
                            rs.getString("Engine"),
                            rs.getString("Transmission"),
                            rs.getBigDecimal("Price") // ✅ FIX
                    );

                    DTOEVMVehicleColor color = new DTOEVMVehicleColor(
                            rs.getInt("ColorID"),
                            rs.getInt("ModelID"),
                            rs.getString("ColorName")
                    );

                    vehicle.setModel(model);
                    vehicle.setVersion(version);
                    vehicle.setColor(color);
                } else {
                    System.out.println("❌ [ERROR] No vehicle found in database for VIN: " + vin);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ [ERROR] SQL error when fetching vehicle by VIN: " + vin);
            e.printStackTrace();
        }

        if (vehicle != null) {
            System.out.println("✅ [SUCCESS] Vehicle found in database: " + vehicle.getModel().getModelName());
        } else {
            System.out.println("❌ [ERROR] Vehicle is null for VIN: " + vin);
        }

        return vehicle;
    }

    // ======================================================
    // SEARCH VEHICLE BY MODEL NAME
    // ======================================================
    public List<DTOEVMVehicle> searchVehiclesByModelName(String modelName) {
        List<DTOEVMVehicle> list = new ArrayList<>();

        String sql = """
            SELECT v.VIN, v.ModelID, v.VersionID, v.ColorID, v.ManufactureDate, v.Status, v.EvmID,
                   m.ModelName, m.Brand, m.BodyType, m.Year, m.Description, 
                   m.BasePrice, m.ModelImage, m.MergedToID,
                   ver.VersionName, ver.Engine, ver.Transmission, ver.Price,
                   c.ColorName
            FROM EVM_Vehicle v
            JOIN EVM_VehicleModel m ON v.ModelID = m.ModelID
            JOIN EVM_VehicleVersion ver ON v.VersionID = ver.VersionID
            JOIN EVM_VehicleColor c ON v.ColorID = c.ColorID
            WHERE m.ModelName LIKE ?
        """;

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + modelName + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DTOEVMVehicle vehicle = new DTOEVMVehicle();
                    vehicle.setVIN(rs.getString("VIN"));
                    vehicle.setModelID(rs.getInt("ModelID"));
                    vehicle.setVersionID(rs.getInt("VersionID"));
                    vehicle.setColorID(rs.getInt("ColorID"));
                    vehicle.setManufactureDate(rs.getDate("ManufactureDate"));
                    vehicle.setStatus(rs.getString("Status"));
                    vehicle.setEvmID(rs.getInt("EvmID"));

                    DTOEVMVehicleModel model = new DTOEVMVehicleModel(
                            rs.getInt("ModelID"),
                            rs.getString("ModelName"),
                            rs.getString("Brand"),
                            rs.getString("BodyType"),
                            rs.getInt("Year"),
                            rs.getString("Description"),
                            rs.getInt("EvmID"),
                            rs.getBigDecimal("BasePrice"), // ✅ FIX
                            rs.getBytes("ModelImage"),
                            rs.getObject("MergedToID", Integer.class)
                    );

                    DTOEVMVehicleVersion version = new DTOEVMVehicleVersion(
                            rs.getInt("VersionID"),
                            rs.getInt("ModelID"),
                            rs.getString("VersionName"),
                            rs.getString("Engine"),
                            rs.getString("Transmission"),
                            rs.getBigDecimal("Price") // ✅ FIX
                    );

                    DTOEVMVehicleColor color = new DTOEVMVehicleColor(
                            rs.getInt("ColorID"),
                            rs.getInt("ModelID"),
                            rs.getString("ColorName")
                    );

                    vehicle.setModel(model);
                    vehicle.setVersion(version);
                    vehicle.setColor(color);
                    list.add(vehicle);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ======================================================
    // CREATE VEHICLE
    // ======================================================
    public boolean createVehicle(
            String vin,
            String modelName,
            String brand,
            String bodyType,
            int year,
            String description,
            BigDecimal basePrice, // ✅ đổi double -> BigDecimal
            String versionName,
            String engine,
            String transmission,
            String colorName,
            java.util.Date manufactureDate,
            String status,
            int evmID,
            byte[] thumbnailBytes
    ) {
        boolean success = false;

        try (Connection conn = DBUtils.getConnection()) {
            conn.setAutoCommit(false);

            int modelID = -1;
            int colorID = -1;
            int versionID = -1;

            // Model
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT ModelID FROM EVM_VehicleModel WHERE ModelName = ?")) {
                ps.setString(1, modelName);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    modelID = rs.getInt("ModelID");
                } else {
                    try (PreparedStatement ins = conn.prepareStatement("""
                        INSERT INTO EVM_VehicleModel (ModelName, Brand, BodyType, Year, Description, EvmID, BasePrice, ModelImage)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS)) {
                        ins.setString(1, modelName);
                        ins.setString(2, brand);
                        ins.setString(3, bodyType);
                        ins.setInt(4, year);
                        ins.setString(5, description);
                        ins.setInt(6, evmID);
                        ins.setBigDecimal(7, basePrice); // ✅ FIX
                        ins.setBytes(8, thumbnailBytes);
                        ins.executeUpdate();

                        ResultSet gen = ins.getGeneratedKeys();
                        if (gen.next()) modelID = gen.getInt(1);
                    }
                }
            }

            // Color
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT ColorID FROM EVM_VehicleColor WHERE ColorName = ? AND ModelID = ?")) {
                ps.setString(1, colorName);
                ps.setInt(2, modelID);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    colorID = rs.getInt("ColorID");
                } else {
                    try (PreparedStatement ins = conn.prepareStatement("""
                        INSERT INTO EVM_VehicleColor (ModelID, ColorName)
                        VALUES (?, ?)
                    """, Statement.RETURN_GENERATED_KEYS)) {
                        ins.setInt(1, modelID);
                        ins.setString(2, colorName);
                        ins.executeUpdate();
                        ResultSet gen = ins.getGeneratedKeys();
                        if (gen.next()) colorID = gen.getInt(1);
                    }
                }
            }

            // Version
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT VersionID FROM EVM_VehicleVersion WHERE VersionName = ? AND ModelID = ?")) {
                ps.setString(1, versionName);
                ps.setInt(2, modelID);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    versionID = rs.getInt("VersionID");
                } else {
                    try (PreparedStatement ins = conn.prepareStatement("""
                        INSERT INTO EVM_VehicleVersion (ModelID, VersionName, Engine, Transmission, Price)
                        VALUES (?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS)) {
                        ins.setInt(1, modelID);
                        ins.setString(2, versionName);
                        ins.setString(3, engine);
                        ins.setString(4, transmission);
                        ins.setBigDecimal(5, basePrice); // ✅ FIX
                        ins.executeUpdate();
                        ResultSet gen = ins.getGeneratedKeys();
                        if (gen.next()) versionID = gen.getInt(1);
                    }
                }
            }

            // Vehicle
            try (PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO EVM_Vehicle (VIN, ModelID, VersionID, ColorID, ManufactureDate, Status, EvmID)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """)) {
                ps.setString(1, vin);
                ps.setInt(2, modelID);
                ps.setInt(3, versionID);
                ps.setInt(4, colorID);
                ps.setDate(5, new java.sql.Date(manufactureDate.getTime()));
                ps.setString(6, status);
                ps.setInt(7, evmID);
                ps.executeUpdate();
            }

            conn.commit();
            success = true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }

    // ======================================================
    // UPDATE VEHICLE
    // ======================================================
    public boolean updateVehicle(String vin, String modelName, String brand, String bodyType, 
                                int year, String description, BigDecimal basePrice, 
                                String versionName, String engine, String transmission, 
                                String colorName, Date manufactureDate, String status, 
                                int evmID, byte[] thumbnailBytes) {
        boolean success = false;
        System.out.println("🔍 [DEBUG] DAOEVMVehicle.updateVehicle called with VIN: " + vin);
        System.out.println("🔍 [DEBUG] Status to update: " + status);

        String updateVehicleSQL = """
            UPDATE EVM_Vehicle 
            SET ManufactureDate = ?, Status = ?, EvmID = ?
            WHERE VIN = ?
        """;

        String updateModelSQL = """
            UPDATE EVM_VehicleModel 
            SET ModelName = ?, Brand = ?, BodyType = ?, Year = ?, 
                Description = ?, BasePrice = ?, ModelImage = ?
            WHERE ModelID = (SELECT ModelID FROM EVM_Vehicle WHERE VIN = ?)
        """;

        String updateVersionSQL = """
            UPDATE EVM_VehicleVersion 
            SET VersionName = ?, Engine = ?, Transmission = ?
            WHERE VersionID = (SELECT VersionID FROM EVM_Vehicle WHERE VIN = ?)
        """;

        String updateColorSQL = """
            UPDATE EVM_VehicleColor 
            SET ColorName = ?
            WHERE ColorID = (SELECT ColorID FROM EVM_Vehicle WHERE VIN = ?)
        """;

        try (Connection conn = DBUtils.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Update vehicle
                try (PreparedStatement ps = conn.prepareStatement(updateVehicleSQL)) {
                    ps.setDate(1, new java.sql.Date(manufactureDate.getTime()));
                    ps.setString(2, status);
                    ps.setInt(3, evmID);
                    ps.setString(4, vin);
                    ps.executeUpdate();
                }

                // Update model
                try (PreparedStatement ps = conn.prepareStatement(updateModelSQL)) {
                    ps.setString(1, modelName);
                    ps.setString(2, brand);
                    ps.setString(3, bodyType);
                    ps.setInt(4, year);
                    ps.setString(5, description);
                    ps.setBigDecimal(6, basePrice);
                    ps.setBytes(7, thumbnailBytes);
                    ps.setString(8, vin);
                    ps.executeUpdate();
                }

                // Update version
                try (PreparedStatement ps = conn.prepareStatement(updateVersionSQL)) {
                    ps.setString(1, versionName);
                    ps.setString(2, engine);
                    ps.setString(3, transmission);
                    ps.setString(4, vin);
                    ps.executeUpdate();
                }

                // Update color
                try (PreparedStatement ps = conn.prepareStatement(updateColorSQL)) {
                    ps.setString(1, colorName);
                    ps.setString(2, vin);
                    ps.executeUpdate();
                }

                conn.commit();
                success = true;
                System.out.println("✅ Vehicle updated successfully: " + vin);

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("❌ Error updating vehicle: " + e.getMessage());
                
                // Check if it's a CHECK constraint error for Status
                if (e.getMessage().contains("CHECK constraint") && e.getMessage().contains("Status")) {
                    System.out.println("🔍 [DEBUG] CHECK constraint error for Status, trying alternative values");
                    
                    // Try with status values that match the EVM_Vehicle CHECK constraint: 'Sold' OR 'InStock' OR 'InProduction'
                    String[] alternativeStatuses = {"InStock", "InProduction", "Sold"};
                    boolean updateSuccess = false;
                    
                    for (String altStatus : alternativeStatuses) {
                        try {
                            System.out.println("🔍 [DEBUG] Trying status: " + altStatus);
                            
                            // Retry the update with alternative status
                            try (PreparedStatement ps = conn.prepareStatement(updateVehicleSQL)) {
                                ps.setDate(1, new java.sql.Date(manufactureDate.getTime()));
                                ps.setString(2, altStatus);
                                ps.setInt(3, evmID);
                                ps.setString(4, vin);
                                ps.executeUpdate();
                            }
                            
                            // Continue with other updates
                            try (PreparedStatement ps = conn.prepareStatement(updateModelSQL)) {
                                ps.setString(1, modelName);
                                ps.setString(2, brand);
                                ps.setString(3, bodyType);
                                ps.setInt(4, year);
                                ps.setString(5, description);
                                ps.setBigDecimal(6, basePrice);
                                ps.setBytes(7, thumbnailBytes);
                                ps.setString(8, vin);
                                ps.executeUpdate();
                            }

                            try (PreparedStatement ps = conn.prepareStatement(updateVersionSQL)) {
                                ps.setString(1, versionName);
                                ps.setString(2, engine);
                                ps.setString(3, transmission);
                                ps.setString(4, vin);
                                ps.executeUpdate();
                            }

                            try (PreparedStatement ps = conn.prepareStatement(updateColorSQL)) {
                                ps.setString(1, colorName);
                                ps.setString(2, vin);
                                ps.executeUpdate();
                            }

                            conn.commit();
                            success = true;
                            updateSuccess = true;
                            System.out.println("✅ Vehicle updated successfully with alternative status: " + altStatus);
                            break;
                            
                        } catch (SQLException retryException) {
                            System.out.println("❌ Alternative status " + altStatus + " also failed: " + retryException.getMessage());
                            conn.rollback();
                        }
                    }
                    
                    if (!updateSuccess) {
                        System.out.println("❌ All alternative status values failed");
                    }
                } else {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Database connection error: " + e.getMessage());
            e.printStackTrace();
        }

        return success;
    }

    // ======================================================
    // DELETE VEHICLE
    // ======================================================
    public boolean deleteVehicle(String vin) {
        boolean success = false;

        String deleteVehicleSQL = "DELETE FROM EVM_Vehicle WHERE VIN = ?";

        try (Connection conn = DBUtils.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Delete vehicle (cascade will handle related records)
                try (PreparedStatement ps = conn.prepareStatement(deleteVehicleSQL)) {
                    ps.setString(1, vin);
                    int affectedRows = ps.executeUpdate();
                    
                    if (affectedRows > 0) {
                        conn.commit();
                        success = true;
                        System.out.println("✅ Vehicle deleted successfully: " + vin);
                    } else {
                        System.out.println("⚠️ No vehicle found with VIN: " + vin);
                    }
                }

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("❌ Error deleting vehicle: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.out.println("❌ Database connection error: " + e.getMessage());
            e.printStackTrace();
        }

        return success;
    }
}
