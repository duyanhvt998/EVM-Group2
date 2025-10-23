package com.dealermanagementsysstem.project.controller;

import org.springframework.http.ResponseEntity;

import com.dealermanagementsysstem.project.Model.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.Date;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String role = auth.getAuthorities().iterator().next().getAuthority();

            if (role.equals("ROLE_EVM") || role.equals("ROLE_EVMSTAFF") || role.equals("ROLE_ADMIN")) {
                model.addAttribute("actionRole", "EVM");
            } else if (role.equals("ROLE_DEALER") || role.equals("ROLE_DEALERSTAFF")) {
                model.addAttribute("actionRole", "DEALER");
            }
        }

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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String role = auth.getAuthorities().iterator().next().getAuthority();

            if (role.equals("ROLE_EVM") || role.equals("ROLE_EVMSTAFF") || role.equals("ROLE_ADMIN")) {
                model.addAttribute("actionRole", "EVM");
            } else if (role.equals("ROLE_DEALER") || role.equals("ROLE_DEALERSTAFF")) {
                model.addAttribute("actionRole", "DEALER");
            }
        }

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
    
    // Exception handler for file upload size errors
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, Model model) {
        System.out.println("❌ [ERROR] File upload size exceeded: " + ex.getMessage());
        model.addAttribute("error", "❌ File size too large. Please upload an image smaller than 10MB.");
        return "evmPage/createANewVehicleToList";
    }
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
        System.out.println("🔍 [DEBUG] EVMVehicleController.createVehicle called");
        System.out.println("🔍 [DEBUG] VIN: " + vin);
        System.out.println("🔍 [DEBUG] Model: " + modelName);
        System.out.println("🔍 [DEBUG] Status: " + status);
        System.out.println("🔍 [DEBUG] Request reached controller successfully - CSRF disabled for testing");
        
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

    // ===========================
    // 5️⃣ Form chỉnh sửa xe
    // ===========================
    @GetMapping("/edit/{vin}")
    public String showEditForm(@PathVariable("vin") String vin, Model model) {
        System.out.println("🔍 [DEBUG] EVMVehicleController.showEditForm called with VIN: " + vin);
        
        DTOEVMVehicle vehicle = dao.getVehicleByVIN(vin);
        if (vehicle == null) {
            System.out.println("❌ [ERROR] Vehicle not found for VIN: " + vin);
            model.addAttribute("error", "Vehicle not found for VIN: " + vin);
            model.addAttribute("vehicle", null); // Explicitly set to null
            return "evmPage/editVehicle";
        }
        
        System.out.println("✅ [SUCCESS] Vehicle found: " + vehicle.getModel().getModelName());
        model.addAttribute("vehicle", vehicle);
        return "evmPage/editVehicle";
    }

    // ===========================
    // 6️⃣ Xử lý cập nhật xe
    // ===========================
    @PostMapping("/edit/{vin}")
    public String updateVehicle(
            @PathVariable("vin") String vin,
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
        System.out.println("🔍 [DEBUG] EVMVehicleController.updateVehicle called with VIN: " + vin);
        System.out.println("🔍 [DEBUG] Status value: " + status);
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
            } else {
                // Nếu không có thumbnail mới, giữ nguyên thumbnail cũ
                DTOEVMVehicle existingVehicle = dao.getVehicleByVIN(vin);
                if (existingVehicle != null && existingVehicle.getModel() != null) {
                    thumbnailBytes = existingVehicle.getModel().getModelImage();
                }
            }

            // === Gọi DAO xử lý cập nhật ===
            boolean updated = dao.updateVehicle(
                    vin, modelName, brand, bodyType, year,
                    description, basePrice, versionName,
                    engine, transmission, colorName,
                    new java.sql.Date(manufactureDate.getTime()), status, evmID, thumbnailBytes
            );

            if (!updated) {
                model.addAttribute("error", "❌ Failed to update vehicle. Please try again.");
                return "evmPage/editVehicle";
            }

        } catch (IOException e) {
            model.addAttribute("error", "⚠️ Error uploading thumbnail: " + e.getMessage());
            e.printStackTrace();
            return "evmPage/editVehicle";
        } catch (Exception e) {
            model.addAttribute("error", "⚠️ Error: " + e.getMessage());
            e.printStackTrace();
            return "evmPage/editVehicle";
        }

        return "redirect:/evm/vehicle/detail/" + vin;
    }

    // ===========================
    // 7️⃣ Xử lý xóa xe
    // ===========================
    @PostMapping("/delete/{vin}")
    public String deleteVehicle(@PathVariable("vin") String vin, Model model, 
                               HttpServletRequest request) {
        System.out.println("🔍 [DEBUG] EVMVehicleController.deleteVehicle called with VIN: " + vin);
        
        // Validate VIN parameter
        if (vin == null || vin.trim().isEmpty()) {
            System.out.println("❌ [ERROR] Invalid VIN parameter");
            model.addAttribute("error", "❌ Invalid vehicle identifier.");
            return "redirect:/evm/vehicle/list";
        }
        
        try {
            // Check if vehicle exists before attempting deletion
            DTOEVMVehicle existingVehicle = dao.getVehicleByVIN(vin);
            if (existingVehicle == null) {
                System.out.println("❌ [ERROR] Vehicle not found: " + vin);
                model.addAttribute("error", "❌ Vehicle not found.");
                return "redirect:/evm/vehicle/list";
            }
            
            // Attempt deletion
            boolean deleted = dao.deleteVehicle(vin);
            
            if (deleted) {
                System.out.println("✅ [SUCCESS] Vehicle deleted successfully: " + vin);
                model.addAttribute("message", "✅ Vehicle '" + existingVehicle.getModel().getModelName() + "' deleted successfully!");
            } else {
                System.out.println("❌ [FAILED] Failed to delete vehicle: " + vin);
                model.addAttribute("error", "❌ Failed to delete vehicle. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("❌ [ERROR] Exception while deleting vehicle: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "⚠️ Error deleting vehicle: " + e.getMessage());
        }

        return "redirect:/evm/vehicle/list";
    }
}
