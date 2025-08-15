package org.assignment.crm.service;

import org.assignment.crm.entity.User;
import org.assignment.crm.entity.UserPrincipal;
import org.assignment.crm.exception.UsernameNotFound;
import org.assignment.crm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MyUserService myUserService;

    @Test
    void loadUserByUsername_whenPresent_returnsUserPrincipal() {
        User u = new User();
        u.setUserName("test");
        when(userRepository.findUserByUserName("test")).thenReturn(Optional.of(u));

        UserDetails details = myUserService.loadUserByUsername("test");
        assertThat(details).isInstanceOf(UserPrincipal.class);
    }

    @Test
    void loadUserByUsername_whenMissing_throws() {
        when(userRepository.findUserByUserName("missing")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> myUserService.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFound.class);
    }
}
