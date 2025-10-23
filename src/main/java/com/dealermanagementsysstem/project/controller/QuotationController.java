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
import java.util.List;

@Controller
@RequestMapping("/quotation")
public class QuotationController {

    private final DAOQuotation dao = new DAOQuotation();

    // ✅ Hiển thị form báo giá
    @GetMapping("/new")
    public String showQuotationForm(
            @RequestParam("vin") String vin,
            HttpSession session,
            Model model
    ) {
        System.out.println("🧾 [DEBUG] Open quotation form for VIN: " + vin);

        // 1️⃣ Lấy thông tin xe
        System.out.println("🔍 [DEBUG] Looking for vehicle with VIN: " + vin);
        DTOVehicle vehicle = dao.getVehicleByVIN(vin);
        if (vehicle == null) {
            System.out.println("❌ [ERROR] Vehicle not found for VIN: " + vin);
            model.addAttribute("error", "Vehicle not found for VIN: " + vin + ". Please check the VIN and try again.");
            return "dealerPage/errorPage";
        }
        System.out.println("✅ [SUCCESS] Vehicle found: " + vehicle.getModelName() + " (VIN: " + vehicle.getVIN() + ")");

        // 2️⃣ Lấy thông tin dealer từ session (debug)
        DTOAccount account = (DTOAccount) session.getAttribute("user");
        System.out.println("🔎 [DEBUG] Session user: " + (account != null ? account.getUsername() : "null")
                + ", dealerId=" + (account != null ? account.getDealerId() : null));

        DTODealer dealer = null;
        if (account != null && account.getDealerId() != null) {
            dealer = dao.getDealerByID(account.getDealerId());
            System.out.println("🏢 [DEBUG] Resolved dealer from session: " + (dealer != null ? dealer.getDealerName() : "null"));
        } else {
            // No dealer in session: load dealer list so user can pick in the form instead of redirecting to login
            try {
                List<DTODealer> dealerList = daoDealer.getAllDealers();
                model.addAttribute("dealerList", dealerList);
                System.out.println("📄 [DEBUG] No dealer in session. Providing dealerList size=" + (dealerList != null ? dealerList.size() : 0));
            } catch (Exception ex) {
                System.out.println("❌ [ERROR] Failed to load dealer list: " + ex.getMessage());
            }
        }

        // 3️⃣ Ngày tạo báo giá
        Timestamp createdAt = Timestamp.valueOf(LocalDateTime.now());

        // 4️⃣ Load customers for selection
        try {
            DAOCustomer customerDAO = new DAOCustomer();
            List<DTOCustomer> customerList = customerDAO.getAllCustomers();
            model.addAttribute("customerList", customerList);
            System.out.println("👥 [DEBUG] Loaded " + customerList.size() + " customers for selection");
        } catch (Exception ex) {
            System.out.println("❌ [ERROR] Failed to load customer list: " + ex.getMessage());
        }

        // 5️⃣ Truyền dữ liệu sang view
        if (dealer != null) {
            model.addAttribute("dealer", dealer);
        }
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("createdAt", createdAt);

        return "dealerPage/quotationForm"; // ✅ Tên file HTML của bạn
    }

