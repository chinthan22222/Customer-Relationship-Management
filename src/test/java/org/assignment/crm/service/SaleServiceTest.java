package org.assignment.crm.service;

import org.assignment.crm.entity.Customer;
import org.assignment.crm.entity.Sale;
import org.assignment.crm.entity.User;
import org.assignment.crm.enums.SaleStatus;
import org.assignment.crm.exception.CustomerNotFound;
import org.assignment.crm.exception.SaleNotFound;
import org.assignment.crm.exception.UserNotFound;
import org.assignment.crm.repository.CustomerRepository;
import org.assignment.crm.repository.SaleRepository;
import org.assignment.crm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;
    
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SaleService saleService;

    @Test
    void addSale_setsDefaultsAndSaves() {
        Sale input = new Sale();
        input.setAmount(new BigDecimal("100.00"));

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setTotalPurchaseValue(new BigDecimal("0.00"));
        input.setCustomer(customer);

        User salesRep = new User();
        salesRep.setId(1L);
        input.setSalesRep(salesRep);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(userRepository.findById(1L)).thenReturn(Optional.of(salesRep));
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> {
            Sale s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        Sale result = saleService.addSale(input);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isNotNull();
        verify(saleRepository).save(any(Sale.class));
    }

    @Test
    void getSaleById_whenMissing_throws() {
        when(saleRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> saleService.getSaleById(2L))
                .isInstanceOf(SaleNotFound.class);
    }

    @Test
    void updateSale_updatesAmountAndRelations() {
        Customer existingCustomer = new Customer();
        existingCustomer.setId(10L);
        existingCustomer.setTotalPurchaseValue(new BigDecimal("100.00"));

        Sale existing = new Sale();
        existing.setId(3L);
        existing.setAmount(new BigDecimal("50"));
        existing.setStatus(SaleStatus.PENDING);
        existing.setCustomer(existingCustomer);
        when(saleRepository.findById(3L)).thenReturn(Optional.of(existing));

        when(customerRepository.findById(10L)).thenReturn(Optional.of(existingCustomer));

        Customer newCustomer = new Customer();
        newCustomer.setId(11L);
        newCustomer.setTotalPurchaseValue(new BigDecimal("200.00"));
        when(customerRepository.findById(11L)).thenReturn(Optional.of(newCustomer));

        User rep = new User();
        rep.setId(22L);
        when(userRepository.findById(22L)).thenReturn(Optional.of(rep));

        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

        Sale updates = new Sale();
        updates.setAmount(new BigDecimal("150"));
        updates.setStatus(SaleStatus.COMPLETED);

        Customer customerRef = new Customer();
        customerRef.setId(11L);
        updates.setCustomer(customerRef);

        User userRef = new User();
        userRef.setId(22L);
        updates.setSalesRep(userRef);

        Sale result = saleService.updateSale(3L, updates);

        assertThat(result.getAmount()).isEqualTo(new BigDecimal("150"));
        assertThat(result.getStatus()).isEqualTo(SaleStatus.COMPLETED);
        assertThat(result.getCustomer()).isNotNull();
        assertThat(result.getSalesRep()).isNotNull();
    }


    @Test
    void getSaleByRepId_whenRepMissing_throws() {
        when(userRepository.findById(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> saleService.getSaleByRepId(9L))
                .isInstanceOf(UserNotFound.class);
    }

    @Test
    void getSaleByCustomerId_whenCustomerMissing_throws() {
        when(customerRepository.findById(7L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> saleService.getSaleByCustomerId(7L))
                .isInstanceOf(CustomerNotFound.class);
    }

    @Test
    void markStatus_helpers_callUpdateSaleStatus() {
        Sale s = new Sale();
        s.setId(1L);
        s.setStatus(SaleStatus.PENDING);
        when(saleRepository.findById(1L)).thenReturn(Optional.of(s));
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(saleService.markSaleAsCompleted(1L).getStatus()).isEqualTo(SaleStatus.COMPLETED);
        when(saleRepository.findById(1L)).thenReturn(Optional.of(s));
        assertThat(saleService.markSaleAsCanceled(1L).getStatus()).isEqualTo(SaleStatus.CANCELED);
        when(saleRepository.findById(1L)).thenReturn(Optional.of(s));
        assertThat(saleService.markSaleAsPending(1L).getStatus()).isEqualTo(SaleStatus.PENDING);
    }
}
