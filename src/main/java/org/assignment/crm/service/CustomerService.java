package org.assignment.crm.service;

import org.assignment.crm.entity.Customer;
import org.assignment.crm.enums.CustomerStatus;
import org.assignment.crm.exception.CustomerNotFound;
import org.assignment.crm.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public Customer createCustomer(Customer customer) {
        logger.info("Creating new customer with email: {}", customer.getEmail());
        try {
            customer.setCreatedAt(LocalDateTime.now());
            customer.setUpdatedAt(LocalDateTime.now());
            customer.setStatus(CustomerStatus.ACTIVE);

            logger.debug("Set default status ACTIVE for new customer: {}", customer.getEmail());

            Customer savedCustomer = customerRepository.save(customer);
            logger.info("Successfully created customer with ID: {} and email: {}",
                    savedCustomer.getId(), savedCustomer.getEmail());

            return savedCustomer;
        } catch (Exception e) {
            logger.error("Error creating customer with email {}: {}", customer.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        logger.info("Retrieving all customers");
        try {
            List<Customer> customers = this.customerRepository.findAll();
            logger.info("Successfully retrieved {} customers", customers.size());
            return customers;
        } catch (Exception e) {
            logger.error("Error retrieving all customers: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Optional<Customer> findById(long id) {
        logger.info("Finding customer by ID: {}", id);
        try {
            Optional<Customer> customer = this.customerRepository.findById(id);
            if (customer.isPresent()) {
                logger.info("Successfully found customer with ID: {}, email: {}",
                        id, customer.get().getEmail());
            } else {
                logger.info("No customer found with ID: {}", id);
            }
            return customer;
        } catch (Exception e) {
            logger.error("Error finding customer by ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Optional<Customer> findByEmail(String email) {
        logger.info("Finding customer by email: {}", email);
        try {
            Optional<Customer> customer = this.customerRepository.findCustomerByEmail(email);
            if (customer.isPresent()) {
                logger.info("Successfully found customer with email: {}, ID: {}",
                        email, customer.get().getId());
            } else {
                logger.info("No customer found with email: {}", email);
            }
            return customer;
        } catch (Exception e) {
            logger.error("Error finding customer by email {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        logger.info("Updating customer with ID: {}", id);
        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new CustomerNotFound("Customer doesn't Exist"));

            logger.debug("Found existing customer: {} for update", customer.getEmail());

            if (updatedCustomer.getFirstName() != null) {
                logger.debug("Updating firstName from '{}' to '{}' for customer ID: {}",
                        customer.getFirstName(), updatedCustomer.getFirstName(), id);
                customer.setFirstName(updatedCustomer.getFirstName());
            }

            if (updatedCustomer.getLastName() != null) {
                logger.debug("Updating lastName from '{}' to '{}' for customer ID: {}",
                        customer.getLastName(), updatedCustomer.getLastName(), id);
                customer.setLastName(updatedCustomer.getLastName());
            }

            if (updatedCustomer.getEmail() != null) {
                logger.debug("Updating email from '{}' to '{}' for customer ID: {}",
                        customer.getEmail(), updatedCustomer.getEmail(), id);
                customer.setEmail(updatedCustomer.getEmail());
            }

            if(updatedCustomer.getStatus()!=null){
                logger.debug("Updating status from '{}' to '{}' for customer ID: {}",
                        customer.getStatus(), updatedCustomer.getStatus(), id);
                customer.setStatus(updatedCustomer.getStatus());
            }

            if (updatedCustomer.getPhoneNumber() != null) {
                logger.debug("Updating phoneNumber from '{}' to '{}' for customer ID: {}",
                        customer.getPhoneNumber(), updatedCustomer.getPhoneNumber(), id);
                customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
            }

            if(updatedCustomer.getTotalPurchaseValue()!=null){
                logger.debug("Updating Total Purchase Value from '{}' to '{}' for customer ID: {}",
                        customer.getTotalPurchaseValue(), updatedCustomer.getTotalPurchaseValue(), id);
                customer.setTotalPurchaseValue(updatedCustomer.getTotalPurchaseValue());
            }

            customer.setUpdatedAt(LocalDateTime.now());
            Customer savedCustomer = customerRepository.save(customer);

            logger.info("Successfully updated customer with ID: {} and email: {}",
                    id, savedCustomer.getEmail());
            return savedCustomer;

        } catch (CustomerNotFound e) {
            logger.warn("Cannot update - customer not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating customer with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void deleteCustomer(Long id) {
        logger.info("Deleting customer with ID: {}", id);
        try {
            Optional<Customer> customerOpt = customerRepository.findById(id);
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                logger.debug("Found customer to delete: {} (email: {})", id, customer.getEmail());


                this.customerRepository.deleteById(id);
                logger.info("Successfully deleted customer with ID: {} (email: {})",
                        id, customer.getEmail());
            } else {
                logger.warn("Attempted to delete non-existent customer with ID: {}", id);
                this.customerRepository.deleteById(id);
            }
        } catch (Exception e) {
            logger.error("Error deleting customer with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public long getTotalCount() {
        logger.info("Getting total customer count");
        try {
            long count = customerRepository.count();
            logger.info("Total customer count: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("Error getting customer count: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Customer> findActiveCustomers() {
        logger.info("Retrieving active customers");
        try {
            List<Customer> activeCustomers = customerRepository.findAll().stream()
                    .filter(customer -> customer.getStatus() == CustomerStatus.ACTIVE)
                    .toList();
            logger.info("Successfully retrieved {} active customers", activeCustomers.size());
            return activeCustomers;
        } catch (Exception e) {
            logger.error("Error retrieving active customers: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public Customer deactivateCustomer(Long id) {
        logger.info("Deactivating customer with ID: {}", id);
        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new CustomerNotFound("Customer doesn't exist"));

            CustomerStatus oldStatus = customer.getStatus();
            if(oldStatus == CustomerStatus.INACTIVE){
                throw new RuntimeException("Already Inactive");
            }
            customer.setStatus(CustomerStatus.INACTIVE);
            customer.setUpdatedAt(LocalDateTime.now());

            Customer updatedCustomer = customerRepository.save(customer);
            logger.info("Successfully deactivated customer with ID: {} (email: {}), status changed from {} to {}",
                    id, customer.getEmail(), oldStatus, CustomerStatus.INACTIVE);

            return updatedCustomer;
        } catch (CustomerNotFound e) {
            logger.warn("Cannot deactivate - customer not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error deactivating customer with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
