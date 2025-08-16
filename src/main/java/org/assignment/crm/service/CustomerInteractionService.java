package org.assignment.crm.service;

import org.assignment.crm.entity.Customer;
import org.assignment.crm.entity.CustomerInteraction;
import org.assignment.crm.entity.User;
import org.assignment.crm.enums.InteractionType;
import org.assignment.crm.exception.CustomerNotFound;
import org.assignment.crm.exception.CustomerInteractionNotFound;
import org.assignment.crm.exception.UserNotFound;
import org.assignment.crm.repository.CustomerRepository;
import org.assignment.crm.repository.CustomerInteractionRepository;
import org.assignment.crm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerInteractionService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerInteractionService.class);

    @Autowired
    private CustomerInteractionRepository customerInteractionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CustomerInteraction addCustomerInteraction(CustomerInteraction customerInteraction) {
        logger.info("Creating new customer interaction of type: {}", customerInteraction.getType());
        try {
            if(customerInteraction.getInteractionDate() == null) {
                customerInteraction.setInteractionDate(LocalDateTime.now());
                logger.debug("Set default interaction date to current time");
            }

            if(customerInteraction.getType() == null) {
                customerInteraction.setType(InteractionType.EMAIL);
                logger.debug("Set default interaction type to EMAIL");
            }

            Customer customer = this.customerRepository.findById(customerInteraction.getCustomer().getId())
                            .orElseThrow(() -> new CustomerNotFound("Customer not found exception!"));

            User user = this.userRepository.findById(customerInteraction.getPerformedBy().getId())
                            .orElseThrow(() -> new UserNotFound("Support Not found With this id"));

            customerInteraction.setCustomer(customer);
            customerInteraction.setPerformedBy(user);

            customerInteraction.setCreatedTime(LocalDateTime.now());
            customerInteraction.setUpdateTime(LocalDateTime.now());

            CustomerInteraction savedInteraction = this.customerInteractionRepository.save(customerInteraction);
            logger.info("Successfully created customer interaction with ID: {} of type: {}",
                    savedInteraction.getId(), savedInteraction.getType());

            return savedInteraction;
        } catch (Exception e) {
            logger.error("Error creating customer interaction of type {}: {}",
                    customerInteraction.getType(), e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<CustomerInteraction> getAllCustomerInteractions() {
        logger.info("Retrieving all customer interactions");
        try {
            List<CustomerInteraction> interactions = this.customerInteractionRepository.findAll();
            logger.info("Successfully retrieved {} customer interactions", interactions.size());
            return interactions;
        } catch (Exception e) {
            logger.error("Error retrieving all customer interactions: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public CustomerInteraction getCustomerInteractionById(long interaction_id) {
        logger.info("Retrieving customer interaction with ID: {}", interaction_id);
        try {
            CustomerInteraction interaction = customerInteractionRepository.findById(interaction_id)
                    .orElseThrow(() -> new CustomerInteractionNotFound("Customer Interaction with this ID is not found!"));

            logger.info("Successfully retrieved customer interaction with ID: {} of type: {}",
                    interaction_id, interaction.getType());
            return interaction;
        } catch (CustomerInteractionNotFound e) {
            logger.warn("Customer interaction not found with ID: {}", interaction_id);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving customer interaction with ID {}: {}", interaction_id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<CustomerInteraction> getInteractionsByCustomerId(long customer_id) {
        logger.info("Retrieving interactions for customer ID: {}", customer_id);
        try {
            Customer customer = customerRepository.findById(customer_id)
                    .orElseThrow(() -> new CustomerNotFound("Customer not found with this id"));

            List<CustomerInteraction> interactions = customerInteractionRepository.findCustomerInteractionByCustomer_Id(customer_id);
            logger.info("Successfully retrieved {} interactions for customer ID: {} (email: {})",
                    interactions.size(), customer_id, customer.getEmail());

            return interactions;
        } catch (CustomerNotFound e) {
            logger.warn("Customer not found with ID: {} while retrieving interactions", customer_id);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving interactions for customer ID {}: {}", customer_id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<CustomerInteraction> getInteractionsByUserId(long user_id) {
        logger.info("Retrieving interactions performed by user ID: {}", user_id);
        try {
            User user = userRepository.findById(user_id)
                    .orElseThrow(() -> new UserNotFound("User not found with this id"));

            List<CustomerInteraction> interactions = customerInteractionRepository.findCustomerInteractionByPerformedBy_Id(user_id);
            logger.info("Successfully retrieved {} interactions performed by user ID: {} (username: {})",
                    interactions.size(), user_id, user.getUserName());

            return interactions;
        } catch (UserNotFound e) {
            logger.warn("User not found with ID: {} while retrieving interactions", user_id);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving interactions for user ID {}: {}", user_id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<CustomerInteraction> getInteractionsByType(InteractionType type) {
        logger.info("Retrieving interactions by type: {}", type);
        try {
            List<CustomerInteraction> interactions = customerInteractionRepository.findCustomerInteractionByType(type);
            logger.info("Successfully retrieved {} interactions of type: {}", interactions.size(), type);
            return interactions;
        } catch (Exception e) {
            logger.error("Error retrieving interactions by type {}: {}", type, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public CustomerInteraction updateCustomerInteraction(long interaction_id, CustomerInteraction existingInteraction) {
        logger.info("Updating customer interaction with ID: {}", interaction_id);
        try {
            CustomerInteraction interaction = this.customerInteractionRepository.findById(interaction_id)
                    .orElseThrow(() -> new CustomerInteractionNotFound("Customer Interaction with this ID is not found!"));

            logger.debug("Found existing interaction ID: {} of type: {}", interaction_id, interaction.getType());

            if (existingInteraction.getType() != null) {
                logger.debug("Updating interaction type from '{}' to '{}' for ID: {}",
                        interaction.getType(), existingInteraction.getType(), interaction_id);
                interaction.setType(existingInteraction.getType());
            }

            if (existingInteraction.getNotes() != null) {
                logger.debug("Updating interaction notes for ID: {}", interaction_id);
                interaction.setNotes(existingInteraction.getNotes());
            }

            if (existingInteraction.getInteractionDate() != null) {
                logger.debug("Updating interaction date from '{}' to '{}' for ID: {}",
                        interaction.getInteractionDate(), existingInteraction.getInteractionDate(), interaction_id);
                interaction.setInteractionDate(existingInteraction.getInteractionDate());
            }

            if (existingInteraction.getCustomer() != null) {
                Customer customer = customerRepository.findById(existingInteraction.getCustomer().getId())
                        .orElseThrow(() -> new CustomerNotFound("Customer not found with this id"));
                logger.debug("Updating interaction customer to ID: {} (email: {}) for interaction ID: {}",
                        customer.getId(), customer.getEmail(), interaction_id);
                interaction.setCustomer(customer);
            }

            if (existingInteraction.getPerformedBy() != null) {
                User user = userRepository.findById(existingInteraction.getPerformedBy().getId())
                        .orElseThrow(() -> new UserNotFound("User not found with this id"));
                logger.debug("Updating interaction performer to user ID: {} (username: {}) for interaction ID: {}",
                        user.getId(), user.getUserName(), interaction_id);
                interaction.setPerformedBy(user);
            }

            interaction.setUpdateTime(LocalDateTime.now());
            CustomerInteraction updatedInteraction = this.customerInteractionRepository.save(interaction);

            logger.info("Successfully updated customer interaction with ID: {} of type: {}",
                    interaction_id, updatedInteraction.getType());
            return updatedInteraction;

        } catch (CustomerInteractionNotFound | CustomerNotFound | UserNotFound e) {
            logger.warn("Update failed for interaction ID {}: {}", interaction_id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error updating customer interaction with ID {}: {}", interaction_id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void deleteCustomerInteraction(long interaction_id) {
        logger.info("Deleting customer interaction with ID: {}", interaction_id);
        try {
            CustomerInteraction interaction = this.customerInteractionRepository.findById(interaction_id)
                    .orElseThrow(() -> new CustomerInteractionNotFound("Customer Interaction with this ID is not found!"));

            logger.debug("Found interaction to delete: ID {} of type: {}", interaction_id, interaction.getType());

            this.customerInteractionRepository.delete(interaction);
            logger.info("Successfully deleted customer interaction with ID: {} of type: {}",
                    interaction_id, interaction.getType());
        } catch (CustomerInteractionNotFound e) {
            logger.warn("Cannot delete - customer interaction not found with ID: {}", interaction_id);
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting customer interaction with ID {}: {}", interaction_id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public long getTotalInteractionCount() {
        logger.info("Getting total interaction count");
        try {
            long count = customerInteractionRepository.count();
            logger.info("Total interaction count: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("Error getting interaction count: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<CustomerInteraction> getRecentInteractions(int limit) {
        logger.info("Retrieving {} most recent interactions", limit);
        try {
            List<CustomerInteraction> allInteractions = customerInteractionRepository.findAll();
            List<CustomerInteraction> recentInteractions = allInteractions.stream()
                    .sorted((a, b) -> b.getInteractionDate().compareTo(a.getInteractionDate()))
                    .limit(limit)
                    .toList();

            logger.info("Successfully retrieved {} recent interactions", recentInteractions.size());
            return recentInteractions;
        } catch (Exception e) {
            logger.error("Error retrieving recent interactions: {}", e.getMessage(), e);
            throw e;
        }
    }
}
