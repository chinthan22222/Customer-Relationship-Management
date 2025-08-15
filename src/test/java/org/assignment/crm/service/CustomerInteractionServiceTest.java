package org.assignment.crm.service;

import org.assignment.crm.entity.CustomerInteraction;
import org.assignment.crm.enums.InteractionType;
import org.assignment.crm.exception.CustomerInteractionNotFound;
import org.assignment.crm.exception.CustomerNotFound;
import org.assignment.crm.exception.UserNotFound;
import org.assignment.crm.repository.CustomerInteractionRepository;
import org.assignment.crm.repository.CustomerRepository;
import org.assignment.crm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerInteractionServiceTest {

    @Mock
    private CustomerInteractionRepository interactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomerInteractionService service;

    @Test
    void addCustomerInteraction_setsDefaultsAndSaves() {
        CustomerInteraction input = new CustomerInteraction();
        input.setType(null);
        when(interactionRepository.save(any(CustomerInteraction.class))).thenAnswer(inv -> {
            CustomerInteraction i = inv.getArgument(0);
            i.setId(1L);
            return i;
        });

        CustomerInteraction result = service.addCustomerInteraction(input);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getType()).isNotNull();
    }

    @Test
    void getCustomerInteractionById_notFound_throws() {
        when(interactionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getCustomerInteractionById(99L))
                .isInstanceOf(CustomerInteractionNotFound.class);
    }

    @Test
    void getInteractionsByCustomerId_checksCustomerExists() {
        when(customerRepository.findById(5L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getInteractionsByCustomerId(5L))
                .isInstanceOf(CustomerNotFound.class);
    }

    @Test
    void getInteractionsByUserId_checksUserExists() {
        when(userRepository.findById(6L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getInteractionsByUserId(6L))
                .isInstanceOf(UserNotFound.class);
    }

    @Test
    void getInteractionsByType_delegatesToRepo() {
        when(interactionRepository.findCustomerInteractionByType(InteractionType.EMAIL))
                .thenReturn(List.of(new CustomerInteraction()));
        assertThat(service.getInteractionsByType(InteractionType.EMAIL)).hasSize(1);
        verify(interactionRepository).findCustomerInteractionByType(InteractionType.EMAIL);
    }
}
