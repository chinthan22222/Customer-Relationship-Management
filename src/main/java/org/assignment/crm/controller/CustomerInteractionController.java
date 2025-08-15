package org.assignment.crm.controller;

import org.assignment.crm.entity.CustomerInteraction;
import org.assignment.crm.enums.InteractionType;
import org.assignment.crm.service.CustomerInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-interactions")
public class CustomerInteractionController {

    @Autowired
    private CustomerInteractionService customerInteractionService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP', 'SUPPORT')")
    @GetMapping
    public List<CustomerInteraction> getAllCustomerInteractions() {
        return this.customerInteractionService.getAllCustomerInteractions();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP', 'SUPPORT')")
    @GetMapping("/{id}")
    public CustomerInteraction getCustomerInteractionsById(@PathVariable long id) {
        return this.customerInteractionService.getCustomerInteractionById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP', 'SUPPORT')")
    @GetMapping("/customer/{id}")
    public List<CustomerInteraction> getCustomerInteractionsByCustomerId(@PathVariable long id) {
        return this.customerInteractionService.getInteractionsByCustomerId(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP', 'SUPPORT')")
    @GetMapping("/user/{id}")
    public List<CustomerInteraction> getCustomerInteractionsByUserId(@PathVariable long id) {
        return this.customerInteractionService.getInteractionsByUserId(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP', 'SUPPORT')")
    @GetMapping("/type/{interactionType}")
    public List<CustomerInteraction> getCustomerInteractionByType(
            @PathVariable InteractionType interactionType) {
        return this.customerInteractionService.getInteractionsByType(interactionType);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/total-count")
    public long totalInteractions() {
        return this.customerInteractionService.getTotalInteractionCount();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP', 'SUPPORT')")
    @GetMapping("/recent")
    public List<CustomerInteraction> getRecentInteractions(
            @RequestParam(defaultValue = "10") int limit) {
        return this.customerInteractionService.getRecentInteractions(limit);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP', 'SUPPORT')")
    @PostMapping
    public CustomerInteraction addCustomerInteraction(
            @RequestBody CustomerInteraction customerInteraction) {
        return this.customerInteractionService.addCustomerInteraction(customerInteraction);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @PutMapping("/{id}")
    public CustomerInteraction updateCustomerInteraction(
            @PathVariable long id,
            @RequestBody CustomerInteraction existingCustomerInteraction) {
        return this.customerInteractionService.updateCustomerInteraction(id, existingCustomerInteraction);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCustomerInteraction(@PathVariable Long id) {
        customerInteractionService.deleteCustomerInteraction(id);
    }
}
