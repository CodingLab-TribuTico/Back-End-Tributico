package com.project.demo.rest.ivaCalculation;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.ivacalculation.IvaCalculation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iva-simulation")
public class IvaCalculationController {
    @Autowired
    IvaCalculationService ivaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> createIvaSimulation(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam Long userId,
            HttpServletRequest request
    ) {
       IvaCalculation result = ivaService.createIvaSimulation(year, month, userId);
        return new GlobalResponseHandler().handleResponse(
                "Simulación de IVA creada exitosamente",
                result,
                HttpStatus.CREATED,
                request
        );

    }
}
