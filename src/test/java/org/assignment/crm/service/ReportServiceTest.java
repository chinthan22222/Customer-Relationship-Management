package org.assignment.crm.service;

import org.assignment.crm.entity.Customer;
import org.assignment.crm.entity.CustomerInteraction;
import org.assignment.crm.entity.Sale;
import org.assignment.crm.enums.InteractionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private SaleService saleService;

    @Mock
    private CustomerInteractionService interactionService;

    @InjectMocks
    private ReportService reportService;

    @Test
    void getDashboardReport_aggregatesCountsAndRevenue() {
        Customer c1 = new Customer();
        Customer c2 = new Customer();
        when(customerService.findAll()).thenReturn(List.of(c1, c2));

        Sale s1 = new Sale(); s1.setAmount(new BigDecimal("10")); s1.setUpdatedAt(LocalDateTime.now()); s1.setSaleDate(LocalDateTime.now());
        Sale s2 = new Sale(); s2.setAmount(new BigDecimal("20")); s2.setUpdatedAt(LocalDateTime.now()); s2.setSaleDate(LocalDateTime.now());
        when(saleService.getAllSales()).thenReturn(List.of(s1, s2));

        CustomerInteraction i1 = new CustomerInteraction(); i1.setType(InteractionType.EMAIL);
        CustomerInteraction i2 = new CustomerInteraction(); i2.setType(InteractionType.CALL);
        CustomerInteraction i3 = new CustomerInteraction(); i3.setType(InteractionType.MEETING);
        when(interactionService.getAllCustomerInteractions()).thenReturn(List.of(i1, i2, i3));

        Map<String, Object> dashboard = reportService.getDashboardReport();
        assertThat(dashboard.get("totalCustomers")).isEqualTo(2);
        assertThat(dashboard.get("totalSales")).isEqualTo(2);
        assertThat(dashboard.get("totalInteractions")).isEqualTo(3);
        assertThat(dashboard.get("totalRevenue")).isEqualTo(new BigDecimal("30"));
    }

    @Test
    void calculateCustomerActivity_summarizesByCustomer() {
        Customer customer = new Customer();
        customer.setId(5L);
        when(customerService.findById(5L)).thenReturn(java.util.Optional.of(customer));

        Sale s1 = new Sale(); s1.setAmount(new BigDecimal("100")); s1.setUpdatedAt(LocalDateTime.now()); s1.setSaleDate(LocalDateTime.now());
        when(saleService.getSaleByCustomerId(5L)).thenReturn(List.of(s1));

        CustomerInteraction int1 = new CustomerInteraction(); int1.setType(InteractionType.EMAIL);
        when(interactionService.getInteractionsByCustomerId(5L)).thenReturn(List.of(int1));

        Map<String, Object> report = reportService.getCustomerActivityReport(5L);
        assertThat(report.get("customer")).isEqualTo(customer);
        assertThat(report.get("totalSales")).isEqualTo(1);
        assertThat(report.get("totalRevenue")).isEqualTo(new BigDecimal("100"));
        assertThat(report.get("totalInteractions")).isEqualTo(1);
    }
}
