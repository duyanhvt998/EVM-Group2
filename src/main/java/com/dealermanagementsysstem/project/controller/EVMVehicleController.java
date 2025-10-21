package com.dealermanagementsysstem.project.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.dealermanagementsysstem.project.Model.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
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

        if (keyword != null && !keyword.trim().isEmpty()) {
            vehicles = dao.searchVehiclesByModelName(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            vehicles = dao.getAllVehicles();
        }

        model.addAttribute("vehicles", vehicles);
        return "evmPage/vehicleList";
    }


    // ===========================
    // 2️⃣ Chi tiết xe theo VIN
    // ===========================
    @GetMapping("/detail/{vin}")
    public String vehicleDetail(@PathVariable("vin") String vin, Model model) {
        DTOEVMVehicle vehicle = dao.getVehicleByVIN(vin);
        if (vehicle == null) {
            model.addAttribute("error", "Vehicle not found for VIN: " + vin);
            return "evmPage/vehicleList";
        }
        model.addAttribute("vehicle", vehicle);
        return "evmPage/vehicleListDetail";
    }

    // ===========================
    // 3️⃣ Form tạo xe mới
    // ===========================
    @GetMapping("/create")
    public String showCreateForm() {
        return "evmPage/createANewVehicleToList";
    }

    // ===========================
// ✅ Trả ảnh model theo VIN
// ===========================
    @GetMapping("/showImage/{vin}")
    @ResponseBody
    public ResponseEntity<byte[]> showVehicleImage(@PathVariable("vin") String vin) {
        DTOEVMVehicle vehicle = dao.getVehicleByVIN(vin);
        if (vehicle == null || vehicle.getModel() == null || vehicle.getModel().getModelImage() == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] imageBytes = vehicle.getModel().getModelImage();
        return ResponseEntity
                .ok()
                .header("Content-Type", "image/jpeg")
                .body(imageBytes);
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
            @RequestParam("basePrice") BigDecimal basePrice,
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
            byte[] thumbnailBytes = null;
            if (thumbnail != null && !thumbnail.isEmpty()) {
                String uploadDir = "src/main/resources/static/uploads/vehicle/";
                String fileName = System.currentTimeMillis() + "_" + thumbnail.getOriginalFilename();

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(fileName);
                Files.copy(thumbnail.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // ✅ Đọc file thành byte[]
                thumbnailBytes = Files.readAllBytes(filePath);
            }

            // === Gọi DAO xử lý thêm mới ===
            boolean created = dao.createVehicle(
                    vin, modelName, brand, bodyType, year,
                    description, basePrice, versionName,
                    engine, transmission, colorName,
                    manufactureDate, status, evmID, thumbnailBytes
            );

            if (!created) {
                model.addAttribute("success", false);
                model.addAttribute("message", "❌ Failed to create vehicle. Please try again.");
                return "evmPage/createANewVehicleToList";
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

        return "redirect:/evm/vehicle/list";
    }
}
