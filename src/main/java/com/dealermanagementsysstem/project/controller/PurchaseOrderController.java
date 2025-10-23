package com.dealermanagementsysstem.project.controller;

import com.dealermanagementsysstem.project.Model.DAOPurchaseOrder;
import com.dealermanagementsysstem.project.Model.DAOPurchaseOrderDetail;
import com.dealermanagementsysstem.project.Model.DTOPurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@Controller
@RequestMapping("/orderdealer")
public class PurchaseOrderController {

    @Autowired
    private DAOPurchaseOrder daoPurchaseOrder;

    @Autowired
    private DAOPurchaseOrderDetail daoPurchaseOrderDetail;

    /**
     * 🔹 Trang danh sách đơn hàng
     */
    /**
     * 🔹 Trang danh sách đơn hàng (chỉ hiển thị đơn của Dealer đang đăng nhập)
     */
    @GetMapping("")
    public String showOrderList(Model model) {
        try {
            // Lấy email của người đăng nhập (Spring Security)
            org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            org.springframework.security.core.userdetails.User user =
                    (org.springframework.security.core.userdetails.User) auth.getPrincipal();
            String email = user.getUsername();

            // Lấy DealerID dựa theo email đăng nhập
            int dealerId = daoPurchaseOrder.getDealerIdByEmail(email);

            if (dealerId <= 0) {
                model.addAttribute("message", "❌ Không tìm thấy Dealer tương ứng với tài khoản đăng nhập (" + email + ")");
                model.addAttribute("orders", List.of());
                return "dealerPage/orderStatusList";
            }

            // Lấy danh sách đơn hàng theo DealerID
            List<DTOPurchaseOrder> orders = daoPurchaseOrder.getPurchaseOrdersByDealerId(dealerId);

            model.addAttribute("orders", orders);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "⚠️ Lỗi khi tải danh sách đơn hàng: " + e.getMessage());
        }

        return "dealerPage/orderStatusList";
    }


    /**
     * 🔹 Khi chọn xe → mở form nhập chi tiết đơn hàng
     */
    @GetMapping("/create")
    public String showCreateForm(
            @RequestParam(required = false) Integer modelId,
            @RequestParam(required = false) Integer colorId,
            @RequestParam(required = false) String modelName,
            Model model) {

        model.addAttribute("modelId", modelId);
        model.addAttribute("colorId", colorId);
        model.addAttribute("modelName", modelName);
        model.addAttribute("order", new DTOPurchaseOrder());
        return "dealerPage/createDealerOrderForm"; // form nhập số lượng + version
    }

    /**
     * 🔹 Xử lý form tạo đơn hàng
     */
    @PostMapping("/create")
    public String createOrder(
            @RequestParam Integer modelId,
            @RequestParam Integer colorId,
            @RequestParam Integer quantity,
            @RequestParam String version,
            @RequestParam(required = false) String status,
            Model model) {

        try {
            // Lấy email người đang đăng nhập (Spring Security)
            org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            org.springframework.security.core.userdetails.User user =
                    (org.springframework.security.core.userdetails.User) auth.getPrincipal();
            String email = user.getUsername();

            // Lấy DealerID & StaffID dựa theo email đăng nhập
            int dealerId = daoPurchaseOrder.getDealerIdByEmail(email);
            int staffId = daoPurchaseOrder.getStaffIdByEmail(email);

            if (dealerId <= 0 || staffId <= 0) {
                model.addAttribute("message", "❌ Không tìm thấy Dealer hoặc Staff tương ứng với tài khoản đăng nhập (" + email + ")");
                return "dealerPage/success";
            }

            //Tạo đơn hàng mới
            DTOPurchaseOrder order = new DTOPurchaseOrder();
            order.setDealerId(dealerId);
            order.setStaffId(staffId);
            order.setStatus(status != null ? status : "Pending");
            order.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // ✅ Ghi vào DB
            int newOrderId = daoPurchaseOrder.insertPurchaseOrder(order);

            if (newOrderId > 0) {
                boolean added = daoPurchaseOrderDetail.insertOrderDetail(
                        newOrderId, modelId, colorId, quantity, version
                );
                model.addAttribute("message", added
                        ? " Đặt xe thành công!"
                        : "⚠Đơn đã tạo nhưng chưa ghi chi tiết!");
            } else {
                model.addAttribute("message", " Không thể tạo đơn hàng!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", " Lỗi hệ thống: " + e.getMessage());
        }

        return "dealerPage/success";
    }

    /**
     * 🔹 Trang success (chỉ khi load trực tiếp)
     */
    @GetMapping("/success")
    public String showSuccessPage(Model model) {
        if (!model.containsAttribute("message")) {
            model.addAttribute("message", " Order processed!");
        }
        return "dealerPage/success";
    }

    /**
     * 🔹 API: Lấy danh sách đơn hàng (JSON)
     */
    @ResponseBody
    @GetMapping("/api")
    public List<DTOPurchaseOrder> getAllOrders() {
        return daoPurchaseOrder.getAllPurchaseOrders();
    }

    /**
     * 🔹 API: Lấy đơn hàng theo ID (JSON)
     */
    @ResponseBody
    @GetMapping("/api/{id}")
    public DTOPurchaseOrder getOrderById(@PathVariable int id) {
        return daoPurchaseOrder.getPurchaseOrderById(id);
    }

    /**
     * 🔹 API: Cập nhật trạng thái đơn hàng
     */
    @ResponseBody
    @PutMapping("/api/{id}/status")
    public String updateStatus(@PathVariable int id, @RequestParam String status) {
        boolean updated = daoPurchaseOrder.updatePurchaseOrderStatus(id, status);
        return updated ? "Updated successfully" : "Update failed";
    }

    /**
     * 🔹 API: Xóa đơn hàng
     */
    @ResponseBody
    @DeleteMapping("/api/{id}")
    public String deleteOrder(@PathVariable int id) {
        int result = daoPurchaseOrder.deletePurchaseOrder(id);
        return result > 0 ? "Deleted successfully" : "Delete failed";
    }
}
