package com.dealermanagementsysstem.project.controller;

import com.dealermanagementsysstem.project.Model.DAOAccount;
import com.dealermanagementsysstem.project.Model.DAODiscountPolicy;
import com.dealermanagementsysstem.project.Model.DTODiscountPolicy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/discount-policy")
public class DealerDiscountPolicyController {

    private final DAODiscountPolicy daoPolicy;
    private final DAOAccount daoAccount;

    public DealerDiscountPolicyController() {
        this.daoPolicy = new DAODiscountPolicy();
        this.daoAccount = new DAOAccount();
    }

    // ✅ [GET] Trang hiển thị danh sách + form + search
    @GetMapping
    public String showDiscountPolicyPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model
    ) {
        // 🔹 Lấy email đăng nhập từ Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Integer dealerID = daoAccount.getDealerIdByEmail(email);

        if (dealerID == null) {
            model.addAttribute("error", "Không tìm thấy Dealer của email: " + email);
            return "dealerPage/discountPolicyPage";
        }

        List<DTODiscountPolicy> policies;

        if (keyword != null && !keyword.trim().isEmpty()) {
            policies = daoPolicy.searchPolicyByName(keyword, dealerID);
            model.addAttribute("keyword", keyword);
        } else {
            policies = daoPolicy.getPoliciesByDealer(dealerID);
        }

        model.addAttribute("policies", policies);
        model.addAttribute("newPolicy", new DTODiscountPolicy());
        return "dealerPage/discountPolicyPage"; // ✅ trỏ về đúng HTML trang
    }

    // ✅ [POST] Tạo Discount Policy mới
    @PostMapping("/insert")
    public String insertDiscountPolicy(
            @RequestParam("policyName") String policyName,
            @RequestParam("description") String description,
            @RequestParam("hangPercent") double hangPercent,
            @RequestParam("dailyPercent") double dailyPercent,
            @RequestParam("status") String status,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model
    ) {
        // 🔹 Lấy DealerID từ email đăng nhập
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Integer dealerID = daoAccount.getDealerIdByEmail(email);

        if (dealerID == null) {
            model.addAttribute("error", "Không tìm thấy Dealer của email: " + email);
            return "dealerPage/discountPolicyPage";
        }

        // 🔹 Tạo object DTO
        DTODiscountPolicy dto = new DTODiscountPolicy();
        dto.setDealerID(dealerID);
        dto.setPolicyName(policyName);
        dto.setDescription(description);
        dto.setHangPercent(hangPercent);
        dto.setDailyPercent(dailyPercent);
        dto.setStatus(status);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);

        // 🔹 Gọi DAO để thêm mới
        boolean success = daoPolicy.createDiscountPolicy(dto);

        if (success) {
            model.addAttribute("message", "Tạo Discount Policy thành công!");
        } else {
            model.addAttribute("error", "Không thể tạo Discount Policy. Kiểm tra lại dữ liệu!");
        }

        // 🔹 Reload lại danh sách policy sau khi thêm
        List<DTODiscountPolicy> policies = daoPolicy.getPoliciesByDealer(dealerID);
        model.addAttribute("policies", policies);
        model.addAttribute("newPolicy", new DTODiscountPolicy());
        return "dealerPage/discountPolicyPage";
    }
}
