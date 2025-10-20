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

}
