package com.project.demo.invoice;

import com.project.demo.logic.entity.detailsInvoice.DetailsInvoice;
import com.project.demo.logic.entity.invoice.Invoice;
import com.project.demo.logic.entity.invoice.InvoiceRepository;
import com.project.demo.logic.entity.invoice.InvoiceService;
import com.project.demo.logic.entity.invoiceUser.InvoiceUser;
import com.project.demo.logic.entity.user.User;
import com.project.demo.rest.invoice.InvoiceRestController;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceRestControllerTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private InvoiceRestController invoiceRestController;

    private Invoice testInvoice;
    private User testUser;
    private List<DetailsInvoice> testDetails;
    private InvoiceUser testInvoiceUser;

    @BeforeEach
    void setUp() {
        testInvoice = new Invoice();
        testInvoice.setId(1L);
        testInvoice.setConsecutive("123456");
        testInvoice.setIssueDate(LocalDate.now());
        testInvoice.setType("Ingreso");
        testInvoice.setInvoiceKey("12");
        testInvoice.setUser(testUser);
        testInvoice.setReceiver(testInvoiceUser);
        testInvoice.setDetails(testDetails);

        testDetails = new ArrayList<>();
        DetailsInvoice detailsInvoice = new DetailsInvoice();
        detailsInvoice.setId(1L);
        detailsInvoice.setCabys("1234");
        detailsInvoice.setDescription("Test");
        detailsInvoice.setQuantity(2);
        detailsInvoice.setUnitPrice(2000000);
        detailsInvoice.setUnit("Test");
        detailsInvoice.setDiscount(0);
        detailsInvoice.setTax(13);
        detailsInvoice.setCategory("Test");
        detailsInvoice.setTaxAmount(2000000);
        detailsInvoice.setTotal(2300000);
        testDetails.add(detailsInvoice);

        testInvoiceUser = new InvoiceUser();
        testInvoiceUser.setId(1L);
        testInvoiceUser.setName("Diego");
        testInvoiceUser.setLastName("Nunez");
        testInvoiceUser.setIdentification("101110111");
        testInvoiceUser.setEmail("diego@gmail.com");

        testUser = new User();
        testUser.setId(1L);

        StringBuffer reqURL = new StringBuffer("http://localhost");
        when(httpServletRequest.getRequestURL()).thenReturn(reqURL);
    }

    @Test
    @DisplayName("Debe crear una factura")
    void addInvoice_WithValidData_ShouldReturnCreated() {
        when(invoiceService.saveInvoice(any(Invoice.class), eq(testUser.getId()))).thenReturn(testInvoice);

        ResponseEntity<?> response = invoiceRestController.createInvoice(testInvoice, testUser, httpServletRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(invoiceService).saveInvoice(any(Invoice.class), eq(testUser.getId()));
    }

    @Test
    @DisplayName("Debe retornar una lista paginada con todas las facturas")
    void getAllInvoices_WhenInvoicesExist_ShouldReturnPaginatedList() {
        Page<Invoice> invoicesPage = new PageImpl<>(Collections.singletonList(testInvoice));
        when(invoiceRepository.findByUserId(eq(testUser.getId()), any(Pageable.class)))
                .thenReturn(invoicesPage);

        ResponseEntity<?> response = invoiceRestController.getAll(1, 5, "", testUser, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(invoiceRepository).findByUserId(eq(testUser.getId()), any(Pageable.class));
    }

    @Test
    @DisplayName("Debe actualizar una factura cuando el ID proporcionado es válido")
    void updateInvoice_WithExistingId_ShouldReturnOk() {
        when(invoiceService.saveInvoice(any(Invoice.class), eq(testUser.getId()))).thenReturn(testInvoice);

        ResponseEntity<?> response = invoiceRestController.updateInvoice(1L, testInvoice, testUser, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(invoiceService).saveInvoice(any(Invoice.class), eq(testUser.getId()));
    }

    @Test
    @DisplayName("Debe eliminar una factura cuando el ID proporcionado es válido")
    void deleteInvoice_WithExistingId_ShouldReturnOk() {
        doNothing().when(invoiceService).deleteInvoice(1L);

        ResponseEntity<?> response = invoiceRestController.deleteInvoice(1L, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(invoiceService).deleteInvoice(1L);
    }

}
