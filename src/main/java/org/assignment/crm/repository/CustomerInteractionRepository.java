package org.assignment.crm.repository;

import org.assignment.crm.entity.CustomerInteraction;
import org.assignment.crm.enums.InteractionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerInteractionRepository extends JpaRepository<CustomerInteraction,Long> {
    List<CustomerInteraction> findCustomerInteractionByType(InteractionType type);

    List<CustomerInteraction> findCustomerInteractionByCustomer_Id(Long customerId);

    List<CustomerInteraction> findCustomerInteractionByPerformedBy_Id(long performedById);
}

