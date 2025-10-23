package com.dealermanagementsysstem.project.controller;

import com.dealermanagementsysstem.project.Model.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/saleorder")
public class OrderController {

    private final DAOSaleOrder dao;

    public OrderController() {
        this.dao = new DAOSaleOrder();
    }

    // ✅ Hiển thị danh sách tất cả đơn hàng
    @GetMapping
    public String listSaleOrders(Model model) {
        List<DTOSaleOrder> orders = dao.getAllSaleOrders();
        model.addAttribute("orders", orders);
        return "dealerPage/customerOrderList"; // 👉 trang Thymeleaf hiển thị danh sách
    }

    // 🔥 CORE FLOW: Show create order form with approved quotations
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session) {
        System.out.println("📋 [DEBUG] Loading create order form with approved quotations");

        try {
            // Get dealer ID from session
            DTOAccount account = (DTOAccount) session.getAttribute("user");
            if (account == null || account.getDealerId() == null) {
                model.addAttribute("error", "Bạn cần đăng nhập bằng tài khoản dealer!");
                return "redirect:/login";
            }

            // Get approved quotations for this dealer
            DAOQuotation quotationDAO = new DAOQuotation();
            List<DTOQuotation> approvedQuotations = quotationDAO.getQuotationsByDealer(account.getDealerId())
                    .stream()
                    .filter(q -> "Approved".equalsIgnoreCase(q.getStatus()))
                    .toList();

            if (approvedQuotations.isEmpty()) {
                model.addAttribute("error", "No approved quotations found. Please create and approve a quotation first.");
                model.addAttribute("redirectUrl", "/quotation/list");
                return "dealerPage/noQuotations";
            }

            model.addAttribute("order", new DTOSaleOrder());
            model.addAttribute("detail", new DTOSaleOrderDetail());
            model.addAttribute("quotations", approvedQuotations);
            model.addAttribute("message", "Found " + approvedQuotations.size() + " approved quotations");

            System.out.println("✅ [SUCCESS] Loaded " + approvedQuotations.size() + " approved quotations");
            return "dealerPage/createSaleOrder";
        } catch (Exception e) {
            System.out.println("❌ [ERROR] Failed to load create order form: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to load create order form: " + e.getMessage());
            return "dealerPage/errorPage";
        }
    }

    // 🔥 CORE FLOW: Create SaleOrder with mandatory quotation validation
    @PostMapping("/insert")
    public String insertSaleOrder(
            @RequestParam("customerID") int customerID,
            @RequestParam("staffID") int staffID,
            @RequestParam("vin") String vin,
            @RequestParam("quotationID") int quotationID,
            @RequestParam(value = "status", required = false, defaultValue = "Pending") String status,
            HttpSession session,
            Model model
    ) {
        System.out.println("🧩 [DEBUG] Creating SaleOrder for CustomerID: " + customerID + ", VIN: " + vin + ", QuotationID: " + quotationID);

        // ✅ Lấy dealerID từ tài khoản đang đăng nhập
        DTOAccount account = (DTOAccount) session.getAttribute("user");
        if (account == null) {
            System.out.println("⚠️ [ERROR] Không tìm thấy tài khoản trong session. Người dùng chưa đăng nhập!");
            model.addAttribute("error", "Bạn cần đăng nhập để tạo đơn hàng!");
            return "redirect:/login";
        }

        Integer dealerID = account.getDealerId();
        if (dealerID == null) {
            System.out.println("⚠️ [ERROR] Tài khoản hiện tại không có DealerID (không phải dealer).");
            model.addAttribute("error", "Tài khoản không hợp lệ để tạo đơn hàng!");
            return "redirect:/saleorder";
        }

        // 🔥 MANDATORY QUOTATION VALIDATION
        DAOQuotation quotationDAO = new DAOQuotation();
        
        // 1. Check if quotation exists
        DTOQuotation quotation = quotationDAO.getQuotationById(quotationID);
        if (quotation == null) {
            System.out.println("❌ [ERROR] Quotation not found: " + quotationID);
            model.addAttribute("error", "Quotation not found! Please select a valid quotation.");
            return "redirect:/quotation/list";
        }

        // 2. Check if quotation is approved
        if (!quotationDAO.isQuotationApproved(quotationID)) {
            System.out.println("❌ [ERROR] Quotation not approved: " + quotationID + ", Status: " + quotation.getStatus());
            model.addAttribute("error", "Quotation must be approved before creating sale order! Current status: " + quotation.getStatus());
            return "redirect:/quotation/list";
        }

        // 3. Validate quotation belongs to same dealer
        if (quotation.getDealer().getDealerID() != dealerID) {
            System.out.println("❌ [ERROR] Quotation belongs to different dealer: " + quotation.getDealer().getDealerID() + " vs " + dealerID);
            model.addAttribute("error", "Quotation belongs to different dealer!");
            return "redirect:/quotation/list";
        }

        // 4. Validate customer matches
        if (quotation.getCustomer().getCustomerID() != customerID) {
            System.out.println("❌ [ERROR] Customer mismatch: " + quotation.getCustomer().getCustomerID() + " vs " + customerID);
            model.addAttribute("error", "Customer does not match quotation!");
            return "redirect:/quotation/list";
        }

        // 5. Get price from quotation details
        List<DTOQuotationDetail> quotationDetails = quotationDAO.getQuotationDetails(quotationID);
        if (quotationDetails.isEmpty()) {
            System.out.println("❌ [ERROR] No quotation details found for quotation: " + quotationID);
            model.addAttribute("error", "Quotation details not found!");
            return "redirect:/quotation/list";
        }

        DTOQuotationDetail quotationDetail = quotationDetails.get(0);
        java.math.BigDecimal price = quotationDetail.getUnitPrice();

        System.out.println("✅ [VALIDATION] Quotation validation passed - ID: " + quotationID + ", Price: " + price);

        // --- Tạo các đối tượng DTO ---
        DTOSaleOrder order = new DTOSaleOrder();
        order.setCustomer(new DTOCustomer());
        order.getCustomer().setCustomerID(customerID);

        order.setDealer(new DTODealer());
        order.getDealer().setDealerID(dealerID);

        order.setStaff(new DTODealerStaff());
        order.getStaff().setStaffID(staffID);

        order.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        order.setStatus(status);

        // --- Chi tiết đơn hàng với giá từ quotation ---
        DTOVehicle vehicle = new DTOVehicle();
        vehicle.setVIN(vin);

        DTOSaleOrderDetail detail = new DTOSaleOrderDetail();
        detail.setVehicle(vehicle);
        detail.setPrice(price); // ✅ Sử dụng giá từ quotation
        detail.setQuotationID(quotationID); // ✅ Link to quotation

        List<DTOSaleOrderDetail> detailList = new ArrayList<>();
        detailList.add(detail);

        order.setDetail(detailList);

        // --- Gọi DAO để lưu vào DB ---
        boolean success = dao.insertSaleOrder(order);

        if (success) {
            System.out.println("✅ [SUCCESS] SaleOrder created successfully for DealerID: " + dealerID + ", VIN: " + vin + ", QuotationID: " + quotationID);
            model.addAttribute("message", "Sale order created successfully from approved quotation!");
            return "redirect:/saleorder";
        } else {
            System.out.println("❌ [FAILED] Failed to create SaleOrder for DealerID: " + dealerID + ", VIN: " + vin);
            model.addAttribute("error", "Failed to create sale order. Please check input data!");
            return "dealerPage/createSaleOrder";
        }
    }

    // ✅ Chi tiết 1 đơn hàng
    @GetMapping("/detail/{id}")
    public String viewOrderDetail(@PathVariable("id") int id, Model model) {
        List<DTOSaleOrder> list = dao.getAllSaleOrders();
        DTOSaleOrder order = list.stream().filter(o -> o.getSaleOrderID() == id).findFirst().orElse(null);

        if (order == null) {
            model.addAttribute("error", "Order not found!");
            return "redirect:/saleorder";
        }

        model.addAttribute("order", order);
        return "dealerPage/saleOrderDetail"; // 👉 trang chi tiết đơn hàng
    }
    @Autowired
    private DAODealer daoDealer;

    @GetMapping("/order/create")
    public String createOrderForm(Model model) throws SQLException {
        List<DTODealer> dealerList = daoDealer.getAllDealers();
        model.addAttribute("dealerList", dealerList);
        model.addAttribute("order", new DTOOrder());
        return "evmPage/order-create";
    }


}
