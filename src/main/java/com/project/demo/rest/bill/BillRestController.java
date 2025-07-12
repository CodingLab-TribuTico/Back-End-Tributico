package com.project.demo.rest.bill;

import com.project.demo.logic.entity.bill.Bill;
import com.project.demo.logic.entity.bill.BillRepository;
import com.project.demo.logic.entity.bill.BillService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RequestMapping("/bill")
@RestController
public class BillRestController {

    @Autowired
    private BillService billService;

    @Autowired
    private BillRepository billRepository;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','USER')")
    public ResponseEntity<Bill> upload(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Bill saved = billService.formatAndSave(inputStream);
            System.out.println(saved.toString());
            billRepository.save(saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAllBills(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         HttpServletRequest request){
        
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Bill> billsPage;

        billsPage = billRepository.findAll(pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(billsPage.getTotalPages());
        meta.setTotalElements(billsPage.getTotalElements());
        meta.setPageNumber(billsPage.getNumber() + 1);
        meta.setPageSize(billsPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Facturas recuperadas exitosamente",
                billsPage.getContent(),
                HttpStatus.OK,
                meta
        );
        
    }

}
