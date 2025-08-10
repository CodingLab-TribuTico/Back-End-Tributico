package com.project.demo.rest.ivaSimulationExport;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.project.demo.logic.entity.ivacalculation.IvaCalculation;
import com.project.demo.logic.entity.ivacalculation.IvaCalculationRepository;
import com.project.demo.logic.entity.ivacalculation.IvaExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
@RequestMapping("/iva-export")
public class IvaSimulationExport {

    @Autowired
    IvaCalculationRepository ivaCalculationRepository;

    @Autowired
    IvaExportService ivaExportService;

    @GetMapping("/generate-pdf/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        Optional<IvaCalculation> foundSimulation = ivaCalculationRepository.findById(id);

        if (foundSimulation.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        IvaCalculation simulation = foundSimulation.get();

        try {
            String htmlContent = ivaExportService.generateSimulationPdf(simulation);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            ConverterProperties properties = new ConverterProperties();

            HtmlConverter.convertToPdf(htmlContent, pdf, properties);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename("IVA-Simulacion.pdf").build());

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/generate-csv/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<byte[]> generateCsv(@PathVariable Long id) {
        Optional<IvaCalculation> foundSimulation = ivaCalculationRepository.findById(id);

        if (foundSimulation.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        IvaCalculation simulation = foundSimulation.get();
        String csvContent = ivaExportService.generateCsv(simulation);
        byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("IVA-Simulacion.csv").build());

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }
}
