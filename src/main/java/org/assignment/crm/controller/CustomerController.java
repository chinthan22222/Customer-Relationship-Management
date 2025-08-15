package org.assignment.crm.controller;

import org.assignment.crm.entity.Customer;
import org.assignment.crm.exception.CustomerNotFound;
import org.assignment.crm.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP', 'SUPPORT')")
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP', 'SUPPORT')")
    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable Long id) {
        return customerService.findById(id)
                .orElseThrow(() -> new CustomerNotFound("Customer not found"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP', 'SUPPORT')")
    @GetMapping("/email/{email}")
    public Customer getCustomerByEmail(@PathVariable String email) {
        return customerService.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFound("Customer not found with email: " + email));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SALES_REP')")
    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        return customerService.updateCustomer(id, customer);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/active")
    public List<Customer> getActiveCustomers(){
        return this.customerService.findActiveCustomers();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/count")
    public long getCustomerCount(){
        return this.customerService.getTotalCount();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/{id}/In-active")
    public Customer getActiveCustomers(@PathVariable long id){
        return this.customerService.deactivateCustomer(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
}
