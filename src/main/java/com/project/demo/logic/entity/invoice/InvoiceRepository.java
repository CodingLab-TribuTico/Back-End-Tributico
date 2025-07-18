package com.project.demo.logic.entity.invoice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT e FROM Invoice e WHERE " +
            "CAST(e.consecutive AS string) LIKE CONCAT('%', :search, '%') OR " +
            "CAST(e.invoiceKey AS string) LIKE CONCAT('%', :search, '%') OR " +
            "CAST(e.issueDate AS string) LIKE CONCAT('%', :search, '%')")
    Page<Invoice> seacrhInovices(@Param("search") String search, Pageable pageable);

}
