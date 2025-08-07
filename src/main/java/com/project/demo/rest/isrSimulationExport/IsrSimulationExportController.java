package com.project.demo.rest.isrSimulationExport;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.project.demo.logic.entity.isrSimulation.IsrRepository;
import com.project.demo.logic.entity.isrSimulation.IsrSimulation;
import com.project.demo.logic.entity.isrSimulation.IsrExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("/export")
public class IsrSimulationExportController {

    @Autowired
    IsrRepository isrRepository;

    @Autowired
    IsrExportService simulationExportService;

    @GetMapping("/generate-pdf/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        Optional<IsrSimulation> foundSimulation = isrRepository.findById(id);

        if (foundSimulation.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        IsrSimulation simulation = foundSimulation.get();

        try {
            String htmlBody = simulationExportService.generateSimulationPdf(simulation);
            String formattedHtmlContent = simulationExportService.generateHtmlContent(htmlBody);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            ConverterProperties converterProperties = new ConverterProperties();
            HtmlConverter.convertToPdf(formattedHtmlContent, pdfDocument, converterProperties);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("simulación.pdf")
                    .build());
            headers.setContentType(MediaType.APPLICATION_PDF);

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/generate-csv/{id}")
    public ResponseEntity<byte[]> generateCsv(@PathVariable Long id) {
        Optional<IsrSimulation> foundSimulation = isrRepository.findById(id);

        if (foundSimulation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        IsrSimulation sim = foundSimulation.get();
        String csvContent = simulationExportService.generateSimulationCsv(sim);
        byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("Simulación.csv")
                .build());

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }
}


