package org.assignment.crm.repository;

import org.assignment.crm.entity.Sale;
import org.assignment.crm.enums.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale,Long> {
    List<Sale> findSaleBySalesRep_Id(long salesRepId);

    List<Sale> findSaleByCustomer_Id(long customerId);

    List<Sale> findSaleByStatus(SaleStatus status);
}

