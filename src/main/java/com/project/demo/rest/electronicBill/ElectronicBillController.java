package com.project.demo.rest.electronicBill;


import com.project.demo.logic.entity.electronicBill.ElectronicBill;
import com.project.demo.logic.entity.electronicBill.ElectronicBillRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.filters.ExpiresFilter;
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
@RequestMapping("/electronic-bill")
public class ElectronicBillController {
    @Autowired ElectronicBillRepository electronicBillRepository;
    @Autowired UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ElectronicBill> electronicBillPage;

        if (search == null || search.trim().isEmpty()) {
            electronicBillPage = electronicBillRepository.findAll(pageable);
        } else {
            electronicBillPage = electronicBillRepository.searchElectronicBills(search.trim(), pageable);
        }

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(electronicBillPage.getTotalPages());
        meta.setTotalElements(electronicBillPage.getTotalElements());
        meta.setPageNumber(electronicBillPage.getNumber() + 1);
        meta.setPageSize(electronicBillPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Electronic recuperados exitosamente",
                electronicBillPage.getContent(),
                HttpStatus.OK,
                meta
        );
    }

    @PostMapping("/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> createElectronicBill(@RequestBody ElectronicBill electronicBill, @PathVariable Long userId, HttpServletRequest request) {
        try {
            System.out.println("DEBUG: userId recibido: " + userId);

            Optional<User> foundUser = userRepository.findById(userId);
            
            System.out.println("DEBUG: Usuario encontrado: " + foundUser.isPresent());
            
            if (foundUser.isPresent()) {
                User user = foundUser.get();
                System.out.println("DEBUG: Usuario ID: " + user.getId());
                System.out.println("DEBUG: Usuario identification: " + user.getIdentification());
                
                electronicBill.setUser(user);
                
                System.out.println("DEBUG: Usuario asignado a factura: " + electronicBill.getUser().getId());
                
                if (electronicBill.getDetails() != null) {
                    System.out.println("DEBUG: Número de detalles: " + electronicBill.getDetails().size());
                    electronicBill.getDetails().forEach(detail -> {
                        detail.setElectronicBill(electronicBill);
                        System.out.println("DEBUG: Detalle asignado: " + detail.getDetailDescription());
                    });
                }
                
                ElectronicBill savedBill = electronicBillRepository.save(electronicBill);
                System.out.println("DEBUG: Factura guardada con ID: " + savedBill.getId());
                
                return new GlobalResponseHandler().handleResponse("Factura agregada", savedBill, HttpStatus.CREATED, request);
            } else {
                System.out.println("DEBUG: Usuario no encontrado con ID: " + userId);
                return new GlobalResponseHandler().handleResponse("No existe el usuario", null, HttpStatus.NOT_FOUND, request);
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Error completo: " + e.getMessage());
            e.printStackTrace();
            return new GlobalResponseHandler().handleResponse("Error al crear factura: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> updateElectronicBill(@PathVariable Long id, @RequestBody ElectronicBill electronicBill, HttpServletRequest request) {
        Optional<ElectronicBill> foundElectronicBill = electronicBillRepository.findById(id);
        if (foundElectronicBill.isPresent()) {
            electronicBill.setId(foundElectronicBill.get().getId());
            electronicBillRepository.save(electronicBill);
            return new GlobalResponseHandler().handleResponse("Factura actualizada exitosamente", electronicBill, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Factura no encontrada", HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> deleteElectronicBill(@PathVariable Long id, HttpServletRequest request) {
        Optional<ElectronicBill> foundElectronicBill = electronicBillRepository.findById(id);
        if (foundElectronicBill.isPresent()) {
            electronicBillRepository.deleteById(id);
            return new GlobalResponseHandler().handleResponse("Factura eliminada exitosamente", foundElectronicBill.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Factura no encontrada", id, HttpStatus.NOT_FOUND, request);
        }
    }


}
