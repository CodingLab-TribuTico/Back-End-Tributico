package com.project.demo.rest.detailsBill;


import com.project.demo.logic.entity.detailsBill.DetailsBill;
import com.project.demo.logic.entity.detailsBill.DetailsBillRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
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
@RequestMapping("/details-bill")
public class DetailsBillController {
    @Autowired DetailsBillRepository detailsBillRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<DetailsBill> detailsBillPage;

        if (search == null || search.trim().isEmpty()) {
            detailsBillPage = detailsBillRepository.findAll(pageable);
        } else {
            detailsBillPage = detailsBillRepository.searchBillsDetails(search.trim(), pageable);
        }

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(detailsBillPage.getTotalPages());
        meta.setTotalElements(detailsBillPage.getTotalElements());
        meta.setPageNumber(detailsBillPage.getNumber() + 1);
        meta.setPageSize(detailsBillPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Detalles recuperados exitosamente",
                detailsBillPage.getContent(),
                HttpStatus.OK,
                meta
        );
    }

    @PostMapping
    public ResponseEntity<?> createDetailsBill(@RequestBody DetailsBill detailsBill, HttpServletRequest request) {
        DetailsBill savedDetailsBill = detailsBillRepository.save(detailsBill);
        return new GlobalResponseHandler().handleResponse("Detalles de facturas creados", savedDetailsBill, HttpStatus.CREATED, request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getDetailsBillById(@PathVariable Long id, HttpServletRequest request) {
        Optional<DetailsBill> foundDetailsBill = detailsBillRepository.findById(id);
        if (foundDetailsBill.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Detalles no encontrados", foundDetailsBill.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Detalles no encontrados" + id + "no fue encontrada", HttpStatus.NOT_FOUND, request);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> updateDetailsBill(@PathVariable Long id, @RequestBody DetailsBill detailsBill, HttpServletRequest request) {
        Optional<DetailsBill> foundDetailsBill = detailsBillRepository.findById(id);
        if (foundDetailsBill.isPresent()) {
            detailsBill.setId(foundDetailsBill.get().getId());
            detailsBillRepository.save(detailsBill);
            return new GlobalResponseHandler().handleResponse("Detalles actualizados exitosamente", detailsBill, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Detalles no encontrados", HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> deleteDetailsBill(@PathVariable Long id, HttpServletRequest request) {
        Optional<DetailsBill> foundDetailsBill = detailsBillRepository.findById(id);
        if (foundDetailsBill.isPresent()) {
            detailsBillRepository.deleteById(id);
            return new GlobalResponseHandler().handleResponse("Detalles eliminados exitosamente", foundDetailsBill.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Detalles no encontrados", id, HttpStatus.NOT_FOUND, request);
        }
    }


}
