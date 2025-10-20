package com.dealermanagementsysstem.project.controller;

import com.dealermanagementsysstem.project.Model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/evm/vehicle")
public class EVMVehicleController {

    private final DAOEVMVehicle dao = new DAOEVMVehicle();

    // Hiển thị danh sách xe
    @GetMapping("/list")
    public String listVehicles(Model model) {
        List<DTOEVMVehicle> vehicles = dao.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
        return "evm_vehicle_list"; // tên file JSP hoặc Thymeleaf
    }

    // Hiển thị chi tiết xe
    @GetMapping("/detail/{vin}")
    public String vehicleDetail(@PathVariable("vin") String vin, Model modelAttr) {
        DTOEVMVehicle vehicle = dao.getVehicleByVIN(vin);
        if (vehicle == null) {
            modelAttr.addAttribute("error", "Vehicle not found for VIN: " + vin);
            return "evm_vehicle_detail";
        }
        modelAttr.addAttribute("vehicle", vehicle);
        return "evm_vehicle_detail";
    }

    @GetMapping("/search")
    public String searchVehicles(@RequestParam("modelName") String modelName, Model model) {
        List<DTOEVMVehicle> vehicles = dao.searchVehiclesByModelName(modelName);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("searchKey", modelName);
        return "evm_vehicle_list"; // tái sử dụng view danh sách
    }

}
