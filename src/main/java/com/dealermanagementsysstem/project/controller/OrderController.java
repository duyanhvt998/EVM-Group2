package com.dealermanagementsysstem.project.controller;

import com.dealermanagementsysstem.project.Model.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/saleorder")
public class OrderController {

    private final DAOSaleOrder daoSaleOrder;
    private final DAOQuotation daoQuotation;
    private final DAODealer daoDealer;

    @Autowired
    public OrderController(DAODealer daoDealer) {
        this.daoSaleOrder = new DAOSaleOrder();
        this.daoQuotation = new DAOQuotation();
        this.daoDealer = daoDealer;
    }

    // ======================================================
    // 1️⃣ DANH SÁCH TẤT CẢ ĐƠN HÀNG
    // ======================================================
    @GetMapping
    public String listSaleOrders(Model model) {
        List<DTOSaleOrder> orders = daoSaleOrder.getAllSaleOrders();
        model.addAttribute("orders", orders);
        return "dealerPage/dealerCustomerOrderList";
    }

    // ======================================================
    // 2️⃣ FORM TẠO ĐƠN HÀNG MỚI (từ quotation đã duyệt)
    // ======================================================
    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session) {
        try {
            DTOAccount account = (DTOAccount) session.getAttribute("user");
            if (account == null || account.getDealerId() == null) {
                model.addAttribute("error", "Bạn cần đăng nhập bằng tài khoản Dealer!");
                return "redirect:/login";
            }

            int dealerId = account.getDealerId();
            List<DTOQuotation> approvedQuotations = daoQuotation.getQuotationsByDealer(dealerId)
                    .stream()
                    .filter(q -> "Approved".equalsIgnoreCase(q.getStatus()))
                    .toList();

            if (approvedQuotations.isEmpty()) {
                model.addAttribute("error", "Không có báo giá nào đã được duyệt.");
                model.addAttribute("redirectUrl", "/quotation/list");
                return "dealerPage/noQuotations";
            }

            model.addAttribute("order", new DTOSaleOrder());
            model.addAttribute("quotations", approvedQuotations);
            model.addAttribute("message", "Tìm thấy " + approvedQuotations.size() + " báo giá đã duyệt.");
            return "dealerPage/createSaleOrder";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải form tạo đơn hàng: " + e.getMessage());
            return "dealerPage/errorPage";
        }
    }

    // ======================================================
    // 3️⃣ XỬ LÝ SUBMIT FORM TẠO SALE ORDER
    // ======================================================
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
        System.out.println(vin);
        DTOAccount account = (DTOAccount) session.getAttribute("user");
        if (account == null) {
            model.addAttribute("error", "Bạn cần đăng nhập để tạo đơn hàng!");
            System.out.println("Noaccount found");
            return "redirect:/login";
        }

        Integer dealerID = account.getDealerId();
        if (dealerID == null) {
            model.addAttribute("error", "Tài khoản hiện tại không có DealerID hợp lệ!");
            return "redirect:/saleorder";
        }

        try {
            // 🧩 Kiểm tra báo giá
            DTOQuotation quotation = daoQuotation.getQuotationById(quotationID);
            if (quotation == null) {
                model.addAttribute("error", "Không tìm thấy báo giá #" + quotationID);
                return "redirect:/quotation/list";
            }

            if (!daoQuotation.isQuotationApproved(quotationID)) {
                model.addAttribute("error", "Báo giá chưa được duyệt! Trạng thái hiện tại: " + quotation.getStatus());
                return "redirect:/quotation/list";
            }

            if (quotation.getDealer().getDealerID() != dealerID) {
                model.addAttribute("error", "Báo giá thuộc dealer khác!");
                return "redirect:/quotation/list";
            }

            if (quotation.getCustomer().getCustomerID() != customerID) {
                model.addAttribute("error", "Khách hàng không trùng với báo giá!");
                return "redirect:/quotation/list";
            }

            List<DTOQuotationDetail> quotationDetails = daoQuotation.getQuotationDetails(quotationID);
            if (quotationDetails.isEmpty()) {
                model.addAttribute("error", "Không có chi tiết báo giá!");
                return "redirect:/quotation/list";
            }

            DTOQuotationDetail qDetail = quotationDetails.get(0);
            BigDecimal price = qDetail.getUnitPrice();

            // 🔧 Tạo đơn hàng
            DTOSaleOrder order = new DTOSaleOrder();
            order.setCustomer(new DTOCustomer());
            order.getCustomer().setCustomerID(customerID);

            order.setDealer(new DTODealer());
            order.getDealer().setDealerID(dealerID);

            order.setStaff(new DTODealerStaff());
            order.getStaff().setStaffID(staffID);

            order.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            order.setStatus(status);

            // 🔧 Chi tiết đơn hàng
            DTOVehicle vehicle = new DTOVehicle();
            vehicle.setVIN(vin);

            DTOSaleOrderDetail detail = new DTOSaleOrderDetail();
            detail.setVehicle(vehicle);
            detail.setPrice(price);
            detail.setQuantity(1);

            List<DTOSaleOrderDetail> details = new ArrayList<>();
            details.add(detail);
            order.setDetail(details);

            // 💾 Lưu vào DB
            boolean success = daoSaleOrder.insertSaleOrder(order);
            if (success) {
                model.addAttribute("message", "Tạo đơn hàng thành công!");
                return "redirect:/saleorder";
            } else {
                model.addAttribute("error", "Không thể tạo đơn hàng!");
                return "dealerPage/createSaleOrder";
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tạo đơn hàng: " + e.getMessage());
            return "dealerPage/errorPage";
        }
    }

    // ======================================================
    // 4️⃣ XEM CHI TIẾT 1 ĐƠN HÀNG
    // ======================================================
    @GetMapping("/detail/{id}")
    public String viewOrderDetail(@PathVariable("id") int id, Model model) {
        DTOSaleOrder order = daoSaleOrder.getSaleOrderById(id);
        if (order == null) {
            model.addAttribute("error", "Không tìm thấy đơn hàng!");
            return "redirect:/saleorder";
        }
        model.addAttribute("order", order);
        return "dealerPage/saleOrderDetail";
    }

    // ======================================================
    // 5️⃣ FORM TẠO ĐƠN HÀNG DÀNH CHO ADMIN / EVM
    // ======================================================
    @GetMapping("/admin/create")
    public String createOrderFormForEVM(Model model) {
        try {
            List<DTODealer> dealerList = daoDealer.getAllDealers();
            model.addAttribute("dealerList", dealerList);
            model.addAttribute("order", new DTOSaleOrder());
            return "evmPage/order-create";
        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải danh sách dealer: " + e.getMessage());
            return "dealerPage/errorPage";
        }
    }
}
