package com.project.demo.logic.entity.detailsBill;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DetailsBillRepository extends JpaRepository<DetailsBill, Long> {
    @Query("SELECT e FROM DetailsBill e WHERE " +
            "LOWER(e.detailDescription) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<DetailsBill> searchBillsDetails(@Param("search") String search, Pageable pageable);

}
