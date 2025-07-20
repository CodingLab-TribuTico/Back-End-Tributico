package com.project.demo.logic.entity.invoice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT e FROM Invoice e WHERE " +
            "CAST(e.consecutive AS string) LIKE CONCAT('%', :search, '%') OR " +
            "CAST(e.invoiceKey AS string) LIKE CONCAT('%', :search, '%') OR " +
            "CAST(e.issueDate AS string) LIKE CONCAT('%', :search, '%')")
    Page<Invoice> seacrhInovices(@Param("search") String search, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE YEAR(i.issueDate) = :year AND i.user.id = :userId")
    List<Invoice> findByYear(int year, Long userId);

    @Query("SELECT i FROM Invoice i WHERE i.user.id = :userId")
    List<Invoice> findByUserId(@Param("userId") Long userId);
}
