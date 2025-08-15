package org.assignment.crm.controller;

import org.assignment.crm.entity.Sale;
import org.assignment.crm.enums.SaleStatus;
import org.assignment.crm.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sale createSale(@RequestBody Sale sale) {
        return saleService.addSale(sale);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @GetMapping
    public List<Sale> getAllSales() {
        return saleService.getAllSales();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @GetMapping("/{id}")
    public Sale getSaleById(@PathVariable long id) {
        return saleService.getSaleById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @GetMapping("/rep/{repId}")
    public List<Sale> getSalesByRepId(@PathVariable long repId) {
        return saleService.getSaleByRepId(repId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @GetMapping("/customer/{customerId}")
    public List<Sale> getSalesByCustomerId(@PathVariable long customerId) {
        return saleService.getSaleByCustomerId(customerId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @PutMapping("/{id}")
    public Sale updateSale(@PathVariable long id, @RequestBody Sale sale) {
        return saleService.updateSale(id, sale);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSale(@PathVariable long id) {
        saleService.deleteSale(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @PutMapping("/{id}/complete")
    public Sale markSaleAsCompleted(@PathVariable long id) {
        return saleService.markSaleAsCompleted(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @PutMapping("/{id}/cancel")
    public Sale markSaleAsCanceled(@PathVariable long id) {
        return saleService.markSaleAsCanceled(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @PutMapping("/{id}/pending")
    public Sale markSaleAsPending(@PathVariable long id) {
        return saleService.markSaleAsPending(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @GetMapping("/status/{status}")
    public List<Sale> getSalesByStatus(@PathVariable SaleStatus status) {
        return saleService.getSalesByStatus(status);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @GetMapping("/completed")
    public List<Sale> getCompletedSales() {
        return saleService.getCompletedSales();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @GetMapping("/canceled")
    public List<Sale> getCanceledSales() {
        return saleService.getCanceledSales();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @PutMapping("/{id}/status/{status}")
    public Sale updateSaleStatus(@PathVariable long id, @PathVariable SaleStatus status) {
        return saleService.updateSaleStatus(id, status);
    }
}
