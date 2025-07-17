package com.project.demo.rest.invoice;


import com.project.demo.logic.entity.invoice.Invoice;
import com.project.demo.logic.entity.invoice.InvoiceRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
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
@RequestMapping("/invoice")
public class InvoiceController {
    @Autowired
    InvoiceRepository invoiceRepository;
    @Autowired UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Invoice> electronicBillPage;

        if (search == null || search.trim().isEmpty()) {
            electronicBillPage = invoiceRepository.findAll(pageable);
        } else {
            electronicBillPage = invoiceRepository.seacrhInovices(search.trim(), pageable);
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
    public ResponseEntity<?> createElectronicBill(@RequestBody Invoice invoice, @PathVariable Long userId, HttpServletRequest request) {
        User user = userRepository.findById(userId).orElse(null);
        invoice.setUser(user);

        if (invoice.getDetails() != null) {
            invoice.getDetails().forEach(detail -> detail.setInvoice(invoice));
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return new GlobalResponseHandler().handleResponse("Factura agregada", savedInvoice, HttpStatus.CREATED, request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> updateInvoice(@PathVariable Long id, @RequestBody Invoice invoice, HttpServletRequest request) {
        Optional<Invoice> foundInvoice = invoiceRepository.findById(id);
        if (foundInvoice.isPresent()) {
            invoice.setId(foundInvoice.get().getId());
            invoiceRepository.save(invoice);
            return new GlobalResponseHandler().handleResponse("Factura actualizada exitosamente", invoice, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Factura no encontrada", HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long id, HttpServletRequest request) {
        Optional<Invoice> foundInvoice = invoiceRepository.findById(id);
        if (foundInvoice.isPresent()) {
            invoiceRepository.deleteById(id);
            return new GlobalResponseHandler().handleResponse("Factura eliminada exitosamente", foundInvoice.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Factura no encontrada", id, HttpStatus.NOT_FOUND, request);
        }
    }


}
