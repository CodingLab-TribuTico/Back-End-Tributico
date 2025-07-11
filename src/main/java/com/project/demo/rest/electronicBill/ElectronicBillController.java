package com.project.demo.rest.electronicBill;


import com.project.demo.logic.entity.electronicBill.ElectronicBill;
import com.project.demo.logic.entity.electronicBill.ElectronicBillRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
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

    @PostMapping
    public ResponseEntity<?> createElectronicBill(@RequestBody ElectronicBill electronicBill, HttpServletRequest request) {
        ElectronicBill savedElectronicBill = electronicBillRepository.save(electronicBill);
        return new GlobalResponseHandler().handleResponse("Factura electónica creada", savedElectronicBill, HttpStatus.CREATED, request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getElectronicBillById(@PathVariable Long id, HttpServletRequest request) {
        Optional<ElectronicBill> foundElectricalBill = electronicBillRepository.findById(id);
        if (foundElectricalBill.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Factura no encontrada", foundElectricalBill.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Factura no encontrada" + id + "no fue encontrada",HttpStatus.NOT_FOUND, request);
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
