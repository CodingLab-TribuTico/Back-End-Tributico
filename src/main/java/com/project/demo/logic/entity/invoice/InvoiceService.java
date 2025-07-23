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
        Invoice invoiceToSave;

        if (invoice.getId() != null) {
            invoiceToSave = invoiceRepository.findById(invoice.getId())
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + invoice.getId()));

            invoiceToSave.setConsecutive(invoice.getConsecutive());
            invoiceToSave.setInvoiceKey(invoice.getInvoiceKey());
            invoiceToSave.setIssueDate(invoice.getIssueDate());
            invoiceToSave.setType(invoice.getType());

            if (invoiceToSave.getDetails() != null && !invoiceToSave.getDetails().isEmpty()) {
                detailsInvoiceRepository.deleteAll(invoiceToSave.getDetails());
            }
        } else {
            invoiceToSave = buildBaseInvoice(invoice, currentUser);
        }

        processInvoiceParties(invoiceToSave, invoice, currentUser);
        Invoice savedInvoice = invoiceRepository.save(invoiceToSave);
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
        newInvoice.setInvoiceKey(invoice.getInvoiceKey());
        newInvoice.setIssueDate(invoice.getIssueDate());
        newInvoice.setType(invoice.getType());
        return newInvoice;
    }

    private void processInvoiceParties(Invoice invoice, Invoice requestInvoice, User currentUser) {
        if (requestInvoice.getIssuer() != null) {
            InvoiceUser issuer = findOrCreateInvoiceUser(requestInvoice.getIssuer());
            invoice.setIssuer(issuer);
        } else {
            invoice.setIssuer(findOrCreateInvoiceUser(currentUser));
        }

        if (requestInvoice.getReceiver() != null) {
            InvoiceUser receiver = findOrCreateInvoiceUser(requestInvoice.getReceiver());
            invoice.setReceiver(receiver);
        } else {
            invoice.setReceiver(findOrCreateInvoiceUser(currentUser));
        }
    }

    private InvoiceUser findOrCreateInvoiceUser(InvoiceUser userFromRequest) {
        InvoiceUser existingUser = invoiceUserRepository.findByIdentification(userFromRequest.getIdentification());

        if (existingUser != null) {
            existingUser.setName(userFromRequest.getName());
            existingUser.setLastName(userFromRequest.getLastName());
            existingUser.setEmail(userFromRequest.getEmail());
            return invoiceUserRepository.save(existingUser);
        }

        return invoiceUserRepository.save(userFromRequest);
    }

    private InvoiceUser findOrCreateInvoiceUser(User user) {
        InvoiceUser existingUser = invoiceUserRepository.findByIdentification(user.getIdentification());

        if (existingUser != null) {
            existingUser.setName(user.getName());
            existingUser.setLastName(user.getLastname());
            existingUser.setEmail(user.getEmail());
            return invoiceUserRepository.save(existingUser);
        }

        InvoiceUser newUser = new InvoiceUser();
        newUser.setName(user.getName());
        newUser.setLastName(user.getLastname());
        newUser.setEmail(user.getEmail());
        newUser.setIdentification(user.getIdentification());
        return invoiceUserRepository.save(newUser);
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

        if (detail.getId() != null) {
            newDetail.setId(detail.getId());
        }

        newDetail.setCabys(detail.getCabys());
        newDetail.setDescription(detail.getDescription());
        newDetail.setQuantity(detail.getQuantity());
        newDetail.setUnitPrice(detail.getUnitPrice());
        newDetail.setDiscount(detail.getDiscount());
        newDetail.setUnit(detail.getUnit());
        newDetail.setTax(detail.getTax());
        newDetail.setTaxAmount(detail.getTaxAmount());
        newDetail.setTotal(detail.getTotal());
        newDetail.setCategory(detail.getCategory());
        newDetail.setInvoice(invoice);
        return newDetail;
    }
}