    // 🔥 CORE FLOW STEP 2: Save quotation to database
    @PostMapping("/save")
    public String saveQuotation(
            @RequestParam("customerID") int customerID,
            @RequestParam("vin") String vin,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
            @RequestParam(value = "dealerID", required = false) Integer dealerIDParam,
            HttpSession session,
            Model model
    ) {
        System.out.println("💾 [DEBUG] Saving quotation | customerID=" + customerID + ", vin=" + vin
                + ", quantity=" + quantity + ", dealerIDParam=" + dealerIDParam);
        
        // Debug session info
        DTOAccount account = (DTOAccount) session.getAttribute("user");
        System.out.println("🔍 [DEBUG] Session user: " + (account != null ? account.getUsername() : "null"));

        try {
            // 1️⃣ Get dealer info from session
            Integer resolvedDealerId = null;
            if (account != null && account.getDealerId() != null) {
                resolvedDealerId = account.getDealerId();
                System.out.println("🏢 [DEBUG] Using dealer from session: " + resolvedDealerId);
            } else if (dealerIDParam != null) {
                resolvedDealerId = dealerIDParam;
                System.out.println("🏢 [DEBUG] Using dealer from form param: " + resolvedDealerId);
            }

            if (resolvedDealerId == null) {
                System.out.println("⚠️ [WARN] No dealer in session and no dealerID provided in form");
                model.addAttribute("error", "Please select a dealer to create quotation.");
                return "dealerPage/quotationForm";
            }

            DTODealer dealer = dao.getDealerByID(resolvedDealerId);
            if (dealer == null) {
                model.addAttribute("error", "Không tìm thấy thông tin đại lý.");
                return "dealerPage/errorPage";
            }

            // 2️⃣ Get customer info
            DAOCustomer customerDAO = new DAOCustomer();
            DTOCustomer customer = customerDAO.getAllCustomers().stream()
                    .filter(c -> c.getCustomerID() == customerID)
                    .findFirst()
                    .orElse(null);

            if (customer == null) {
                model.addAttribute("error", "Không tìm thấy thông tin khách hàng.");
                return "dealerPage/errorPage";
            }

            // 3️⃣ Get vehicle info
            DTOVehicle vehicle = dao.getVehicleByVIN(vin);
            if (vehicle == null) {
                model.addAttribute("error", "Không tìm thấy thông tin xe.");
                return "dealerPage/errorPage";
            }

            // 4️⃣ Create quotation object
            DTOQuotation quotation = new DTOQuotation();
            quotation.setCustomer(customer);
            quotation.setDealer(dealer);
            quotation.setVehicle(vehicle);
            quotation.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            quotation.setStatus("Pending");

            // 5️⃣ Save quotation to database
            int quotationID = dao.insertQuotation(quotation);

            if (quotationID > 0) {
                System.out.println("✅ [SUCCESS] Quotation saved successfully with ID: " + quotationID);
                model.addAttribute("message", "Quotation created successfully! ID: " + quotationID);
                model.addAttribute("quotationID", quotationID);
                return "redirect:/quotation/preview/" + quotationID;
            } else {
                System.out.println("❌ [FAILED] Failed to save quotation");
                model.addAttribute("error", "Failed to create quotation. Please try again!");
                return "dealerPage/quotationForm";
            }

        } catch (Exception e) {
            System.out.println("❌ [ERROR] Exception while saving quotation: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while creating quotation: " + e.getMessage());
            return "dealerPage/quotationForm";
        }
    }

    // 🔥 CORE FLOW STEP 3: List all quotations (for dealer to review)
    @GetMapping("/list")
    public String listQuotations(Model model) {
        System.out.println("📋 [DEBUG] Loading quotations list");

        try {
            List<DTOQuotation> quotations = dao.getAllQuotations();
            model.addAttribute("quotations", quotations);
            model.addAttribute("message", "Found " + quotations.size() + " quotations");
            
            System.out.println("✅ [SUCCESS] Loaded " + quotations.size() + " quotations");
            return "dealerPage/quotationList";
        } catch (Exception e) {
            System.out.println("❌ [ERROR] Failed to load quotations: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to load quotations: " + e.getMessage());
            return "dealerPage/errorPage";
        }
    }

    // 🔥 CORE FLOW STEP 4: View quotation details
    @GetMapping("/detail/{id}")
    public String viewQuotationDetail(@PathVariable("id") int id, Model model) {
        System.out.println("🔍 [DEBUG] Viewing quotation detail for ID: " + id);

        try {
            DTOQuotation quotation = dao.getQuotationById(id);
            if (quotation == null) {
                model.addAttribute("error", "Quotation not found!");
                return "redirect:/quotation/list";
            }

            // Get quotation details (price information)
            List<DTOQuotationDetail> details = dao.getQuotationDetails(id);
            quotation.setQuotationDetails(details);

            // Calculate total price from details
            if (details != null && !details.isEmpty()) {
                double totalPrice = details.stream()
                    .mapToDouble(detail -> detail.getUnitPrice().doubleValue() * detail.getQuantity())
                    .sum();
                quotation.setTotalPrice(totalPrice);
                System.out.println("💰 [DEBUG] Calculated total price: $" + totalPrice);
            } else {
                System.out.println("⚠️ [WARNING] No quotation details found for ID: " + id);
            }

            model.addAttribute("quotation", quotation);
            model.addAttribute("details", details);
            
            System.out.println("✅ [SUCCESS] Loaded quotation details for ID: " + id);
            return "dealerPage/quotationDetail";
        } catch (Exception e) {
            System.out.println("❌ [ERROR] Failed to load quotation details: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to load quotation details: " + e.getMessage());
            return "redirect:/quotation/list";
        }
    }

