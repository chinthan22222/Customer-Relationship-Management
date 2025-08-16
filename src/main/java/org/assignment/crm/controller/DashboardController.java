package org.assignment.crm.controller;

import org.assignment.crm.entity.User;
import org.assignment.crm.enums.UserRole;
import org.assignment.crm.service.CustomerService;
import org.assignment.crm.service.SaleService;
import org.assignment.crm.service.UserService;
import org.assignment.crm.service.CustomerInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SaleService saleService;

    @Autowired
    private CustomerInteractionService customerInteractionService;

    @GetMapping("/{username}")
    public Map<String, Object> getUserDashboard(@PathVariable String username) {
        Optional<User> userOpt = userService.findByUserName(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + username);
        }

        User user = userOpt.get();
        Map<String, Object> dashboard = new HashMap<>();

        dashboard.put("userId", user.getId());
        dashboard.put("username", user.getUserName());
        dashboard.put("firstName", user.getFirstname());
        dashboard.put("lastName", user.getLastName());
        dashboard.put("email", user.getEmail());
        dashboard.put("role", user.getRole().toString());
        dashboard.put("status", user.getStatus().toString());

        switch (user.getRole()) {
            case ADMIN:
                dashboard.putAll(getAdminDashboardData());
                break;
            case MANAGER:
                dashboard.putAll(getManagerDashboardData());
                break;
            case SALES_REP:
                dashboard.putAll(getSalesRepDashboardData(user.getId()));
                break;
            case SUPPORT:
                dashboard.putAll(getSupportDashboardData(user.getId()));
                break;
        }

        return dashboard;
    }

    private Map<String, Object> getAdminDashboardData() {
        Map<String, Object> adminData = new HashMap<>();

        adminData.put("totalCustomers", customerService.getTotalCount());
        adminData.put("activeCustomers", customerService.findActiveCustomers().size());
        adminData.put("totalSales", saleService.getAllSales().size());
        adminData.put("totalUsers", userService.findAll().size());
        adminData.put("totalInteractions", customerInteractionService.getTotalInteractionCount());

        adminData.put("adminUsers", userService.findByRole(UserRole.ADMIN).size());
        adminData.put("managerUsers", userService.findByRole(UserRole.MANAGER).size());
        adminData.put("salesRepUsers", userService.findByRole(UserRole.SALES_REP).size());
        adminData.put("supportUsers", userService.findByRole(UserRole.SUPPORT).size());

        adminData.put("dashboardType", "ADMIN_OVERVIEW");
        adminData.put("welcomeMessage", "Welcome to the CRM Admin Dashboard! You have full system access.");

        return adminData;
    }

    private Map<String, Object> getManagerDashboardData() {
        Map<String, Object> managerData = new HashMap<>();

        managerData.put("totalCustomers", customerService.getTotalCount());
        managerData.put("activeCustomers", customerService.findActiveCustomers().size());
        managerData.put("totalSales", saleService.getAllSales().size());
        managerData.put("totalSalesReps", userService.findByRole(UserRole.SALES_REP).size());
        managerData.put("totalInteractions", customerInteractionService.getTotalInteractionCount());
        managerData.put("recentInteractions", customerInteractionService.getRecentInteractions(5).size());

        managerData.put("dashboardType", "MANAGER_OVERVIEW");
        managerData.put("welcomeMessage", "Welcome to the CRM Manager Dashboard! Monitor your team's performance.");

        return managerData;
    }

    private Map<String, Object> getSalesRepDashboardData(Long userId) {
        Map<String, Object> salesRepData = new HashMap<>();

        try {
            salesRepData.put("mySales", saleService.getSaleByRepId(userId).size());
            salesRepData.put("myInteractions", customerInteractionService.getInteractionsByUserId(userId).size());
            salesRepData.put("totalCustomers", customerService.getTotalCount());
            salesRepData.put("recentInteractions", customerInteractionService.getRecentInteractions(5).size());

            salesRepData.put("salesThisMonth", saleService.getSaleByRepId(userId).size());
            salesRepData.put("dashboardType", "SALES_REP_PERFORMANCE");
            salesRepData.put("welcomeMessage", "Welcome to your Sales Dashboard! Track your performance and manage your customers.");

        } catch (Exception e) {
            salesRepData.put("mySales", 0);
            salesRepData.put("myInteractions", 0);
            salesRepData.put("totalCustomers", customerService.getTotalCount());
            salesRepData.put("recentInteractions", 0);
            salesRepData.put("salesThisMonth", 0);
            salesRepData.put("dashboardType", "SALES_REP_PERFORMANCE");
            salesRepData.put("welcomeMessage", "Welcome to your Sales Dashboard! Start tracking your performance.");
        }

        return salesRepData;
    }

    private Map<String, Object> getSupportDashboardData(Long userId) {
        Map<String, Object> supportData = new HashMap<>();

        try {
            supportData.put("myInteractions", customerInteractionService.getInteractionsByUserId(userId).size());
            supportData.put("totalCustomers", customerService.getTotalCount());
            supportData.put("recentInteractions", customerInteractionService.getRecentInteractions(10).size());
            supportData.put("totalInteractions", customerInteractionService.getTotalInteractionCount());

            supportData.put("dashboardType", "SUPPORT_OVERVIEW");
            supportData.put("welcomeMessage", "Welcome to the Support Dashboard! Manage customer interactions and provide excellent service.");

        } catch (Exception e) {
            supportData.put("myInteractions", 0);
            supportData.put("totalCustomers", customerService.getTotalCount());
            supportData.put("recentInteractions", 0);
            supportData.put("totalInteractions", customerInteractionService.getTotalInteractionCount());
            supportData.put("dashboardType", "SUPPORT_OVERVIEW");
            supportData.put("welcomeMessage", "Welcome to the Support Dashboard! Start managing customer interactions.");
        }

        return supportData;
    }
}
