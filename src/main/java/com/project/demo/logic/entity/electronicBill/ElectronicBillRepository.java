package com.project.demo.logic.entity.electronicBill;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ElectronicBillRepository extends JpaRepository<ElectronicBill, Long> {

    @Query("SELECT e FROM ElectronicBill e WHERE " +
            "CAST(e.consecutive AS string) LIKE CONCAT('%', :search, '%') OR " +
            "CAST(e.code AS string) LIKE CONCAT('%', :search, '%') OR " +
            "CAST(e.issueDate AS string) LIKE CONCAT('%', :search, '%')")
    Page<ElectronicBill> searchElectronicBills(@Param("search") String search, Pageable pageable);

}
