package org.assignment.crm.service;

import org.assignment.crm.entity.Customer;
import org.assignment.crm.entity.Sale;
import org.assignment.crm.entity.User;
import org.assignment.crm.enums.SaleStatus;
import org.assignment.crm.exception.CustomerNotFound;
import org.assignment.crm.exception.SaleNotFound;
import org.assignment.crm.exception.UserNotFound;
import org.assignment.crm.repository.CustomerRepository;
import org.assignment.crm.repository.SaleRepository;
import org.assignment.crm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SaleService {

    private static final Logger logger = LoggerFactory.getLogger(SaleService.class);

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    public Sale addSale(Sale sale) {
        logger.info("Creating new sale with amount: {}", sale.getAmount());
        try {
            sale.setCreatedAt(LocalDateTime.now());
            sale.setUpdatedAt(LocalDateTime.now());
            if(sale.getStatus() == null) {
                sale.setStatus(SaleStatus.COMPLETED);
                logger.debug("Set default status to COMPLETED for new sale");
            }

            Optional<Customer> customer = customerRepository.findById(sale.getCustomer().getId());
            if(customer.isEmpty()){
                logger.error("No customer found with ID: {}", sale.getCustomer().getId());
                throw new RuntimeException("Customer not found with ID: " + sale.getCustomer().getId());
            }
            else{
                BigDecimal amount = customer.get().getTotalPurchaseValue().add(sale.getAmount());
                customer.get().setTotalPurchaseValue(amount);
                sale.setCustomer(customer.get());
                logger.debug("Customer found and set: {} {}", customer.get().getFirstName(), customer.get().getLastName());
            }

            Optional<User> user = userRepository.findById(sale.getSalesRep().getId());
            if(user.isEmpty()){
                logger.error("No sales rep found with ID: {}", sale.getSalesRep().getId());
                throw new RuntimeException("Sales rep not found with ID: " + sale.getSalesRep().getId());
            }
            else{
                sale.setSalesRep(user.get());
                logger.debug("Sales rep found and set: {} {}", user.get().getFirstname(), user.get().getLastName());
            }

            Sale savedSale = this.saleRepository.save(sale);
            logger.info("Successfully created sale with ID: {} and amount: {}", savedSale.getId(), savedSale.getAmount());
            return savedSale;
        } catch (Exception e) {
            logger.error("Error creating sale with amount {}: {}", sale.getAmount(), e.getMessage(), e);
            throw e;
        }
    }


    @Transactional(readOnly = true)
    public List<Sale> getAllSales() {
        logger.info("Retrieving all sales");
        try {
            List<Sale> sales = this.saleRepository.findAll();
            logger.info("Successfully retrieved {} sales", sales.size());
            return sales;
        } catch (Exception e) {
            logger.error("Error retrieving all sales: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Sale getSaleById(long sale_id) {
        logger.info("Retrieving sale with ID: {}", sale_id);
        try {
            Sale sale = saleRepository.findById(sale_id)
                    .orElseThrow(() -> new SaleNotFound("Sale with this ID is not found!"));
            logger.info("Successfully retrieved sale with ID: {} and amount: {}", sale_id, sale.getAmount());
            return sale;
        } catch (SaleNotFound e) {
            logger.warn("Sale not found with ID: {}", sale_id);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving sale with ID {}: {}", sale_id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Sale> getSaleByRepId(long rep_id) {
        logger.info("Retrieving sales for sales representative ID: {}", rep_id);
        try {
            User sale_rep = userRepository.findById(rep_id)
                    .orElseThrow(() -> new UserNotFound("No sales Representative with this id"));

            List<Sale> sales = saleRepository.findSaleBySalesRep_Id(rep_id);
            logger.info("Successfully retrieved {} sales for sales rep ID: {}", sales.size(), rep_id);
            return sales;
        } catch (UserNotFound e) {
            logger.warn("Sales representative not found with ID: {}", rep_id);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving sales for rep ID {}: {}", rep_id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Sale> getSaleByCustomerId(long cust_id) {
        logger.info("Retrieving sales for customer ID: {}", cust_id);
        try {
            Customer customer = customerRepository.findById(cust_id)
                    .orElseThrow(() -> new CustomerNotFound("Customer not Found with this id"));

            List<Sale> sales = saleRepository.findSaleByCustomer_Id(cust_id);
            logger.info("Successfully retrieved {} sales for customer ID: {}", sales.size(), cust_id);
            return sales;
        } catch (CustomerNotFound e) {
            logger.warn("Customer not found with ID: {}", cust_id);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving sales for customer ID {}: {}", cust_id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public Sale updateSale(long sale_id, Sale updateData) {
        logger.info("Updating sale with ID: {}", sale_id);
        try {
            Sale existingSale = this.saleRepository.findById(sale_id)
                    .orElseThrow(() -> new SaleNotFound("Sale with ID " + sale_id + " not found"));

            if (updateData.getAmount() != null) {
                logger.debug("Updating sale amount from {} to {}", existingSale.getAmount(), updateData.getAmount());

                if (existingSale.getCustomer() == null || existingSale.getCustomer().getId() == null) {
                    throw new IllegalStateException("Sale must have a valid customer to update amount");
                }

                Long customerId = existingSale.getCustomer().getId();
                Customer customer = customerRepository.findById(customerId)
                        .orElseThrow(() -> new CustomerNotFound("Customer not found with id: " + customerId));

                BigDecimal oldAmount = existingSale.getAmount() != null ? existingSale.getAmount() : BigDecimal.ZERO;
                BigDecimal newAmount = updateData.getAmount();
                BigDecimal currentTotal = customer.getTotalPurchaseValue() != null ? customer.getTotalPurchaseValue() : BigDecimal.ZERO;

                BigDecimal newTotal = currentTotal.subtract(oldAmount).add(newAmount);

                customer.setTotalPurchaseValue(newTotal);
                customer.setUpdatedAt(LocalDateTime.now());
                customerRepository.save(customer);

                existingSale.setAmount(newAmount);

                logger.info("Updated customer {} total purchase value from {} to {}",
                        customerId, currentTotal, newTotal);
            }

            if (updateData.getStatus() != null) {
                logger.debug("Updating sale status from {} to {}", existingSale.getStatus(), updateData.getStatus());
                existingSale.setStatus(updateData.getStatus());
            }

            if (updateData.getCustomer() != null && updateData.getCustomer().getId() != null) {
                Long newCustomerId = updateData.getCustomer().getId();
                logger.debug("Updating sale customer to ID: {}", newCustomerId);

                Customer oldCustomer = existingSale.getCustomer();
                if (oldCustomer == null) {
                    throw new IllegalStateException("Sale must have an existing customer");
                }

                if (!oldCustomer.getId().equals(newCustomerId)) {
                    Customer newCustomer = customerRepository.findById(newCustomerId)
                            .orElseThrow(() -> new CustomerNotFound("Customer not found with id: " + newCustomerId));

                    BigDecimal saleAmount = existingSale.getAmount() != null ? existingSale.getAmount() : BigDecimal.ZERO;

                    BigDecimal oldCustomerTotal = oldCustomer.getTotalPurchaseValue() != null ? oldCustomer.getTotalPurchaseValue() : BigDecimal.ZERO;
                    oldCustomer.setTotalPurchaseValue(oldCustomerTotal.subtract(saleAmount));
                    oldCustomer.setUpdatedAt(LocalDateTime.now());
                    customerRepository.save(oldCustomer);

                    BigDecimal newCustomerTotal = newCustomer.getTotalPurchaseValue() != null ? newCustomer.getTotalPurchaseValue() : BigDecimal.ZERO;
                    newCustomer.setTotalPurchaseValue(newCustomerTotal.add(saleAmount));
                    newCustomer.setUpdatedAt(LocalDateTime.now());
                    customerRepository.save(newCustomer);

                    existingSale.setCustomer(newCustomer);

                    logger.info("Moved sale amount {} from customer {} to customer {}",
                            saleAmount, oldCustomer.getId(), newCustomer.getId());
                } else {
                    logger.debug("Customer ID is the same, no update needed");
                }
            }

            if (updateData.getSalesRep() != null) {
                User salesRep = userRepository.findById(updateData.getSalesRep().getId())
                        .orElseThrow(() -> new UserNotFound("Sales Representative not found with id: " + updateData.getSalesRep().getId()));
                logger.debug("Updating sale representative to ID: {}", salesRep.getId());
                existingSale.setSalesRep(salesRep);
            }


            if (updateData.getDescription() != null) {
                logger.debug("Updating sale description");
                existingSale.setDescription(updateData.getDescription());
            }

            if (updateData.getSaleDate() != null) {
                logger.debug("Updating sale date");
                existingSale.setSaleDate(updateData.getSaleDate());
            }

            existingSale.setUpdatedAt(LocalDateTime.now());
            Sale updatedSale = this.saleRepository.save(existingSale);

            logger.info("Successfully updated sale with ID: {}", sale_id);
            return updatedSale;

        } catch (SaleNotFound | CustomerNotFound | UserNotFound e) {
            logger.warn("Update failed for sale ID {}: {}", sale_id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error updating sale with ID {}: {}", sale_id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void deleteSale(long sale_id) {
        logger.info("Deleting sale with ID: {}", sale_id);
        try {
            Sale sale = this.saleRepository.findById(sale_id)
                    .orElseThrow(() -> new SaleNotFound("Sales with this ID is not found!"));

            Customer customer = sale.getCustomer();
            customer.setTotalPurchaseValue(customer.getTotalPurchaseValue().subtract(sale.getAmount()));
            customerRepository.save(customer);
            this.saleRepository.delete(sale);
            logger.info("Successfully deleted sale with ID: {}", sale_id);
        } catch (SaleNotFound e) {
            logger.warn("Cannot delete - sale not found with ID: {}", sale_id);
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting sale with ID {}: {}", sale_id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public Sale markSaleAsCompleted(long sale_id) {
        logger.info("Marking sale as completed for ID: {}", sale_id);
        return updateSaleStatusWithLogging(sale_id, SaleStatus.COMPLETED, "completed");
    }

    @Transactional
    public Sale markSaleAsCanceled(long sale_id) {
        logger.info("Marking sale as canceled for ID: {}", sale_id);
        return updateSaleStatusWithLogging(sale_id, SaleStatus.CANCELED, "canceled");
    }

    @Transactional
    public Sale markSaleAsPending(long sale_id) {
        logger.info("Marking sale as pending for ID: {}", sale_id);
        return updateSaleStatusWithLogging(sale_id, SaleStatus.PENDING, "pending");
    }

    @Transactional
    public Sale updateSaleStatus(long sale_id, SaleStatus newStatus) {
        logger.info("Updating sale status to {} for ID: {}", newStatus, sale_id);
        return updateSaleStatusWithLogging(sale_id, newStatus, newStatus.toString().toLowerCase());
    }

    private Sale updateSaleStatusWithLogging(long sale_id, SaleStatus newStatus, String statusDescription) {
        try {
            Sale sale = this.saleRepository.findById(sale_id)
                    .orElseThrow(() -> new SaleNotFound("Sales with this ID is not found!"));

            SaleStatus oldStatus = sale.getStatus();
            sale.setStatus(newStatus);
            sale.setUpdatedAt(LocalDateTime.now());

            Sale updatedSale = this.saleRepository.save(sale);
            logger.info("Successfully updated sale ID {} status from {} to {}", sale_id, oldStatus, newStatus);
            return updatedSale;
        } catch (SaleNotFound e) {
            logger.warn("Cannot update status - sale not found with ID: {}", sale_id);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating sale status to {} for ID {}: {}", statusDescription, sale_id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Sale> getSalesByStatus(SaleStatus status) {
        logger.info("Retrieving sales with status: {}", status);
        try {
            List<Sale> sales = this.saleRepository.findSaleByStatus(status);
            logger.info("Successfully retrieved {} sales with status: {}", sales.size(), status);
            return sales;
        } catch (Exception e) {
            logger.error("Error retrieving sales with status {}: {}", status, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Sale> getCompletedSales() {
        logger.info("Retrieving completed sales");
        try {
            List<Sale> sales = saleRepository.findSaleByStatus(SaleStatus.COMPLETED);

            if (sales.isEmpty()) {
                logger.warn("No completed sales found");
                throw new SaleNotFound("No completed sales found");
            }

            logger.info("Successfully retrieved {} completed sales", sales.size());
            return sales;
        } catch (SaleNotFound e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving completed sales: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Sale> getCanceledSales() {
        logger.info("Retrieving canceled sales");
        try {
            List<Sale> sales = saleRepository.findSaleByStatus(SaleStatus.CANCELED);

            if (sales.isEmpty()) {
                logger.warn("No canceled sales found");
                throw new SaleNotFound("No Canceled sales found");
            }

            logger.info("Successfully retrieved {} canceled sales", sales.size());
            return sales;
        } catch (SaleNotFound e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving canceled sales: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Sale> getSalesByRepId(long id){
        return this.saleRepository.getSalesBySalesRep_Id(id);
    }
}
