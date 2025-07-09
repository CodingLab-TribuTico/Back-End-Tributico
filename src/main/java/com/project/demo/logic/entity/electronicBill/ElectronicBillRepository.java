package com.project.demo.logic.entity.electronicBill;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ElectronicBillRepository extends JpaRepository<ElectronicBill, Long> {

    @Query("SELECT e FROM ElectronicBill e WHERE " +
            "LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.lastname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.identification) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<ElectronicBill> searchBills(@Param("search") String search, Pageable pageable);
}
