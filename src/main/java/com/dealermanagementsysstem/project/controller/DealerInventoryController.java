package com.dealermanagementsysstem.project.controller;

import com.dealermanagementsysstem.project.Model.DAODealerInventory;
import com.dealermanagementsysstem.project.Model.DAOAccount;
import com.dealermanagementsysstem.project.Model.DTODealerInventory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dealer-inventory")
public class DealerInventoryController {

    private final DAODealerInventory daoInventory;
    private final DAOAccount daoAccount;

    public DealerInventoryController() {
        this.daoInventory = new DAODealerInventory();
        this.daoAccount = new DAOAccount();
    }

    // ✅ Hiển thị danh sách xe theo DealerID của tài khoản đang đăng nhập
    @GetMapping
    public String showDealerInventory(Model model) {
        try {
            // 🔹 Lấy email từ tài khoản đăng nhập
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            // 🔹 Lấy DealerID theo email
            Integer dealerID = daoAccount.getDealerIdByEmail(email);

            if (dealerID == null) {
                model.addAttribute("error", "Không tìm thấy Dealer ID cho tài khoản hiện tại!");
                return "dealerPage/dealerInventory"; // ⚙️ Trang HTML hiển thị lỗi
            }

            // 🔹 Lấy danh sách xe theo DealerID
            List<DTODealerInventory> vehicles = daoInventory.getVehiclesByDealerID(dealerID);

            model.addAttribute("vehicles", vehicles);
            model.addAttribute("dealerID", dealerID);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Đã xảy ra lỗi khi tải danh sách xe!");
        }

        return "dealerPage/dealerInventory"; // ⚙️ View hiển thị danh sách
    }
}
