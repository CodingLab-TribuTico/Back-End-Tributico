package com.project.demo.rest.invoice;

import com.project.demo.logic.entity.detailsInvoice.DetailsInvoice;
import com.project.demo.logic.entity.detailsInvoice.DetailsInvoiceRepository;
import com.project.demo.logic.entity.invoice.*;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.user.User;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/invoices")
public class InvoiceRestController {
    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    DetailsInvoiceRepository detailsInvoiceRepository;
    @Autowired
    InvoiceUserRepository invoiceUserRepository;
    @Autowired
    InvoiceService invoiceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String search,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest request) {

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Invoice> electronicBillPage;

        if (search == null || search.trim().isEmpty()) {
            electronicBillPage = invoiceRepository.findByUserId(userPrincipal.getId(), pageable);
        } else {
            electronicBillPage = invoiceRepository.searchElectronicBills(search.trim(), userPrincipal.getId(),
                    pageable);
        }

        meta.setTotalPages(electronicBillPage.getTotalPages());
        meta.setTotalElements(electronicBillPage.getTotalElements());
        meta.setPageNumber(electronicBillPage.getNumber() + 1);
        meta.setPageSize(electronicBillPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Facturas recuperadas exitosamente",
                electronicBillPage.getContent(),
                HttpStatus.OK,
                meta);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> createInvoice(@RequestBody Invoice invoice, @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest request) {
        try {
            Invoice savedInvoice = invoiceService.saveInvoice(invoice, userPrincipal.getId());

            return new GlobalResponseHandler().handleResponse("Factura creada exitosamente",
                    savedInvoice, HttpStatus.CREATED, request);

        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error al crear factura: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> updateInvoice(
            @PathVariable Long id,
            @RequestBody Invoice requestInvoice,
            HttpServletRequest request) {

        Optional<Invoice> found = invoiceRepository.findById(id);
        if (found.isEmpty()) {
            return new GlobalResponseHandler().handleResponse("Factura no encontrada", HttpStatus.NOT_FOUND, request);
        }

        Invoice invoiceToUpdate = found.get();

        invoiceToUpdate.setConsecutive(requestInvoice.getConsecutive());
        invoiceToUpdate.setIssueDate(requestInvoice.getIssueDate());
        invoiceToUpdate.setInvoiceKey(requestInvoice.getInvoiceKey());
        invoiceToUpdate.setType(requestInvoice.getType());

        if (invoiceToUpdate.getIssuer() != null && requestInvoice.getIssuer() != null) {
            Optional<InvoiceUser> issuerOpt = invoiceUserRepository.findById(invoiceToUpdate.getIssuer().getId());
            issuerOpt.ifPresent(issuer -> {
                issuer.setName(requestInvoice.getIssuer().getName());
                issuer.setLastName(requestInvoice.getIssuer().getLastName());
                issuer.setIdentification(requestInvoice.getIssuer().getIdentification());
                issuer.setEmail(requestInvoice.getIssuer().getEmail());
                invoiceUserRepository.save(issuer);
            });
        }

        if (invoiceToUpdate.getReceiver() != null && requestInvoice.getReceiver() != null) {
            Optional<InvoiceUser> receiverOpt = invoiceUserRepository.findById(invoiceToUpdate.getReceiver().getId());
            receiverOpt.ifPresent(receiver -> {
                receiver.setName(requestInvoice.getReceiver().getName());
                receiver.setLastName(requestInvoice.getReceiver().getLastName());
                receiver.setIdentification(requestInvoice.getReceiver().getIdentification());
                receiver.setEmail(requestInvoice.getReceiver().getEmail());
                invoiceUserRepository.save(receiver);
            });
        }

        List<DetailsInvoice> currentDetails = invoiceToUpdate.getDetails();
        List<DetailsInvoice> incomingDetails = requestInvoice.getDetails();

        List<DetailsInvoice> toRemove = currentDetails.stream()
                .filter(existing -> incomingDetails.stream().noneMatch(incoming ->
                        incoming.getId() != null && incoming.getId().equals(existing.getId())))
                .collect(Collectors.toList());

        detailsInvoiceRepository.deleteAll(toRemove);
        currentDetails.removeAll(toRemove);

        for (DetailsInvoice detail : incomingDetails) {
            if (detail.getId() == null) {
                detail.setInvoice(invoiceToUpdate);
                currentDetails.add(detail);
            } else {
                DetailsInvoice existing = currentDetails.stream()
                        .filter(d -> d.getId().equals(detail.getId()))
                        .findFirst()
                        .orElse(null);
                if (existing != null) {
                    existing.setCabys(detail.getCabys());
                    existing.setDescription(detail.getDescription());
                    existing.setQuantity(detail.getQuantity());
                    existing.setUnitPrice(detail.getUnitPrice());
                    existing.setDiscount(detail.getDiscount());
                    existing.setUnit(detail.getUnit());
                    existing.setTax(detail.getTax());
                    existing.setTaxAmount(detail.getTaxAmount());
                    existing.setTotal(detail.getTotal());
                    existing.setCategory(detail.getCategory());
                }
            }
        }

        invoiceRepository.save(invoiceToUpdate);

        return new GlobalResponseHandler().handleResponse(
                "Factura actualizada correctamente",
                invoiceToUpdate,
                HttpStatus.OK,
                request
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long id, HttpServletRequest request) {
        Optional<Invoice> foundInvoice = invoiceRepository.findById(id);
        if (foundInvoice.isPresent()) {
            invoiceRepository.deleteById(id);
            return new GlobalResponseHandler().handleResponse("Factura eliminada exitosamente", foundInvoice.get(),
                    HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Factura no encontrada", id, HttpStatus.NOT_FOUND,
                    request);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getInvoiceById(@PathVariable Long id, HttpServletRequest request) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (invoice.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Factura encontrada",invoice,HttpStatus.OK, request);
        }

        return new GlobalResponseHandler().handleResponse("Factura no encontrada",null,HttpStatus.NOT_FOUND, request);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getInvoiceByUserId(@PathVariable Long userId, HttpServletRequest request) {
        List<Invoice> invoices = invoiceRepository.findByUserId(userId);

        return new GlobalResponseHandler().handleResponse("Facturas recuperadas exitosamente", invoices, HttpStatus.OK,
                request);
    }
}
