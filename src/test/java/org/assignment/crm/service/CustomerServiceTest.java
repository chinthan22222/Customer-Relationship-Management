package org.assignment.crm.service;

import org.assignment.crm.entity.Customer;
import org.assignment.crm.enums.CustomerStatus;
import org.assignment.crm.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_ShouldReturnSavedCustomer() {

        Customer savedCustomer = new Customer();
        savedCustomer.setId(1L);
        savedCustomer.setFirstName("test");
        savedCustomer.setEmail("hello@test.com");
        savedCustomer.setStatus(CustomerStatus.ACTIVE);

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = customerService.createCustomer(savedCustomer);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("test");
        assertThat(result.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void findById_WhenCustomerExists_ShouldReturnCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("test");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("test");
    }

    @Test
    void findById_WhenCustomerDoesNotExist_ShouldReturnEmpty() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Customer> result = customerService.findById(1L);

        assertThat(result).isEmpty();
    }
}
