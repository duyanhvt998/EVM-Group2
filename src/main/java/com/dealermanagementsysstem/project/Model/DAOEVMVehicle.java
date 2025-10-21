package com.dealermanagementsysstem.project.Model;

import utils.DBUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                        rs.getDouble("BasePrice"),
                        rs.getString("ModelImage"),
                        rs.getInt("MergedToID")
                );

                DTOEVMVehicleVersion version = new DTOEVMVehicleVersion(
                        rs.getInt("VersionID"),
                        rs.getInt("ModelID"),
                        rs.getString("VersionName"),
                        rs.getString("Engine"),
                        rs.getString("Transmission"),
                        rs.getDouble("Price")
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
                            rs.getDouble("BasePrice"),
                            rs.getString("ModelImage"),
                            rs.getInt("MergedToID")
                    );

                    DTOEVMVehicleVersion version = new DTOEVMVehicleVersion(
                            rs.getInt("VersionID"),
                            rs.getInt("ModelID"),
                            rs.getString("VersionName"),
                            rs.getString("Engine"),
                            rs.getString("Transmission"),
                            rs.getDouble("Price")
                    );

                    DTOEVMVehicleColor color = new DTOEVMVehicleColor(
                            rs.getInt("ColorID"),
                            rs.getInt("ModelID"),
                            rs.getString("ColorName")
                    );

                    vehicle.setModel(model);
                    vehicle.setVersion(version);
                    vehicle.setColor(color);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
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
                            rs.getDouble("BasePrice"),
                            rs.getString("ModelImage"),
                            rs.getInt("MergedToID")
                    );

                    DTOEVMVehicleVersion version = new DTOEVMVehicleVersion(
                            rs.getInt("VersionID"),
                            rs.getInt("ModelID"),
                            rs.getString("VersionName"),
                            rs.getString("Engine"),
                            rs.getString("Transmission"),
                            rs.getDouble("Price")
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
// CREATE VEHICLE (FIXED)
// ======================================================
    public boolean createVehicle(
            String vin,
            String modelName,
            String brand,
            String bodyType,
            int year,
            String description,
            double basePrice,
            String versionName,
            String engine,
            String transmission,
            String colorName,
            java.util.Date manufactureDate,
            String status,
            int evmID,
            byte[] thumbnailBytes // ✅ đổi từ String sang byte[]
    ) {
        boolean success = false;
        try (Connection conn = DBUtils.getConnection()) {
            conn.setAutoCommit(false);

            int modelID = -1;
            int colorID = -1;
            int versionID = -1;

            // 1️⃣ Model
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
                        ins.setDouble(7, basePrice);
                        ins.setBytes(8, thumbnailBytes); // ✅ ghi trực tiếp byte[]

                        ins.executeUpdate();
                        ResultSet gen = ins.getGeneratedKeys();
                        if (gen.next()) modelID = gen.getInt(1);
                    }
                }
            }

            // 2️⃣ Color
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

            // 3️⃣ Version
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
                        ins.setDouble(5, basePrice);
                        ins.executeUpdate();
                        ResultSet gen = ins.getGeneratedKeys();
                        if (gen.next()) versionID = gen.getInt(1);
                    }
                }
            }

            // 4️⃣ Vehicle
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


}
