package com.project.demo.rest.ivaCalculation;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.ivacalculation.IvaCalculation;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/iva-calculation")


public class IvaCalculationController {
    @Autowired private IvaCalculationService ivaCalculationService;

    @Autowired private UserRepository userRepository;
    @Autowired
    private com.project.demo.rest.ivaCalculation.IvaCalculationRepository ivaCalculationRepository;


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<IvaCalculation> ivaCalculationPage;

        if (search == null || search.trim().isEmpty()) {
            ivaCalculationPage = ivaCalculationRepository.findAll(pageable);
        } else {
            ivaCalculationPage = ivaCalculationRepository.searchIvaSimulations(search.trim(), pageable);
        }

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(ivaCalculationPage.getTotalPages());
        meta.setTotalElements(ivaCalculationPage.getTotalElements());
        meta.setPageNumber(ivaCalculationPage.getNumber() + 1);
        meta.setPageSize(ivaCalculationPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Simulaciones recuperados exitosamente",
                ivaCalculationPage.getContent(),
                HttpStatus.OK,
                meta
        );
    }

    @GetMapping("/user-simulations")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getUserSimulations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<IvaCalculation> ivaCalculationPage;

        if (search == null || search.trim().isEmpty()) {
            ivaCalculationPage = ivaCalculationRepository.findAll(pageable);
        } else {
            ivaCalculationPage = ivaCalculationRepository.searchIvaSimulations(search.trim(), pageable);
        }

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(ivaCalculationPage.getTotalPages());
        meta.setTotalElements(ivaCalculationPage.getTotalElements());
        meta.setPageNumber(ivaCalculationPage.getNumber() + 1);
        meta.setPageSize(ivaCalculationPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Simulaciones recuperados exitosamente",
                ivaCalculationPage.getContent(),
                HttpStatus.OK,
                meta
        );
    }


    @PostMapping("/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> addIvaCalculation(@PathVariable Long userId, @RequestBody IvaCalculation ivaCalculation, HttpServletRequest request) {
        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isPresent()) {
            ivaCalculation.setUser(foundUser.get());
            
            if (ivaCalculation.getYear() == 0) {
                ivaCalculation.setYear(java.time.LocalDate.now().getYear());
            }
            if (ivaCalculation.getMonth() == 0) {
                ivaCalculation.setMonth(java.time.LocalDate.now().getMonthValue());
            }
            
            IvaCalculation processedCalculation = ivaCalculationService.processSimulation(ivaCalculation);
            
            processedCalculation.finalizeSimulation();
            
            IvaCalculation savedIvaCalculation = ivaCalculationRepository.save(processedCalculation);
            
            savedIvaCalculation = ivaCalculationRepository.findById(savedIvaCalculation.getId()).orElse(savedIvaCalculation);
            
            savedIvaCalculation.calculateTotals();
            
            return new GlobalResponseHandler().handleResponse("Simulación IVA creada con éxito",
                    savedIvaCalculation, HttpStatus.CREATED, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Usuario no encontrado",
                    HttpStatus.NOT_FOUND, request);
        }
    }
}
