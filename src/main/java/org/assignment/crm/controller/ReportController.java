package org.assignment.crm.controller;

import org.assignment.crm.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        return reportService.getDashboardReport();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @GetMapping("/customer/{customerId}/activity")
    public Map<String, Object> getCustomerActivity(@PathVariable Long customerId) {
        return reportService.getCustomerActivityReport(customerId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/sales-trends")
    public Map<String, Object> getSalesTrends() {
        return reportService.getSalesTrendsReport();
    }
}
