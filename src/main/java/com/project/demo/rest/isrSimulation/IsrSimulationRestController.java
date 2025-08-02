package com.project.demo.rest.isrSimulation;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.invoice.Invoice;
import com.project.demo.logic.entity.invoice.InvoiceRepository;
import com.project.demo.logic.entity.isrSimulation.IsrSimulation;
import com.project.demo.logic.entity.isrSimulation.TaxIsrCalculationService;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/isr-simulation")
public class IsrSimulationRestController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaxIsrCalculationService taxIsrCalculationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> createSimulation(
            @RequestParam(defaultValue = "2024") int year,
            @RequestParam(defaultValue = "0") int childrenNumber,
            @RequestParam(defaultValue = "false") boolean hasSpouse,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest request
    ) {
        Optional<User> userOpt = userRepository.findById(userPrincipal.getId());
        if (userOpt.isEmpty()) {
            return new GlobalResponseHandler().handleResponse("Usuario no encontrado", null, HttpStatus.NOT_FOUND, request);
        }

        List<Invoice> invoices = invoiceRepository.findByYear(year, userPrincipal.getId());
        IsrSimulation sim = taxIsrCalculationService.simulate(userOpt.get(), invoices, year, childrenNumber, hasSpouse);

        return new GlobalResponseHandler().handleResponse("Simulación generada correctamente", sim, HttpStatus.OK, request);
    }
}
