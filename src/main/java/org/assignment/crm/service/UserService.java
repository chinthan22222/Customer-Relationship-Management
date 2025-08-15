package org.assignment.crm.service;

import org.assignment.crm.entity.User;
import org.assignment.crm.enums.UserRole;
import org.assignment.crm.enums.UserStatus;
import org.assignment.crm.exception.UserNameExists;
import org.assignment.crm.exception.UserNotFound;
import org.assignment.crm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user) {
        logger.info("Creating new user with username: {}", user.getUserName());
        try {
            if (userRepository.findUserByUserName(user.getUserName()).isPresent()) {
                logger.warn("Attempt to create user with existing username: {}", user.getUserName());
                throw new UserNameExists("Username already exists");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setStatus(UserStatus.ACTIVE);

            if (user.getRole() == null) {
                user.setRole(UserRole.SALES_REP);
                logger.debug("Set default role SALES_REP for user: {}", user.getUserName());
            }

            User savedUser = userRepository.save(user);
            logger.info("Successfully created user with ID: {} and username: {}", savedUser.getId(), savedUser.getUserName());
            return savedUser;

        } catch (DataIntegrityViolationException e) {
            logger.error("Database constraint violation while creating user {}: {}", user.getUserName(), e.getMessage());
            throw new UserNameExists("Username already exists");
        } catch (UserNameExists e) {
            logger.warn("Username already exists: {}", user.getUserName());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating user {}: {}", user.getUserName(), e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        logger.info("Finding user by ID: {}", id);
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                logger.info("Successfully found user with ID: {}, username: {}", id, user.get().getUserName());
            } else {
                logger.info("No user found with ID: {}", id);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error finding user by ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        logger.info("Retrieving all users");
        try {
            List<User> users = userRepository.findAll();
            logger.info("Successfully retrieved {} users", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error retrieving all users: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUserName(String userName) {
        logger.info("Finding user by username: {}", userName);
        try {
            Optional<User> user = userRepository.findUserByUserName(userName);
            if (user.isPresent()) {
                logger.info("Successfully found user with username: {}", userName);
            } else {
                logger.info("No user found with username: {}", userName);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error finding user by username {}: {}", userName, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        logger.info("Finding user by email: {}", email);
        try {
            Optional<User> user = userRepository.findUserByEmail(email);
            if (user.isPresent()) {
                logger.info("Successfully found user with email: {}", email);
            } else {
                logger.info("No user found with email: {}", email);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error finding user by email {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public User updateUser(Long id, User updatedUser) {
        logger.info("Updating user with ID: {}", id);
        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFound("User not found"));

            logger.debug("Found existing user: {} for update", existingUser.getUserName());

            if (updatedUser.getFirstname() != null) {
                logger.debug("Updating firstname from '{}' to '{}' for user ID: {}",
                        existingUser.getFirstname(), updatedUser.getFirstname(), id);
                existingUser.setFirstname(updatedUser.getFirstname());
            }

            if (updatedUser.getLastName() != null) {
                logger.debug("Updating lastname from '{}' to '{}' for user ID: {}",
                        existingUser.getLastName(), updatedUser.getLastName(), id);
                existingUser.setLastName(updatedUser.getLastName());
            }

            if (updatedUser.getEmail() != null) {
                logger.debug("Updating email from '{}' to '{}' for user ID: {}",
                        existingUser.getEmail(), updatedUser.getEmail(), id);
                existingUser.setEmail(updatedUser.getEmail());
            }

            if (updatedUser.getRole() != null) {
                logger.debug("Updating role from '{}' to '{}' for user ID: {}",
                        existingUser.getRole(), updatedUser.getRole(), id);
                existingUser.setRole(updatedUser.getRole());
            }

            if (updatedUser.getStatus() != null) {
                logger.debug("Updating status from '{}' to '{}' for user ID: {}",
                        existingUser.getStatus(), updatedUser.getStatus(), id);
                existingUser.setStatus(updatedUser.getStatus());
            }

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
                logger.debug("Updating password for user ID: {}", id);
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            existingUser.setUpdatedAt(LocalDateTime.now());
            User savedUser = userRepository.save(existingUser);

            logger.info("Successfully updated user with ID: {} and username: {}", id, savedUser.getUserName());
            return savedUser;

        } catch (UserNotFound e) {
            logger.warn("Cannot update - user not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating user with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        try {
            if (!userRepository.existsById(id)) {
                logger.warn("Cannot delete - user not found with ID: {}", id);
                throw new UserNotFound("User not found");
            }

            userRepository.deleteById(id);
            logger.info("Successfully deleted user with ID: {}", id);
        } catch (UserNotFound e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting user with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public boolean validateUser(String userName, String password) {
        logger.info("Validating user credentials for username: {}", userName);
        try {
            Optional<User> userOpt = findByUserName(userName);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                boolean isValid = user.getStatus() == UserStatus.ACTIVE &&
                        passwordEncoder.matches(password, user.getPassword());

                if (isValid) {
                    logger.info("User validation successful for username: {}", userName);
                } else {
                    if (user.getStatus() != UserStatus.ACTIVE) {
                        logger.warn("User validation failed - user not active: {}", userName);
                    } else {
                        logger.warn("User validation failed - incorrect password for username: {}", userName);
                    }
                }
                return isValid;
            } else {
                logger.warn("User validation failed - user not found: {}", userName);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error validating user {}: {}", userName, e.getMessage(), e);
            return false;
        }
    }

    @Transactional
    public User activateUser(Long id) {
        logger.info("Activating user with ID: {}", id);
        try {
            User user = findById(id)
                    .orElseThrow(() -> new UserNotFound("User not found"));

            UserStatus oldStatus = user.getStatus();
            user.setStatus(UserStatus.ACTIVE);
            user.setUpdatedAt(LocalDateTime.now());

            User activatedUser = userRepository.save(user);
            logger.info("Successfully activated user with ID: {} (username: {}), status changed from {} to {}",
                    id, user.getUserName(), oldStatus, UserStatus.ACTIVE);

            return activatedUser;
        } catch (UserNotFound e) {
            logger.warn("Cannot activate - user not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error activating user with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public User deactivateUser(Long id) {
        logger.info("Deactivating user with ID: {}", id);
        try {
            User user = findById(id)
                    .orElseThrow(() -> new UserNotFound("User not found"));

            UserStatus oldStatus = user.getStatus();
            user.setStatus(UserStatus.INACTIVE);
            user.setUpdatedAt(LocalDateTime.now());

            User deactivatedUser = userRepository.save(user);
            logger.info("Successfully deactivated user with ID: {} (username: {}), status changed from {} to {}",
                    id, user.getUserName(), oldStatus, UserStatus.INACTIVE);

            return deactivatedUser;
        } catch (UserNotFound e) {
            logger.warn("Cannot deactivate - user not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error deactivating user with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

}
