package com.project.demo.rest.ivaCalculation;

import com.project.demo.logic.entity.ivacalculation.IvaCalculation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IvaCalculationRepository extends JpaRepository<IvaCalculation, Long> {

    @Query("SELECT i FROM IvaCalculation i WHERE " +
            "CAST(i.year AS string) LIKE CONCAT('%', :search, '%') OR " +
            "CAST(i.month AS string) LIKE CONCAT('%', :search, '%') OR " +
            "LOWER(i.user.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.user.identification) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<IvaCalculation> searchIvaSimulations(@Param("search") String search, Pageable pageable);
}
