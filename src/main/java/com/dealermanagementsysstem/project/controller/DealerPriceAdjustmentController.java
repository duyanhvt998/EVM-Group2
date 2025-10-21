package com.dealermanagementsysstem.project.controller;

import com.dealermanagementsysstem.project.Model.DAODealerPriceAdjustment;
import com.dealermanagementsysstem.project.Model.DTODealerPriceAdjustment;
import com.dealermanagementsysstem.project.Model.DTOAccount;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/discount")
public class DealerPriceAdjustmentController {

    private final DAODealerPriceAdjustment dao;

    public DealerPriceAdjustmentController() {
        this.dao = new DAODealerPriceAdjustment();
    }

    // ✅ Trang quản lý Discount (list + form + search)
    @GetMapping
    public String showDiscountManagementPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model,
            HttpSession session
    ) {
        // ✅ Lấy account đang đăng nhập từ session
        DTOAccount acc = (DTOAccount) session.getAttribute("loggedInAccount");

        if (acc == null || acc.getDealerId() == null) {
            model.addAttribute("error", "Không tìm thấy Dealer đang đăng nhập!");
            return "dealerPage/DealerHomePage";
        }

        int dealerID = acc.getDealerId();
        List<DTODealerPriceAdjustment> discounts;

        // ✅ Nếu có keyword → tìm theo tên khuyến mãi
        if (keyword != null && !keyword.trim().isEmpty()) {
            discounts = dao.searchByPromotionNameAndDealer(keyword, dealerID);
            model.addAttribute("keyword", keyword);
        } else {
            discounts = dao.getDiscountsByDealer(dealerID);
        }

        model.addAttribute("discounts", discounts);
        model.addAttribute("discount", new DTODealerPriceAdjustment());
        return "dealerPage/createADealerDiscount";
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
            Model model,
            HttpSession session
    ) {
        // ✅ Lấy account đang login
        DTOAccount acc = (DTOAccount) session.getAttribute("loggedInAccount");

        if (acc == null || acc.getDealerId() == null) {
            model.addAttribute("error", "Không tìm thấy Dealer đang đăng nhập!");
            return "dealerPage/discountManagement";
        }

        DTODealerPriceAdjustment d = new DTODealerPriceAdjustment();
        d.setPromotionName(promotionName);
        d.setStartDate(startDate);
        d.setEndDate(endDate);
        d.setDiscountPercent(discountPercent);
        d.setModelID(modelID);
        d.setNotes(notes);
        d.setDealerID(acc.getDealerId()); // ✅ Gắn dealerID từ session
        d.setDiscountAmount(0.0);

        boolean success = dao.createDiscount(d);

        if (success) {
            System.out.println("✅ [SUCCESS] Discount created: " + promotionName + " by dealerID=" + acc.getDealerId());
            model.addAttribute("message", "Tạo discount thành công!");
        } else {
            System.out.println("❌ [FAILED] Failed to create discount: " + promotionName);
            model.addAttribute("error", "Không thể tạo discount. Vui lòng kiểm tra dữ liệu!");
        }

        // ✅ Load lại danh sách theo dealer đang login
        List<DTODealerPriceAdjustment> discounts = dao.getDiscountsByDealer(acc.getDealerId());
        model.addAttribute("discounts", discounts);
        model.addAttribute("discount", new DTODealerPriceAdjustment());
        return "dealerPage/createADealerDiscount";
    }
}