    // 🔥 CORE FLOW STEP 5: Approve quotation
    @PostMapping("/approve/{id}")
    public String approveQuotation(@PathVariable("id") int id, Model model) {
        System.out.println("✅ [DEBUG] Approving quotation ID: " + id);

        try {
            // First, check if quotation exists and get current status
            DTOQuotation quotation = dao.getQuotationById(id);
            if (quotation == null) {
                System.out.println("❌ [ERROR] Quotation not found for ID: " + id);
                model.addAttribute("error", "Quotation not found!");
                return "redirect:/quotation/list";
            }
            
            System.out.println("🔍 [DEBUG] Current quotation status: " + quotation.getStatus());
            System.out.println("🔍 [DEBUG] Attempting to update status to: Accepted");
            
            boolean success = dao.updateQuotationStatus(id, "Accepted");
            if (success) {
                System.out.println("✅ [SUCCESS] Quotation approved successfully: " + id);
                model.addAttribute("message", "Quotation approved successfully!");
            } else {
                System.out.println("❌ [FAILED] Failed to approve quotation: " + id);
                model.addAttribute("error", "Failed to approve quotation!");
            }
        } catch (Exception e) {
            System.out.println("❌ [ERROR] Exception while approving quotation: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while approving quotation: " + e.getMessage());
        }

        return "redirect:/quotation/list";
    }

    // 🔥 CORE FLOW STEP 6: Reject quotation
    @PostMapping("/reject/{id}")
    public String rejectQuotation(@PathVariable("id") int id, Model model) {
        System.out.println("❌ [DEBUG] Rejecting quotation ID: " + id);

        try {
            boolean success = dao.updateQuotationStatus(id, "Rejected");
            if (success) {
                System.out.println("✅ [SUCCESS] Quotation rejected successfully: " + id);
                model.addAttribute("message", "Quotation rejected successfully!");
            } else {
                System.out.println("❌ [FAILED] Failed to reject quotation: " + id);
                model.addAttribute("error", "Failed to reject quotation!");
            }
        } catch (Exception e) {
            System.out.println("❌ [ERROR] Exception while rejecting quotation: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while rejecting quotation: " + e.getMessage());
        }

        return "redirect:/quotation/list";
    }

    // 🔥 CORE FLOW: Quotation preview with PDF export and Create Order buttons
    @GetMapping("/preview/{id}")
    public String previewQuotation(@PathVariable("id") int id, Model model) {
        System.out.println("👁️ [DEBUG] Previewing quotation for ID: " + id);

        try {
            DTOQuotation quotation = dao.getQuotationById(id);
            if (quotation == null) {
                model.addAttribute("error", "Quotation not found!");
                return "redirect:/quotation/list";
            }

            // Get quotation details (price information)
            List<DTOQuotationDetail> details = dao.getQuotationDetails(id);
            quotation.setQuotationDetails(details);

            // Calculate total price from details
            if (details != null && !details.isEmpty()) {
                double totalPrice = details.stream()
                    .mapToDouble(detail -> detail.getUnitPrice().doubleValue() * detail.getQuantity())
                    .sum();
                quotation.setTotalPrice(totalPrice);
            }

            model.addAttribute("quotation", quotation);
            model.addAttribute("details", details);
            
            System.out.println("✅ [SUCCESS] Loaded quotation preview for ID: " + id);
            return "dealerPage/quotationPreview";
        } catch (Exception e) {
            System.out.println("❌ [ERROR] Failed to load quotation preview: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to load quotation preview: " + e.getMessage());
            return "redirect:/quotation/list";
        }
    }

    @Autowired
    private DAODealer daoDealer;

    @GetMapping("/quotation/create")
    public String createQuotationForm(Model model) throws SQLException {
        List<DTODealer> dealerList = daoDealer.getAllDealers();
        model.addAttribute("dealerList", dealerList);
        model.addAttribute("quotation", new DTOQuotation());
        return "evmPage/quotation-create";
    }
}
