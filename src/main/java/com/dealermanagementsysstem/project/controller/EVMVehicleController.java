package com.dealermanagementsysstem.project.controller;

import com.dealermanagementsysstem.project.Model.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/evm/vehicle")
public class EVMVehicleController {

    private final DAOEVMVehicle dao = new DAOEVMVehicle();

    // ===========================
    // 1️⃣ Danh sách xe
    // ===========================
    @GetMapping("/list")
    public String listVehicles(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<DTOEVMVehicle> vehicles;

        // Nếu có từ khóa tìm kiếm → gọi hàm search
        if (keyword != null && !keyword.trim().isEmpty()) {
            vehicles = dao.searchVehiclesByModelName(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            vehicles = dao.getAllVehicles();
        }

        model.addAttribute("vehicles", vehicles);
        return "evmPage/vehicleList"; // ✅ đúng với HTML vehicle list
    }

    // ===========================
    // 2️⃣ Chi tiết xe theo VIN
    // ===========================
    @GetMapping("/detail/{vin}")
    public String vehicleDetail(@PathVariable("vin") String vin, Model model) {
        DTOEVMVehicle vehicle = dao.getVehicleByVIN(vin);
        if (vehicle == null) {
            model.addAttribute("error", "Vehicle not found for VIN: " + vin);
            return "evmPage/vehicleList"; // quay lại list nếu không tìm thấy
        }
        model.addAttribute("vehicle", vehicle);
        return "evmPage/vehicleListDetail";
    }

    // ===========================
    // 3️⃣ Form tạo xe mới
    // ===========================
    @GetMapping("/create")
    public String showCreateForm() {
        return "evmPage/createANewVehicleToList"; // ✅ form HTML của bạn
    }

    // ===========================
    // 4️⃣ Xử lý tạo xe mới
    // ===========================
    @PostMapping("/create")
    public String createVehicle(
            @RequestParam("vin") String vin,
            @RequestParam("modelName") String modelName,
            @RequestParam("brand") String brand,
            @RequestParam("bodyType") String bodyType,
            @RequestParam("year") int year,
            @RequestParam("description") String description,
            @RequestParam("basePrice") double basePrice,
            @RequestParam("versionName") String versionName,
            @RequestParam("engine") String engine,
            @RequestParam("transmission") String transmission,
            @RequestParam("colorName") String colorName,
            @RequestParam("manufactureDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date manufactureDate,
            @RequestParam("status") String status,
            @RequestParam("evmID") int evmID,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            Model model
    ) {
        try {
            // === Upload thumbnail (nếu có) ===
            String thumbnailPath = null;
            if (thumbnail != null && !thumbnail.isEmpty()) {
                String uploadDir = "src/main/resources/static/uploads/vehicle/";
                String fileName = System.currentTimeMillis() + "_" + thumbnail.getOriginalFilename();

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Files.copy(thumbnail.getInputStream(), uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);
                thumbnailPath = "/uploads/vehicle/" + fileName;
            }

            // === Gọi DAO xử lý thêm mới ===
            boolean created = dao.createVehicle(
                    vin, modelName, brand, bodyType, year,
                    description, basePrice, versionName,
                    engine, transmission, colorName,
                    manufactureDate, status, evmID, thumbnailPath
            );

            // === Kiểm tra kết quả ===
            if (!created) {
                model.addAttribute("success", false);
                model.addAttribute("message", "❌ Failed to create vehicle. Please try again.");
                return "evmPage/createANewVehicleToList"; // quay lại form nếu lỗi
            }

        } catch (IOException e) {
            model.addAttribute("success", false);
            model.addAttribute("message", "⚠️ Error uploading thumbnail: " + e.getMessage());
            e.printStackTrace();
            return "evmPage/createANewVehicleToList";
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("message", "⚠️ Error: " + e.getMessage());
            e.printStackTrace();
            return "evmPage/createANewVehicleToList";
        }

        // ✅ Thành công → quay lại danh sách xe
        return "redirect:/evm/vehicle/list";
    }

}
