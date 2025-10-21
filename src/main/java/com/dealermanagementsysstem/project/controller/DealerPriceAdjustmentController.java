package com.dealermanagementsysstem.project.controller;

import com.dealermanagementsysstem.project.Model.DAODealerPriceAdjustment;
import com.dealermanagementsysstem.project.Model.DAOAccount;
import com.dealermanagementsysstem.project.Model.DTODealerPriceAdjustment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/discount")
public class DealerPriceAdjustmentController {

    private final DAODealerPriceAdjustment daoDiscount;
    private final DAOAccount daoAccount;

    public DealerPriceAdjustmentController() {
        this.daoDiscount = new DAODealerPriceAdjustment();
        this.daoAccount = new DAOAccount();
    }

    // ✅ Trang quản lý Discount (list + form + search)
    @GetMapping
    public String showDiscountManagementPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // ✅ Lấy email đang đăng nhập
        Integer dealerID = daoAccount.getDealerIdByEmail(email);

        if (dealerID == null) {
            model.addAttribute("error", "Không tìm thấy Dealer của email: " + email);
            return "dealerPage/discountManagement";
        }

        List<DTODealerPriceAdjustment> discounts;
        if (keyword != null && !keyword.trim().isEmpty()) {
            discounts = daoDiscount.searchByPromotionNameAndDealer(keyword, dealerID);
            model.addAttribute("keyword", keyword);
        } else {
            discounts = daoDiscount.getDiscountsByDealer(dealerID);
        }

        model.addAttribute("discounts", discounts);
        model.addAttribute("discount", new DTODealerPriceAdjustment());
        return "dealerPage/discountManagement";
    }

    // ✅ Tạo discount mới (POST)
    @PostMapping("/insert")
    public String insertDiscount(
            @RequestParam("promotionName") String promotionName,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("discountPercent") Double discountPercent,
            @RequestParam("modelID") int modelID,
            @RequestParam(value = "notes", required = false) String notes,
            Model model
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // ✅ Lấy email đang đăng nhập
        Integer dealerID = daoAccount.getDealerIdByEmail(email);

        if (dealerID == null) {
            model.addAttribute("error", "Không tìm thấy Dealer của email: " + email);
            return "dealerPage/discountManagement";
        }

        DTODealerPriceAdjustment d = new DTODealerPriceAdjustment();
        d.setPromotionName(promotionName);
        d.setStartDate(startDate);
        d.setEndDate(endDate);
        d.setDiscountPercent(discountPercent);
        d.setModelID(modelID);
        d.setNotes(notes);
        d.setDealerID(dealerID);
        d.setDiscountAmount(0.0);

        boolean success = daoDiscount.createDiscount(d);

        if (success) {
            model.addAttribute("message", "Tạo discount thành công!");
        } else {
            model.addAttribute("error", "Không thể tạo discount. Vui lòng kiểm tra dữ liệu!");
        }

        // ✅ Load lại danh sách discount của dealer đó
        List<DTODealerPriceAdjustment> discounts = daoDiscount.getDiscountsByDealer(dealerID);
        model.addAttribute("discounts", discounts);
        model.addAttribute("discount", new DTODealerPriceAdjustment());
        return "dealerPage/discountManagement";
    }
}
