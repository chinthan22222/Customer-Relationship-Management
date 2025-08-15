package org.assignment.crm.config;

import org.assignment.crm.entity.Customer;
import org.assignment.crm.entity.CustomerInteraction;
import org.assignment.crm.entity.Sale;
import org.assignment.crm.entity.User;
import org.assignment.crm.enums.*;
import org.assignment.crm.service.UserService;
import org.assignment.crm.repository.CustomerRepository;
import org.assignment.crm.repository.SaleRepository;
import org.assignment.crm.repository.CustomerInteractionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private CustomerInteractionRepository customerInteractionRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing test data...");

        initializeUsers();

        initializeCustomers();

        initializeSales();

        initializeCustomerInteractions();

        logger.info("Data initialization completed successfully!");
    }

    private void initializeUsers() {
        logger.info("Initializing users...");

        if (userService.findByUserName("admintest").isEmpty()) {
            User admin = new User();
            admin.setUserName("admintest");
            admin.setPassword("admin123");
            admin.setFirstname("Test");
            admin.setLastName("Admin");
            admin.setEmail("admintest@crm.com");
            admin.setRole(UserRole.ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            userService.createUser(admin);
            logger.info("Created admin user: admintest");
        }

        if (userService.findByUserName("managertest").isEmpty()) {
            User manager = new User();
            manager.setUserName("managertest");
            manager.setPassword("manager123");
            manager.setFirstname("Test");
            manager.setLastName("Manager");
            manager.setEmail("managertest@crm.com");
            manager.setRole(UserRole.MANAGER);
            manager.setStatus(UserStatus.ACTIVE);
            userService.createUser(manager);
            logger.info("Created manager user: managertest");
        }

        if (userService.findByUserName("salestest1").isEmpty()) {
            User salesRep1 = new User();
            salesRep1.setUserName("salestest1");
            salesRep1.setPassword("sales123");
            salesRep1.setFirstname("Test");
            salesRep1.setLastName("SalesRep1");
            salesRep1.setEmail("salestest1@crm.com");
            salesRep1.setRole(UserRole.SALES_REP);
            salesRep1.setStatus(UserStatus.ACTIVE);
            userService.createUser(salesRep1);
            logger.info("Created sales rep user: salestest1");
        }

        if (userService.findByUserName("salestest2").isEmpty()) {
            User salesRep2 = new User();
            salesRep2.setUserName("salestest2");
            salesRep2.setPassword("sales123");
            salesRep2.setFirstname("Test");
            salesRep2.setLastName("SalesRep2");
            salesRep2.setEmail("salestest2@crm.com");
            salesRep2.setRole(UserRole.SALES_REP);
            salesRep2.setStatus(UserStatus.ACTIVE);
            userService.createUser(salesRep2);
            logger.info("Created sales rep user: salestest2");
        }

        if (userService.findByUserName("supporttest").isEmpty()) {
            User support = new User();
            support.setUserName("supporttest");
            support.setPassword("support123");
            support.setFirstname("Test");
            support.setLastName("Support");
            support.setEmail("supporttest@crm.com");
            support.setRole(UserRole.SUPPORT);
            support.setStatus(UserStatus.ACTIVE);
            userService.createUser(support);
            logger.info("Created support user: supporttest");
        }
    }

    private void initializeCustomers() {
        logger.info("Initializing customers...");

        if (customerRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();

            Customer customer1 = new Customer();
            customer1.setFirstName("TestCustomer");
            customer1.setLastName("One");
            customer1.setEmail("testcustomer1@example.com");
            customer1.setPhoneNumber("+1-555-0101");
            customer1.setCompany("TestCompany1");
            customer1.setAddress("123 Test Street, Test City, TC 12345");
            customer1.setStatus(CustomerStatus.ACTIVE);
            customer1.setTotalPurchaseValue(BigDecimal.ZERO);
            customer1.setCreatedAt(now);
            customer1.setUpdatedAt(now);
            customerRepository.save(customer1);

            Customer customer2 = new Customer();
            customer2.setFirstName("TestCustomer");
            customer2.setLastName("Two");
            customer2.setEmail("testcustomer2@example.com");
            customer2.setPhoneNumber("+1-555-0102");
            customer2.setCompany("TestCompany2");
            customer2.setAddress("456 Test Avenue, Test City, TC 12346");
            customer2.setStatus(CustomerStatus.ACTIVE);
            customer2.setTotalPurchaseValue(BigDecimal.ZERO);
            customer2.setCreatedAt(now);
            customer2.setUpdatedAt(now);
            customerRepository.save(customer2);

            Customer customer3 = new Customer();
            customer3.setFirstName("TestCustomer");
            customer3.setLastName("Three");
            customer3.setEmail("testcustomer3@example.com");
            customer3.setPhoneNumber("+1-555-0103");
            customer3.setCompany("TestCompany3");
            customer3.setAddress("789 Test Boulevard, Test City, TC 12347");
            customer3.setStatus(CustomerStatus.ACTIVE);
            customer3.setTotalPurchaseValue(BigDecimal.ZERO);
            customer3.setCreatedAt(now);
            customer3.setUpdatedAt(now);
            customerRepository.save(customer3);

            Customer customer4 = new Customer();
            customer4.setFirstName("TestCustomer");
            customer4.setLastName("Four");
            customer4.setEmail("testcustomer4@example.com");
            customer4.setPhoneNumber("+1-555-0104");
            customer4.setCompany("TestCompany4");
            customer4.setAddress("321 Test Lane, Test City, TC 12348");
            customer4.setStatus(CustomerStatus.ACTIVE);
            customer4.setTotalPurchaseValue(BigDecimal.ZERO);
            customer4.setCreatedAt(now);
            customer4.setUpdatedAt(now);
            customerRepository.save(customer4);

            Customer customer5 = new Customer();
            customer5.setFirstName("TestCustomer");
            customer5.setLastName("Five");
            customer5.setEmail("testcustomer5@example.com");
            customer5.setPhoneNumber("+1-555-0105");
            customer5.setCompany("TestCompany5");
            customer5.setAddress("654 Test Drive, Test City, TC 12349");
            customer5.setStatus(CustomerStatus.ACTIVE);
            customer5.setTotalPurchaseValue(BigDecimal.ZERO);
            customer5.setCreatedAt(now);
            customer5.setUpdatedAt(now);
            customerRepository.save(customer5);

            logger.info("Created 5 test customers");
        } else {
            logger.info("Customers already exist, skipping initialization");
        }
    }

    private void initializeSales() {
        logger.info("Initializing sales...");

        if (saleRepository.count() == 0) {
            List<Customer> customers = customerRepository.findAll();
            List<User> salesReps = userService.findByRole(UserRole.SALES_REP);

            if (!customers.isEmpty() && !salesReps.isEmpty()) {
                LocalDateTime now = LocalDateTime.now();

                Sale sale1 = new Sale();
                sale1.setAmount(new BigDecimal("1500.00"));
                sale1.setSaleDate(now.minusDays(10));
                sale1.setStatus(SaleStatus.COMPLETED);
                sale1.setDescription("Test Sale 1 - Software License");
                sale1.setCustomer(customers.get(0));
                sale1.setSalesRep(salesReps.get(0));
                sale1.setCreatedAt(now);
                sale1.setUpdatedAt(now);
                saleRepository.save(sale1);

                Sale sale2 = new Sale();
                sale2.setAmount(new BigDecimal("2750.50"));
                sale2.setSaleDate(now.minusDays(8));
                sale2.setStatus(SaleStatus.COMPLETED);
                sale2.setDescription("Test Sale 2 - Hardware Equipment");
                sale2.setCustomer(customers.get(1));
                sale2.setSalesRep(salesReps.size() > 1 ? salesReps.get(1) : salesReps.get(0));
                sale2.setCreatedAt(now);
                sale2.setUpdatedAt(now);
                saleRepository.save(sale2);

                Sale sale3 = new Sale();
                sale3.setAmount(new BigDecimal("950.25"));
                sale3.setSaleDate(now.minusDays(5));
                sale3.setStatus(SaleStatus.PENDING);
                sale3.setDescription("Test Sale 3 - Consulting Services");
                sale3.setCustomer(customers.get(2));
                sale3.setSalesRep(salesReps.get(0));
                sale3.setCreatedAt(now);
                sale3.setUpdatedAt(now);
                saleRepository.save(sale3);

                Sale sale4 = new Sale();
                sale4.setAmount(new BigDecimal("3200.75"));
                sale4.setSaleDate(now.minusDays(3));
                sale4.setStatus(SaleStatus.COMPLETED);
                sale4.setDescription("Test Sale 4 - Annual Subscription");
                sale4.setCustomer(customers.get(3));
                sale4.setSalesRep(salesReps.size() > 1 ? salesReps.get(1) : salesReps.get(0));
                sale4.setCreatedAt(now);
                sale4.setUpdatedAt(now);
                saleRepository.save(sale4);

                Sale sale5 = new Sale();
                sale5.setAmount(new BigDecimal("1800.00"));
                sale5.setSaleDate(now.minusDays(1));
                sale5.setStatus(SaleStatus.CANCELED);
                sale5.setDescription("Test Sale 5 - Training Package");
                sale5.setCustomer(customers.get(4));
                sale5.setSalesRep(salesReps.get(0));
                sale5.setCreatedAt(now);
                sale5.setUpdatedAt(now);
                saleRepository.save(sale5);

                updateCustomerTotals();

                logger.info("Created 5 test sales");
            } else {
                logger.warn("Cannot create sales - customers or sales reps not found");
            }
        } else {
            logger.info("Sales already exist, skipping initialization");
        }
    }

    private void initializeCustomerInteractions() {
        logger.info("Initializing customer interactions...");

        if (customerInteractionRepository.count() == 0) {
            List<Customer> customers = customerRepository.findAll();
            List<User> users = userService.findAll();

            if (!customers.isEmpty() && !users.isEmpty()) {
                LocalDateTime now = LocalDateTime.now();

                CustomerInteraction interaction1 = new CustomerInteraction();
                interaction1.setType(InteractionType.CALL);
                interaction1.setInteractionDate(now.minusDays(15));
                interaction1.setNotes("Test interaction 1 - Initial contact call to discuss requirements");
                interaction1.setCustomer(customers.get(0));
                interaction1.setPerformedBy(users.get(0));
                interaction1.setCreatedTime(now);
                interaction1.setUpdateTime(now);
                customerInteractionRepository.save(interaction1);

                CustomerInteraction interaction2 = new CustomerInteraction();
                interaction2.setType(InteractionType.EMAIL);
                interaction2.setInteractionDate(now.minusDays(12));
                interaction2.setNotes("Test interaction 2 - Follow-up email with proposal and pricing");
                interaction2.setCustomer(customers.get(1));
                interaction2.setPerformedBy(users.get(1));
                interaction2.setCreatedTime(now);
                interaction2.setUpdateTime(now);
                customerInteractionRepository.save(interaction2);

                CustomerInteraction interaction3 = new CustomerInteraction();
                interaction3.setType(InteractionType.MEETING);
                interaction3.setInteractionDate(now.minusDays(7));
                interaction3.setNotes("Test interaction 3 - In-person meeting to demonstrate product features");
                interaction3.setCustomer(customers.get(2));
                interaction3.setPerformedBy(users.get(2));
                interaction3.setCreatedTime(now);
                interaction3.setUpdateTime(now);
                customerInteractionRepository.save(interaction3);

                CustomerInteraction interaction4 = new CustomerInteraction();
                interaction4.setType(InteractionType.SUPPORT_TICKET);
                interaction4.setInteractionDate(now.minusDays(4));
                interaction4.setNotes("Test interaction 4 - Technical support session for implementation");
                interaction4.setCustomer(customers.get(3));
                interaction4.setPerformedBy(users.get(3));
                interaction4.setCreatedTime(now);
                interaction4.setUpdateTime(now);
                customerInteractionRepository.save(interaction4);

                CustomerInteraction interaction5 = new CustomerInteraction();
                interaction5.setType(InteractionType.CALL);
                interaction5.setInteractionDate(now.minusDays(2));
                interaction5.setNotes("Test interaction 5 - Customer satisfaction follow-up call");
                interaction5.setCustomer(customers.get(4));
                interaction5.setPerformedBy(users.get(4));
                interaction5.setCreatedTime(now);
                interaction5.setUpdateTime(now);
                customerInteractionRepository.save(interaction5);

                logger.info("Created 5 test customer interactions");
            } else {
                logger.warn("Cannot create interactions - customers or users not found");
            }
        } else {
            logger.info("Customer interactions already exist, skipping initialization");
        }
    }

    private void updateCustomerTotals() {
        List<Sale> completedSales = saleRepository.findSaleByStatus(SaleStatus.COMPLETED);
        for (Sale sale : completedSales) {
            Customer customer = sale.getCustomer();
            BigDecimal currentTotal = customer.getTotalPurchaseValue() != null
                    ? customer.getTotalPurchaseValue()
                    : BigDecimal.ZERO;
            customer.setTotalPurchaseValue(currentTotal.add(sale.getAmount()));
            customer.setUpdatedAt(LocalDateTime.now());
            customerRepository.save(customer);
        }
        logger.info("Updated customer total purchase values");
    }
}
