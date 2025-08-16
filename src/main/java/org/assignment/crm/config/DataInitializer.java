package org.assignment.crm.config;

import org.assignment.crm.entity.Customer;
import org.assignment.crm.entity.CustomerInteraction;
import org.assignment.crm.entity.Sale;
import org.assignment.crm.entity.User;
import org.assignment.crm.enums.*;
import org.assignment.crm.repository.CustomerInteractionRepository;
import org.assignment.crm.repository.CustomerRepository;
import org.assignment.crm.repository.SaleRepository;
import org.assignment.crm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private CustomerInteractionRepository customerInteractionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data initialization...");

        // Check if data already exists
        if (userRepository.count() > 0) {
            logger.info("Data already exists. Skipping initialization.");
            return;
        }

        initializeUsers();
        initializeCustomers();
        initializeSales();
        initializeCustomerInteractions();

        logger.info("Data initialization completed successfully!");
    }

    private void initializeUsers() {
        logger.info("Initializing users...");

        List<User> users = Arrays.asList(
            createUser("admin_test", "admin.test@demo.com", "Test", "Admin", UserRole.ADMIN, UserStatus.ACTIVE),
            createUser("sales_demo1", "sales1.demo@test.com", "Demo", "Sales1", UserRole.SALES_REP, UserStatus.ACTIVE),
            createUser("sales_demo2", "sales2.demo@test.com", "Demo", "Sales2", UserRole.SALES_REP, UserStatus.ACTIVE),
            createUser("manager_test", "manager.test@demo.com", "Test", "Manager", UserRole.MANAGER, UserStatus.ACTIVE),
            createUser("support_demo", "support.demo@test.com", "Demo", "Support", UserRole.SUPPORT, UserStatus.INACTIVE)
        );

        userRepository.saveAll(users);
        logger.info("Successfully created {} users", users.size());
    }

    private void initializeCustomers() {
        logger.info("Initializing customers...");

        List<Customer> customers = Arrays.asList(
            createCustomer("Test", "Customer1", "test.customer1@demo.com", "+1-555-0101", "Demo Corp", "123 Test St, Demo City", CustomerStatus.ACTIVE, new BigDecimal("1500.00")),
            createCustomer("Demo", "Client2", "demo.client2@test.com", "+1-555-0102", "Test Industries", "456 Demo Ave, Test Town", CustomerStatus.ACTIVE, new BigDecimal("2750.50")),
            createCustomer("Sample", "User3", "sample.user3@demo.com", "+1-555-0103", "Demo Solutions", "789 Sample Blvd, Demo Village", CustomerStatus.ACTIVE, new BigDecimal("890.25")),
            createCustomer("Trial", "Account4", "trial.account4@test.com", "+1-555-0104", "Test Enterprises", "321 Trial Rd, Test City", CustomerStatus.ACTIVE, new BigDecimal("3200.00")),
            createCustomer("Example", "Contact5", "example.contact5@demo.com", "+1-555-0105", "Demo Systems", "654 Example Lane, Demo Heights", CustomerStatus.INACTIVE, new BigDecimal("0.00"))
        );

        customerRepository.saveAll(customers);
        logger.info("Successfully created {} customers", customers.size());
    }

    private void initializeSales() {
        logger.info("Initializing sales...");

        List<User> users = userRepository.findAll();
        List<Customer> customers = customerRepository.findAll();

        User salesRep1 = users.stream().filter(u -> u.getUserName().equals("sales_demo1")).findFirst().orElse(null);
        User salesRep2 = users.stream().filter(u -> u.getUserName().equals("sales_demo2")).findFirst().orElse(null);

        List<Sale> sales = Arrays.asList(
            createSale(new BigDecimal("500.00"), "Demo software license", SaleStatus.COMPLETED, customers.get(0), salesRep1),
            createSale(new BigDecimal("1250.50"), "Test implementation services", SaleStatus.COMPLETED, customers.get(1), salesRep1),
            createSale(new BigDecimal("890.25"), "Sample consultation package", SaleStatus.COMPLETED, customers.get(2), salesRep2),
            createSale(new BigDecimal("2200.00"), "Trial enterprise solution", SaleStatus.PENDING, customers.get(3), salesRep2),
            createSale(new BigDecimal("750.00"), "Example support contract", SaleStatus.CANCELED, customers.get(1), salesRep1)
        );

        saleRepository.saveAll(sales);
        logger.info("Successfully created {} sales", sales.size());
    }

    private void initializeCustomerInteractions() {
        logger.info("Initializing customer interactions...");

        List<User> users = userRepository.findAll();
        List<Customer> customers = customerRepository.findAll();

        User salesRep1 = users.stream().filter(u -> u.getUserName().equals("sales_demo1")).findFirst().orElse(null);
        User salesRep2 = users.stream().filter(u -> u.getUserName().equals("sales_demo2")).findFirst().orElse(null);
        User support = users.stream().filter(u -> u.getUserName().equals("support_demo")).findFirst().orElse(null);

        List<CustomerInteraction> interactions = Arrays.asList(
            createInteraction(InteractionType.CALL, "Initial demo call discussion", customers.get(0), salesRep1),
            createInteraction(InteractionType.EMAIL, "Follow-up test proposal sent", customers.get(1), salesRep1),
            createInteraction(InteractionType.MEETING, "Sample requirements gathering meeting", customers.get(2), salesRep2),
            createInteraction(InteractionType.SUPPORT_TICKET, "Trial technical support request", customers.get(3), support),
            createInteraction(InteractionType.EMAIL, "Example contract renewal reminder", customers.get(1), salesRep2)
        );

        customerInteractionRepository.saveAll(interactions);
        logger.info("Successfully created {} customer interactions", interactions.size());
    }

    private User createUser(String username, String email, String firstName, String lastName, UserRole role, UserStatus status) {
        User user = new User();
        user.setUserName(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("demo123")); // Default password for all test users
        user.setFirstname(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setStatus(status);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private Customer createCustomer(String firstName, String lastName, String email, String phone, String company, String address, CustomerStatus status, BigDecimal totalPurchaseValue) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhoneNumber(phone);
        customer.setCompany(company);
        customer.setAddress(address);
        customer.setStatus(status);
        customer.setTotalPurchaseValue(totalPurchaseValue);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        return customer;
    }

    private Sale createSale(BigDecimal amount, String description, SaleStatus status, Customer customer, User salesRep) {
        Sale sale = new Sale();
        sale.setAmount(amount);
        sale.setDescription(description);
        sale.setStatus(status);
        sale.setSaleDate(LocalDateTime.now().minusDays((long) (Math.random() * 30))); // Random date within last 30 days
        sale.setCustomer(customer);
        sale.setSalesRep(salesRep);
        sale.setCreatedAt(LocalDateTime.now());
        sale.setUpdatedAt(LocalDateTime.now());
        return sale;
    }

    private CustomerInteraction createInteraction(InteractionType type, String notes, Customer customer, User performedBy) {
        CustomerInteraction interaction = new CustomerInteraction();
        interaction.setType(type);
        interaction.setNotes(notes);
        interaction.setInteractionDate(LocalDateTime.now().minusDays((long) (Math.random() * 15))); // Random date within last 15 days
        interaction.setCustomer(customer);
        interaction.setPerformedBy(performedBy);
        interaction.setCreatedTime(LocalDateTime.now());
        interaction.setUpdateTime(LocalDateTime.now());
        return interaction;
    }
}
