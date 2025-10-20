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
    public String listVehicles(Model model) {
        List<DTOEVMVehicle> vehicles = dao.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        return "evm_vehicle_list"; // tên file JSP hoặc Thymeleaf
=======
        return "evmPage/vehicleList";
>>>>>>> Stashed changes
=======
        return "evmPage/vehicleList";
>>>>>>> Stashed changes
    }

    // ===========================
    // 2️⃣ Chi tiết xe theo VIN
    // ===========================
    @GetMapping("/detail/{vin}")
    public String vehicleDetail(@PathVariable("vin") String vin, Model modelAttr) {
        DTOEVMVehicle vehicle = dao.getVehicleByVIN(vin);
        if (vehicle == null) {
            modelAttr.addAttribute("error", "Vehicle not found for VIN: " + vin);
            return "evmPage/vehicleDetail";
        }
        modelAttr.addAttribute("vehicle", vehicle);
        return "evmPage/vehicleDetail";
    }

    // ===========================
    // 3️⃣ Tìm kiếm theo Model Name
    // ===========================
    @GetMapping("/search")
    public String searchVehicles(@RequestParam("modelName") String modelName, Model model) {
        List<DTOEVMVehicle> vehicles = dao.searchVehiclesByModelName(modelName);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("searchKey", modelName);
        return "evmPage/vehicleList";
    }

    // ===========================
    // 4️⃣ Form tạo xe mới
    // ===========================
    @GetMapping("/create")
    public String showCreateForm() {
        return "evmPage/vehicleCreate"; // file JSP/HTML form nhập liệu
    }

    // ===========================
    // 5️⃣ Xử lý tạo xe mới
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
            // === 1. Upload thumbnail ===
            String thumbnailPath = null;
            if (thumbnail != null && !thumbnail.isEmpty()) {
                String uploadDir = "src/main/resources/static/uploads/vehicle/";
                String fileName = System.currentTimeMillis() + "_" + thumbnail.getOriginalFilename();
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(thumbnail.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                thumbnailPath = "/uploads/vehicle/" + fileName; // dùng trong HTML hiển thị
            }

            // === 2. Gọi DAO xử lý logic thêm mới ===
            boolean created = dao.createVehicle(
                    vin,
                    modelName,
                    brand,
                    bodyType,
                    year,
                    description,
                    basePrice,
                    versionName,
                    engine,
                    transmission,
                    colorName,
                    manufactureDate,
                    status,
                    evmID,
                    thumbnailPath
            );

            // === 3. Trả kết quả về view ===
            if (created) {
                model.addAttribute("success", true);
                model.addAttribute("message", "✅ Vehicle created successfully!");
            } else {
                model.addAttribute("success", false);
                model.addAttribute("message", "❌ Failed to create vehicle. Please try again.");
            }

        } catch (IOException e) {
            model.addAttribute("success", false);
            model.addAttribute("message", "⚠️ Error uploading thumbnail: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("message", "⚠️ Error: " + e.getMessage());
            e.printStackTrace();
        }

        return "evmPage/vehicleCreateResult"; // hiển thị kết quả
    }
}
