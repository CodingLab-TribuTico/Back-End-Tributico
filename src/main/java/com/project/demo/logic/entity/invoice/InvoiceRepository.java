package com.project.demo.logic.entity.invoice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT e FROM Invoice e WHERE " +
            "e.user.id = :userId AND (" +
            "LOWER(CAST(e.id AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(e.consecutive AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(CAST(e.consecutive AS string)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.user.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Invoice> searchElectronicBills(@Param("search") String search, @Param("userId") Long userId,
                                        Pageable pageable);

    Page<Invoice> findByUserId(Long userId, Pageable pageable);
}
