package com.project.demo.logic.entity.invoice;

import com.project.demo.logic.entity.detailsInvoice.DetailsInvoice;
import com.project.demo.logic.entity.detailsInvoice.DetailsInvoiceRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvoiceUserRepository invoiceUserRepository;
    @Autowired
    private DetailsInvoiceRepository detailsInvoiceRepository;

    public Invoice saveInvoice(Invoice invoice, Long userId) {
        User currentUser = validateAndGetUser(userId);
        Invoice newInvoice = buildBaseInvoice(invoice, currentUser);
        processInvoiceParties(newInvoice, invoice, currentUser);
        Invoice savedInvoice = invoiceRepository.save(newInvoice);
        processInvoiceDetails(invoice, savedInvoice);
        return savedInvoice;
    }

    private User validateAndGetUser(Long userId) {
        Optional<User> foundUser = userRepository.findById(userId);
        System.out.println("DEBUG: Usuario encontrado: " + foundUser.isPresent());
        return foundUser.orElse(null);
    }

    private Invoice buildBaseInvoice(Invoice invoice, User user) {
        Invoice newInvoice = new Invoice();
        newInvoice.setUser(user);
        newInvoice.setConsecutive(invoice.getConsecutive());
        newInvoice.setKey(invoice.getKey());
        newInvoice.setIssueDate(invoice.getIssueDate());
        newInvoice.setType(invoice.getType());
        return newInvoice;
    }

    private void processInvoiceParties(Invoice invoice, Invoice requestInvoice, User currentUser) {
        if ("GASTO".equalsIgnoreCase(requestInvoice.getType())) {
            processGastoInvoice(invoice, requestInvoice, currentUser);
        } else {
            processIngresoInvoice(invoice, requestInvoice, currentUser);
        }
    }

    private void processGastoInvoice(Invoice invoice, Invoice requestInvoice, User currentUser) {
        if (requestInvoice.getIssuer() != null) {
            InvoiceUser issuer = findOrCreateInvoiceUser(requestInvoice.getIssuer());
            invoice.setIssuer(issuer);
        }

        InvoiceUser receiver = findOrCreateInvoiceUser(currentUser);
        invoice.setReceiver(receiver);
    }

    private void processIngresoInvoice(Invoice invoice, Invoice requestInvoice, User currentUser) {
        InvoiceUser issuer = findOrCreateInvoiceUser(currentUser);
        invoice.setIssuer(issuer);

        if (requestInvoice.getReceiver() != null) {
            InvoiceUser receiver = findOrCreateInvoiceUser(requestInvoice.getReceiver());
            invoice.setReceiver(receiver);
        }
    }

    private InvoiceUser findOrCreateInvoiceUser(InvoiceUser invoiceUser) {
        InvoiceUser existingUser = invoiceUserRepository.findByIdentification(invoiceUser.getIdentification());
        return existingUser != null ? existingUser : invoiceUserRepository.save(invoiceUser);
    }

    private InvoiceUser findOrCreateInvoiceUser(User user) {
        InvoiceUser existingUser = invoiceUserRepository.findByIdentification(user.getIdentification());
        if (existingUser != null) {
            return existingUser;
        }

        InvoiceUser newInvoiceUser = new InvoiceUser();
        newInvoiceUser.setName(user.getName());
        newInvoiceUser.setLastName(user.getLastname());
        newInvoiceUser.setEmail(user.getEmail());
        newInvoiceUser.setIdentification(user.getIdentification());

        return invoiceUserRepository.save(newInvoiceUser);
    }

    private void processInvoiceDetails(Invoice requestInvoice, Invoice savedInvoice) {
        if (requestInvoice.getDetails() != null && !requestInvoice.getDetails().isEmpty()) {
            List<DetailsInvoice> details = requestInvoice.getDetails().stream()
                    .map(detail -> buildDetailInvoice(detail, savedInvoice))
                    .collect(Collectors.toList());

            detailsInvoiceRepository.saveAll(details);
            savedInvoice.setDetails(details);
        }
    }

    private DetailsInvoice buildDetailInvoice(DetailsInvoice detail, Invoice invoice) {
        DetailsInvoice newDetail = new DetailsInvoice();
        newDetail.setCabys(detail.getCabys());
        newDetail.setDetailDescription(detail.getDetailDescription());
        newDetail.setQuantity(detail.getQuantity());
        newDetail.setUnitPrice(detail.getUnitPrice());
        newDetail.setUnit(detail.getUnit());
        newDetail.setTax(detail.getTax());
        newDetail.setTaxAmount(detail.getTaxAmount());
        newDetail.setTotal(detail.getTotal());
        newDetail.setCategory(detail.getCategory());
        newDetail.setInvoice(invoice);
        return newDetail;
    }

}