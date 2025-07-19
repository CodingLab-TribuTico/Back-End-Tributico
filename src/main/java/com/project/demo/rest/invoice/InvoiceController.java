package com.project.demo.rest.invoice;


import com.project.demo.logic.entity.detailsInvoice.DetailsInvoice;
import com.project.demo.logic.entity.detailsInvoice.DetailsInvoiceRepository;
import com.project.demo.logic.entity.invoice.*;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
    @Autowired
    InvoiceRepository invoiceRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    DetailsInvoiceRepository detailsInvoiceRepository;
    @Autowired
    InvoiceUserRepository invoiceUserRepository;
    @Autowired
    InvoiceService  invoiceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Invoice> electronicBillPage;

        if (search == null || search.trim().isEmpty()) {
            electronicBillPage = invoiceRepository.findByUserId(userPrincipal.getId(), pageable);
        } else {
            electronicBillPage = invoiceRepository.searchElectronicBills(search.trim(), userPrincipal.getId(), pageable);
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
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> createInvoice(@RequestBody Invoice invoice, @AuthenticationPrincipal User userPrincipal,
                                           HttpServletRequest request) {
        try {
            Invoice savedInvoice = invoiceService.saveInvoice(invoice,userPrincipal.getId());

            return new GlobalResponseHandler().handleResponse("Factura creada exitosamente",
                    savedInvoice,HttpStatus.CREATED, request);

        }catch (Exception e){
            return new GlobalResponseHandler().handleResponse("Error al crear factura: "+ e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
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
