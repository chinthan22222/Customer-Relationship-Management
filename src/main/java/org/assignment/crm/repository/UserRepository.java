package org.assignment.crm.repository;

import org.assignment.crm.entity.User;
import org.assignment.crm.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findUserByUserName(String userName);

    Optional<User> findUserByEmail(String Email);

    List<User> findUserByManager_Id(long managerId);

    List<User> findByRole(UserRole role);
}
