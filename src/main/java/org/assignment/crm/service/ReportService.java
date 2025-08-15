package org.assignment.crm.service;

import org.assignment.crm.entity.Customer;
import org.assignment.crm.entity.CustomerInteraction;
import org.assignment.crm.entity.Sale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SaleService saleService;

    @Autowired
    private CustomerInteractionService interactionService;

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardReport() {
        logger.info("Generating dashboard report");
        try {
            Map<String, Object> dashboard = new HashMap<>();

            List<Customer> customers = customerService.findAll();
            List<Sale> sales = saleService.getAllSales();
            List<CustomerInteraction> interactions = interactionService.getAllCustomerInteractions();

            logger.debug("Retrieved data for dashboard: {} customers, {} sales, {} interactions",
                    customers.size(), sales.size(), interactions.size());

            dashboard.put("totalCustomers", customers.size());
            dashboard.put("totalSales", sales.size());
            dashboard.put("totalInteractions", interactions.size());

            BigDecimal totalRevenue = calculateTotalRevenue(sales);
            dashboard.put("totalRevenue", totalRevenue);
            logger.debug("Calculated total revenue: {}", totalRevenue);

            if (!sales.isEmpty()) {
                BigDecimal averageSale = totalRevenue.divide(BigDecimal.valueOf(sales.size()), 2, BigDecimal.ROUND_HALF_UP);
                dashboard.put("averageSaleValue", averageSale);
                logger.debug("Calculated average sale value: {}", averageSale);
            } else {
                logger.debug("No sales found, skipping average calculation");
            }

            List<Sale> recentSales = getRecentSales(sales, 5);
            dashboard.put("recentSales", recentSales);
            logger.debug("Retrieved {} recent sales", recentSales.size());

            long activeCustomers = getActiveCustomers(customers);
            dashboard.put("activeCustomers", activeCustomers);
            logger.debug("Found {} active customers", activeCustomers);

            logger.info("Successfully generated dashboard report with {} total customers and revenue of {}",
                    customers.size(), totalRevenue);
            return dashboard;

        } catch (Exception e) {
            logger.error("Error generating dashboard report: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCustomerActivityReport(Long customerId) {
        logger.info("Generating customer activity report for customer ID: {}", customerId);
        try {
            Customer customer = customerService.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            logger.debug("Found customer: {} (email: {}) for activity report",
                    customer.getFirstName() + " " + customer.getLastName(), customer.getEmail());

            Map<String, Object> report = new HashMap<>();
            report.put("customer", customer);

            List<Sale> customerSales = saleService.getSaleByCustomerId(customerId);
            report.put("totalSales", customerSales.size());

            BigDecimal customerRevenue = calculateTotalRevenue(customerSales);
            report.put("totalRevenue", customerRevenue);
            report.put("salesHistory", customerSales);

            logger.debug("Customer {} has {} sales with total revenue: {}",
                    customerId, customerSales.size(), customerRevenue);

            List<CustomerInteraction> interactions = interactionService.getInteractionsByCustomerId(customerId);
            report.put("totalInteractions", interactions.size());

            Map<String, Long> interactionsByType = groupInteractionsByType(interactions);
            report.put("interactionsByType", interactionsByType);

            List<CustomerInteraction> recentInteractions = getRecentInteractions(interactions, 5);
            report.put("recentInteractions", recentInteractions);

            logger.debug("Customer {} has {} interactions grouped by type: {}",
                    customerId, interactions.size(), interactionsByType);

            LocalDateTime lastActivity = getLastActivityDate(customerSales, interactions);
            report.put("lastActivity", lastActivity);

            String customerValue = calculateCustomerValue(customerSales, interactions);
            report.put("customerValue", customerValue);

            logger.info("Successfully generated activity report for customer ID: {} with value: {} and last activity: {}",
                    customerId, customerValue, lastActivity);
            return report;

        } catch (RuntimeException e) {
            logger.warn("Customer not found for activity report: {}", customerId);
            throw e;
        } catch (Exception e) {
            logger.error("Error generating customer activity report for ID {}: {}", customerId, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSalesTrendsReport() {
        logger.info("Generating sales trends report");
        try {
            List<Sale> sales = saleService.getAllSales();
            Map<String, Object> trends = new HashMap<>();

            logger.debug("Analyzing trends for {} sales", sales.size());

            trends.put("totalSales", sales.size());

            BigDecimal totalRevenue = calculateTotalRevenue(sales);
            trends.put("totalRevenue", totalRevenue);

            Map<String, Integer> salesByMonth = groupSalesByMonth(sales);
            trends.put("salesByMonth", salesByMonth);
            logger.debug("Sales distribution by month: {}", salesByMonth);

            List<Sale> topSales = getTopSales(sales, 10);
            trends.put("topPerformingSales", topSales);
            logger.debug("Retrieved {} top performing sales", topSales.size());

            logger.info("Successfully generated sales trends report with total revenue: {} across {} months",
                    totalRevenue, salesByMonth.size());
            return trends;

        } catch (Exception e) {
            logger.error("Error generating sales trends report: {}", e.getMessage(), e);
            throw e;
        }
    }

    private BigDecimal calculateTotalRevenue(List<Sale> sales) {
        logger.debug("Calculating total revenue for {} sales", sales.size());
        try {
            BigDecimal revenue = sales.stream()
                    .map(Sale::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            logger.debug("Total revenue calculated: {}", revenue);
            return revenue;
        } catch (Exception e) {
            logger.error("Error calculating total revenue: {}", e.getMessage(), e);
            throw e;
        }
    }

    private List<Sale> getRecentSales(List<Sale> sales, int limit) {
        logger.debug("Getting {} most recent sales from {} total sales", limit, sales.size());
        try {
            List<Sale> recentSales = sales.stream()
                    .sorted((a, b) -> b.getSaleDate().compareTo(a.getSaleDate()))
                    .limit(limit)
                    .collect(Collectors.toList());
            logger.debug("Retrieved {} recent sales", recentSales.size());
            return recentSales;
        } catch (Exception e) {
            logger.error("Error getting recent sales: {}", e.getMessage(), e);
            throw e;
        }
    }

    private long getActiveCustomers(List<Customer> customers) {
        logger.debug("Calculating active customers from {} total customers", customers.size());
        try {
            LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
            long activeCount = customers.stream()
                    .filter(customer -> hasRecentActivity(customer, threeMonthsAgo))
                    .count();
            logger.debug("Found {} active customers (with activity since {})", activeCount, threeMonthsAgo);
            return activeCount;
        } catch (Exception e) {
            logger.error("Error calculating active customers: {}", e.getMessage(), e);
            throw e;
        }
    }

    private boolean hasRecentActivity(Customer customer, LocalDateTime since) {
        try {
            List<Sale> recentSales = saleService.getSaleByCustomerId(customer.getId())
                    .stream()
                    .filter(sale -> sale.getSaleDate().isAfter(since))
                    .collect(Collectors.toList());
            boolean hasActivity = !recentSales.isEmpty();
            logger.debug("Customer {} has recent activity: {} ({} recent sales)",
                    customer.getId(), hasActivity, recentSales.size());
            return hasActivity;
        } catch (Exception e) {
            logger.error("Error checking recent activity for customer {}: {}", customer.getId(), e.getMessage(), e);
            return false;
        }
    }

    private Map<String, Long> groupInteractionsByType(List<CustomerInteraction> interactions) {
        logger.debug("Grouping {} interactions by type", interactions.size());
        try {
            Map<String, Long> groupedInteractions = interactions.stream()
                    .collect(Collectors.groupingBy(
                            interaction -> interaction.getType().toString(),
                            Collectors.counting()
                    ));
            logger.debug("Interaction groups: {}", groupedInteractions);
            return groupedInteractions;
        } catch (Exception e) {
            logger.error("Error grouping interactions by type: {}", e.getMessage(), e);
            throw e;
        }
    }

    private List<CustomerInteraction> getRecentInteractions(List<CustomerInteraction> interactions, int limit) {
        logger.debug("Getting {} most recent interactions from {} total interactions", limit, interactions.size());
        try {
            List<CustomerInteraction> recentInteractions = interactions.stream()
                    .sorted((a, b) -> b.getInteractionDate().compareTo(a.getInteractionDate()))
                    .limit(limit)
                    .collect(Collectors.toList());
            logger.debug("Retrieved {} recent interactions", recentInteractions.size());
            return recentInteractions;
        } catch (Exception e) {
            logger.error("Error getting recent interactions: {}", e.getMessage(), e);
            throw e;
        }
    }

    private LocalDateTime getLastActivityDate(List<Sale> sales, List<CustomerInteraction> interactions) {
        logger.debug("Calculating last activity date from {} sales and {} interactions",
                sales.size(), interactions.size());
        try {
            LocalDateTime lastSale = sales.stream()
                    .map(Sale::getSaleDate)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.MIN);

            LocalDateTime lastInteraction = interactions.stream()
                    .map(CustomerInteraction::getInteractionDate)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.MIN);

            LocalDateTime lastActivity = lastSale.isAfter(lastInteraction) ? lastSale : lastInteraction;
            logger.debug("Last activity date: {} (last sale: {}, last interaction: {})",
                    lastActivity, lastSale, lastInteraction);
            return lastActivity;
        } catch (Exception e) {
            logger.error("Error calculating last activity date: {}", e.getMessage(), e);
            throw e;
        }
    }

    private String calculateCustomerValue(List<Sale> sales, List<CustomerInteraction> interactions) {
        logger.debug("Calculating customer value from {} sales and {} interactions",
                sales.size(), interactions.size());
        try {
            BigDecimal revenue = calculateTotalRevenue(sales);
            int engagementScore = interactions.size();

            String value;
            if (revenue.compareTo(BigDecimal.valueOf(10000)) > 0 && engagementScore > 5) {
                value = "HIGH_VALUE";
            } else if (revenue.compareTo(BigDecimal.valueOf(5000)) > 0 || engagementScore > 3) {
                value = "MEDIUM_VALUE";
            } else {
                value = "LOW_VALUE";
            }

            logger.debug("Customer value calculated: {} (revenue: {}, engagement: {})",
                    value, revenue, engagementScore);
            return value;
        } catch (Exception e) {
            logger.error("Error calculating customer value: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Map<String, Integer> groupSalesByMonth(List<Sale> sales) {
        logger.debug("Grouping {} sales by month", sales.size());
        try {
            Map<String, Integer> salesByMonth = sales.stream()
                    .collect(Collectors.groupingBy(
                            sale -> sale.getSaleDate().getMonth().toString(),
                            Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                    ));
            logger.debug("Sales by month distribution: {}", salesByMonth);
            return salesByMonth;
        } catch (Exception e) {
            logger.error("Error grouping sales by month: {}", e.getMessage(), e);
            throw e;
        }
    }

    private List<Sale> getTopSales(List<Sale> sales, int limit) {
        logger.debug("Getting top {} sales from {} total sales", limit, sales.size());
        try {
            List<Sale> topSales = sales.stream()
                    .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
                    .limit(limit)
                    .collect(Collectors.toList());
            logger.debug("Retrieved {} top sales", topSales.size());
            return topSales;
        } catch (Exception e) {
            logger.error("Error getting top sales: {}", e.getMessage(), e);
            throw e;
        }
    }
}
