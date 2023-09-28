package com.tetiana.usermanager.repository;

import com.tetiana.usermanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getById(Long id);

    void deleteById(Long id);

    List<User> findByBirthDateBetween(LocalDate startDate, LocalDate endDate);
}
