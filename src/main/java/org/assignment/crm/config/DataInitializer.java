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

    @Autowired private UserRepository              userRepository;
    @Autowired private CustomerRepository          customerRepository;
    @Autowired private SaleRepository              saleRepository;
    @Autowired private CustomerInteractionRepository customerInteractionRepository;
    @Autowired private PasswordEncoder             passwordEncoder;

    @Override
    public void run(String... args) {
        logger.info("Starting data initialization…");

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

        logger.info("Initializing users…");

        User admin   = createUser("admin_test",   "admin.test@company.com",   "System", "Admin",   UserRole.ADMIN,   UserStatus.ACTIVE);
        User manager = createUser("manager_test", "manager.test@company.com", "Team", "Manager", UserRole.MANAGER, UserStatus.ACTIVE);

        User rep1    = createUser("sales_demo1",  "sales1@company.com",  "Chinthan", "Sales",  UserRole.SALES_REP, UserStatus.ACTIVE);
        User rep2    = createUser("sales_demo2",  "sales2@company.com",  "Regional", "Rep",  UserRole.SALES_REP, UserStatus.ACTIVE);
        User support = createUser("support_demo", "support@company.com", "Customer", "Support", UserRole.SUPPORT,  UserStatus.INACTIVE);

        rep1.setManager(manager);
        rep2.setManager(manager);
        support.setManager(manager);

        userRepository.saveAll(Arrays.asList(admin, manager, rep1, rep2, support));

        logger.info("Successfully created 5 users with manager hierarchy.");
    }

    private void initializeCustomers() {

        logger.info("Initializing customers…");

        List<Customer> customers = Arrays.asList(
                createCustomer("Rajesh",    "Kumar", "rajesh.kumar@techcorp.in", "+91-9876543210", "TechCorp Solutions",
                        "A-101, Cyber City, Gurgaon, Haryana 122001",   CustomerStatus.ACTIVE,   new BigDecimal("125000.00")),
                createCustomer("Priya",    "Sharma",   "priya.sharma@innovate.in",    "+91-9823456789", "InnovateTech Industries",
                        "Plot 45, Hinjewadi Phase 2, Pune, Maharashtra 411057",  CustomerStatus.ACTIVE,   new BigDecimal("275000.50")),
                createCustomer("Arjun",  "Patel",     "arjun.patel@digitalsoln.in",    "+91-9765432108", "Digital Solutions Pvt Ltd",
                        "15th Floor, Brigade Road, Bangalore, Karnataka 560001", CustomerStatus.ACTIVE,   new BigDecimal("89000.25")),
                createCustomer("Anita",   "Singh",  "anita.singh@enterprise.in",  "+91-9654321098", "Enterprise Systems Ltd",
                        "Tower B, Sector 18, Noida, Uttar Pradesh 201301",  CustomerStatus.ACTIVE,   new BigDecimal("320000.00")),
                createCustomer("Vikram", "Reddy",  "vikram.reddy@techsys.in","+91-9543210987", "TechSys Corporation",
                        "IT Park, OMR Road, Chennai, Tamil Nadu 600096",  CustomerStatus.INACTIVE, new BigDecimal("0.00"))
        );

        customerRepository.saveAll(customers);
        logger.info("Successfully created {} customers", customers.size());
    }

    private void initializeSales() {

        logger.info("Initializing sales…");

        User  rep1 = userRepository.findUserByUserName("sales_demo1").orElseThrow();
        User  rep2 = userRepository.findUserByUserName("sales_demo2").orElseThrow();
        List<Customer> customers = customerRepository.findAll();

        List<Sale> sales = Arrays.asList(
                createSale(new BigDecimal("50000.00"),  "CRM Software License",        SaleStatus.COMPLETED, customers.get(0), rep1),
                createSale(new BigDecimal("125000.50"), "ERP Implementation Services", SaleStatus.COMPLETED, customers.get(1), rep1),
                createSale(new BigDecimal("89000.25"),  "Digital Transformation Package",  SaleStatus.COMPLETED, customers.get(2), rep2),
                createSale(new BigDecimal("220000.00"), "Enterprise Cloud Solution",     SaleStatus.PENDING,   customers.get(3), rep2),
                createSale(new BigDecimal("75000.00"),  "Annual Maintenance Contract",      SaleStatus.CANCELED,  customers.get(1), rep1)
        );

        saleRepository.saveAll(sales);
        logger.info("Successfully created {} sales", sales.size());
    }

    private void initializeCustomerInteractions() {

        logger.info("Initializing customer interactions…");

        User  rep1   = userRepository.findUserByUserName("sales_demo1").orElseThrow();
        User  rep2   = userRepository.findUserByUserName("sales_demo2").orElseThrow();
        User  support= userRepository.findUserByUserName("support_demo").orElseThrow();
        List<Customer> customers = customerRepository.findAll();

        List<CustomerInteraction> interactions = Arrays.asList(
                createInteraction(InteractionType.CALL,          "Initial product demo and requirement discussion",      customers.get(0), rep1),
                createInteraction(InteractionType.EMAIL,         "Sent detailed proposal with pricing structure",      customers.get(1), rep1),
                createInteraction(InteractionType.MEETING,       "Requirements gathering and solution architecture",    customers.get(2), rep2),
                createInteraction(InteractionType.SUPPORT_TICKET,"Technical support for implementation queries",         customers.get(3), support),
                createInteraction(InteractionType.EMAIL,         "Contract renewal discussion and terms",         customers.get(1), rep2)
        );

        customerInteractionRepository.saveAll(interactions);
        logger.info("Successfully created {} customer interactions", interactions.size());
    }

    private User createUser(String username, String email, String firstName, String lastName,
                            UserRole role, UserStatus status) {

        User u = new User();
        u.setUserName(username);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode("demo123"));
        u.setFirstname(firstName);
        u.setLastName(lastName);
        u.setRole(role);
        u.setStatus(status);
        u.setCreatedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());
        return u;
    }

    private Customer createCustomer(String firstName, String lastName, String email, String phone,
                                    String company, String address, CustomerStatus status,
                                    BigDecimal totalPurchaseValue) {

        Customer c = new Customer();
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setEmail(email);
        c.setPhoneNumber(phone);
        c.setCompany(company);
        c.setAddress(address);
        c.setStatus(status);
        c.setTotalPurchaseValue(totalPurchaseValue);
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        return c;
    }

    private Sale createSale(BigDecimal amount, String description, SaleStatus status,
                            Customer customer, User salesRep) {

        Sale s = new Sale();
        s.setAmount(amount);
        s.setDescription(description);
        s.setStatus(status);
        s.setSaleDate(LocalDateTime.now().minusDays((long)(Math.random()*30)));
        s.setCustomer(customer);
        s.setSalesRep(salesRep);
        s.setCreatedAt(LocalDateTime.now());
        s.setUpdatedAt(LocalDateTime.now());
        return s;
    }

    private CustomerInteraction createInteraction(InteractionType type, String notes,
                                                  Customer customer, User performedBy) {

        CustomerInteraction ci = new CustomerInteraction();
        ci.setType(type);
        ci.setNotes(notes);
        ci.setInteractionDate(LocalDateTime.now().minusDays((long)(Math.random()*15)));
        ci.setCustomer(customer);
        ci.setPerformedBy(performedBy);
        ci.setCreatedTime(LocalDateTime.now());
        ci.setUpdateTime(LocalDateTime.now());
        return ci;
    }
}
