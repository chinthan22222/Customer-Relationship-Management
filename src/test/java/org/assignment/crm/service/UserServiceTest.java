package org.assignment.crm.service;

import org.assignment.crm.entity.User;
import org.assignment.crm.enums.UserRole;
import org.assignment.crm.enums.UserStatus;
import org.assignment.crm.exception.UserNameExists;
import org.assignment.crm.exception.UserNotFound;
import org.assignment.crm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_success_encodesPassword_andSetsDefaults() {
        User input = new User();
        input.setUserName("test");
        input.setPassword("password");
        input.setRole(UserRole.MANAGER);

        when(userRepository.findUserByUserName("test")).thenReturn(Optional.empty());

        when(passwordEncoder.encode("password")).thenReturn("ENC");

        User saved = new User();
        saved.setId(10L);
        saved.setUserName("test");
        saved.setPassword("ENC");
        saved.setRole(UserRole.MANAGER);
        saved.setStatus(UserStatus.ACTIVE);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.createUser(input);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getPassword()).isEqualTo("ENC");
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getRole()).isEqualTo(UserRole.MANAGER);

        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_whenUsernameExists_throwsUserNameExists() {
        User input = new User();
        input.setUserName("test");

        when(userRepository.findUserByUserName("test")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.createUser(input))
                .isInstanceOf(UserNameExists.class);
    }

    @Test
    void updateUser_whenNotFound_throwsUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(1L, new User()))
                .isInstanceOf(UserNotFound.class);
    }

    @Test
    void activateAndDeactivate_updatesStatusAndSaves() {
        User existing = new User();
        existing.setId(2L);
        existing.setUserName("test");
        existing.setStatus(UserStatus.INACTIVE);

        when(userRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User activated = userService.activateUser(2L);
        assertThat(activated.getStatus()).isEqualTo(UserStatus.ACTIVE);

        activated.setStatus(UserStatus.ACTIVE);
        when(userRepository.findById(2L)).thenReturn(Optional.of(activated));

        User deactivated = userService.deactivateUser(2L);
        assertThat(deactivated.getStatus()).isEqualTo(UserStatus.INACTIVE);

        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void validateUser_checksPasswordAndStatus() {
        User user = new User();
        user.setUserName("test");
        user.setPassword("ENC"); // Encoded password stored in DB
        user.setStatus(UserStatus.ACTIVE);

        when(userRepository.findUserByUserName("test")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(eq("password"), eq("ENC"))).thenReturn(true);

        boolean valid = userService.validateUser("test", "password");
        assertThat(valid).isTrue();

        when(passwordEncoder.matches(eq("wrong"), eq("ENC"))).thenReturn(false);

        boolean invalid = userService.validateUser("test", "wrong");
        assertThat(invalid).isFalse();
    }

    @Test
    void validateUser_whenUserNotFound_returnsFalse() {
        when(userRepository.findUserByUserName("nonexistent")).thenReturn(Optional.empty());

        boolean result = userService.validateUser("nonexistent", "password");
        assertThat(result).isFalse();
    }

    @Test
    void validateUser_whenUserInactive_returnsFalse() {
        User inactiveUser = new User();
        inactiveUser.setUserName("inactive");
        inactiveUser.setPassword("ENC");
        inactiveUser.setStatus(UserStatus.INACTIVE);

        when(userRepository.findUserByUserName("inactive")).thenReturn(Optional.of(inactiveUser));

        boolean result = userService.validateUser("inactive", "password");
        assertThat(result).isFalse();

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void deleteUser_whenNotExists_throwsUserNotFound() {
        when(userRepository.existsById(5L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(5L))
                .isInstanceOf(UserNotFound.class);
    }

    @Test
    void deleteUser_whenExists_deletesSuccessfully() {
        when(userRepository.existsById(5L)).thenReturn(true);

        userService.deleteUser(5L);

        verify(userRepository).deleteById(5L);
    }
}
