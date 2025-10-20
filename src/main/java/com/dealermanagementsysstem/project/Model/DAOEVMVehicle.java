package com.dealermanagementsysstem.project.Model;

import utils.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOEVMVehicle {

    public List<DTOEVMVehicle> getAllVehicles() {
        List<DTOEVMVehicle> list = new ArrayList<>();

        String sql = """
                    SELECT v.VIN,
                           v.ModelID,
                           v.VersionID,
                           v.ColorID,
                           v.ManufactureDate,
                           v.Status,
                           v.EvmID,
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

                // Dữ liệu chính
                vehicle.setVIN(rs.getString("VIN"));
                vehicle.setModelID(rs.getInt("ModelID"));
                vehicle.setVersionID(rs.getInt("VersionID"));
                vehicle.setColorID(rs.getInt("ColorID"));
                vehicle.setManufactureDate(rs.getDate("ManufactureDate"));
                vehicle.setStatus(rs.getString("Status"));
                vehicle.setEvmID(rs.getInt("EvmID"));

                // Dữ liệu Model
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

                // Dữ liệu Version
                DTOEVMVehicleVersion version = new DTOEVMVehicleVersion(
                        rs.getInt("VersionID"),
                        rs.getInt("ModelID"),
                        rs.getString("VersionName"),
                        rs.getString("Engine"),
                        rs.getString("Transmission"),
                        rs.getDouble("Price")
                );

                // Dữ liệu Color
                DTOEVMVehicleColor color = new DTOEVMVehicleColor(
                        rs.getInt("ColorID"),
                        rs.getInt("ModelID"),
                        rs.getString("ColorName")
                );

                // Gán vào Vehicle
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

    public DTOEVMVehicle getVehicleByVIN(String vin) {
        DTOEVMVehicle vehicle = null;

        String sql = """
                    SELECT v.VIN,
                           v.ModelID,
                           v.VersionID,
                           v.ColorID,
                           v.ManufactureDate,
                           v.Status,
                           v.EvmID,
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

    public List<DTOEVMVehicle> searchVehiclesByModelName(String modelName) {
        List<DTOEVMVehicle> list = new ArrayList<>();

        String sql = """
                    SELECT v.VIN,
                           v.ModelID,
                           v.VersionID,
                           v.ColorID,
                           v.ManufactureDate,
                           v.Status,
                           v.EvmID,
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

            ps.setString(1, "%" + modelName + "%"); // tìm gần đúng
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
            String thumbnailPath
    ) {
        boolean success = false;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // bắt đầu transaction

            int modelID = -1;
            int colorID = -1;
            int versionID = -1;

            // 1️⃣ Kiểm tra Model có tồn tại chưa
            ps = conn.prepareStatement("SELECT ModelID FROM EVM_VehicleModel WHERE ModelName = ?");
            ps.setString(1, modelName);
            rs = ps.executeQuery();
            if (rs.next()) {
                modelID = rs.getInt("ModelID");
            } else {
                // tạo model mới
                ps.close();
                rs.close();
                ps = conn.prepareStatement("SELECT ISNULL(MAX(ModelID), 0) + 1 FROM EVM_VehicleModel");
                rs = ps.executeQuery();
                if (rs.next()) modelID = rs.getInt(1);
                ps.close();

                ps = conn.prepareStatement("""
                            INSERT INTO EVM_VehicleModel (ModelID, ModelName, Brand, BodyType, Year, Description, EvmID, BasePrice, ModelImage)
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """);
                ps.setInt(1, modelID);
                ps.setString(2, modelName);
                ps.setString(3, brand);
                ps.setString(4, bodyType);
                ps.setInt(5, year);
                ps.setString(6, description);
                ps.setInt(7, evmID);
                ps.setDouble(8, basePrice);
                ps.setString(9, thumbnailPath);
                ps.executeUpdate();
            }

            // 2️⃣ Kiểm tra Color có tồn tại chưa
            ps.close();
            ps = conn.prepareStatement("SELECT ColorID FROM EVM_VehicleColor WHERE ColorName = ? AND ModelID = ?");
            ps.setString(1, colorName);
            ps.setInt(2, modelID);
            rs = ps.executeQuery();
            if (rs.next()) {
                colorID = rs.getInt("ColorID");
            } else {
                ps.close();
                rs.close();
                ps = conn.prepareStatement("SELECT ISNULL(MAX(ColorID), 0) + 1 FROM EVM_VehicleColor");
                rs = ps.executeQuery();
                if (rs.next()) colorID = rs.getInt(1);
                ps.close();

                ps = conn.prepareStatement("""
                            INSERT INTO EVM_VehicleColor (ColorID, ModelID, ColorName)
                            VALUES (?, ?, ?)
                        """);
                ps.setInt(1, colorID);
                ps.setInt(2, modelID);
                ps.setString(3, colorName);
                ps.executeUpdate();
            }

            // 3️⃣ Kiểm tra Version có tồn tại chưa (theo ModelID + VersionName)
            ps.close();
            ps = conn.prepareStatement("SELECT VersionID FROM EVM_VehicleVersion WHERE VersionName = ? AND ModelID = ?");
            ps.setString(1, versionName);
            ps.setInt(2, modelID);
            rs = ps.executeQuery();
            if (rs.next()) {
                versionID = rs.getInt("VersionID");
            } else {
                ps.close();
                rs.close();
                ps = conn.prepareStatement("SELECT ISNULL(MAX(VersionID), 0) + 1 FROM EVM_VehicleVersion");
                rs = ps.executeQuery();
                if (rs.next()) versionID = rs.getInt(1);
                ps.close();

                ps = conn.prepareStatement("""
                            INSERT INTO EVM_VehicleVersion (VersionID, ModelID, VersionName, Engine, Transmission, Price)
                            VALUES (?, ?, ?, ?, ?, ?)
                        """);
                ps.setInt(1, versionID);
                ps.setInt(2, modelID);
                ps.setString(3, versionName);
                ps.setString(4, engine);
                ps.setString(5, transmission);
                ps.setDouble(6, basePrice); // giá gốc
                ps.executeUpdate();
            }

            // 4️⃣ Thêm Vehicle mới
            ps.close();
            ps = conn.prepareStatement("""
                        INSERT INTO EVM_Vehicle (VIN, ModelID, VersionID, ColorID, ManufactureDate, Status, EvmID)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                    """);
            ps.setString(1, vin);
            ps.setInt(2, modelID);
            ps.setInt(3, versionID);
            ps.setInt(4, colorID);
            ps.setDate(5, new java.sql.Date(manufactureDate.getTime()));
            ps.setString(6, status);
            ps.setInt(7, evmID);
            ps.executeUpdate();

            conn.commit();
            success = true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

}
