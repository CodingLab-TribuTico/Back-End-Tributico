package com.project.demo.rest.ivaCalculation;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.isrSimulation.IsrSimulation;
import com.project.demo.logic.entity.ivacalculation.IvaCalculation;
import com.project.demo.logic.entity.ivacalculation.IvaCalculationRepository;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/iva-simulation")
public class IvaCalculationController {
    @Autowired
    IvaCalculationService ivaService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    IvaCalculationRepository ivaCalculationRepository;

    @GetMapping("/create")
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

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> saveSimulation(@RequestBody IvaCalculation simulation,
                                            HttpServletRequest request) {
        Optional<User> foundUser = userRepository.findById(simulation.getUser().getId());
        if (foundUser.isPresent()) {
            simulation.setUser(foundUser.get());
            IvaCalculation savedSimulation = ivaCalculationRepository.save(simulation);
            return new GlobalResponseHandler().handleResponse("Simulación guardada exitosamente",
                    savedSimulation, HttpStatus.CREATED, request);

        } else {
            return new GlobalResponseHandler().handleResponse("No se encontro el usuario",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String search,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<IvaCalculation> simulationPage;

        if (search == null || search.trim().isEmpty()) {
            simulationPage = ivaCalculationRepository.findByUserId(userPrincipal.getId(), pageable);
        } else {
            simulationPage = ivaCalculationRepository.searchIvaSimulation(search.trim(), userPrincipal.getId(),
                    pageable);
        }

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(simulationPage.getTotalPages());
        meta.setTotalElements(simulationPage.getTotalElements());
        meta.setPageNumber(simulationPage.getNumber() + 1);
        meta.setPageSize(simulationPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Simulaciones recuperadas exitosamente",
                simulationPage.getContent(),
                HttpStatus.OK,
                meta);
    }


}
