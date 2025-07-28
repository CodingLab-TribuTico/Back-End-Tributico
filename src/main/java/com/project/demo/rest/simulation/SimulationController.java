package com.project.demo.rest.simulation;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.isrSimulation.IsrRepository;
import com.project.demo.logic.entity.isrSimulation.IsrSimulation;
import com.project.demo.logic.entity.isrSimulation.IsrSimulationService;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/simulation")
public class SimulationController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    IsrRepository isrRepository;

    @Autowired
    IsrSimulationService simulationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> saveSimulation(@RequestBody IsrSimulation simulation,
                                            HttpServletRequest request) {
        Optional<User> foundUser = userRepository.findById(simulation.getUser().getId());
        if (foundUser.isPresent()) {
            simulation.setUser(foundUser.get());
            IsrSimulation savedSimulation = isrRepository.save(simulation);
            return new GlobalResponseHandler().handleResponse("Simulación guardada con exito",
                    savedSimulation, HttpStatus.CREATED, request);

        } else {
            return new GlobalResponseHandler().handleResponse("No se encontro el usuario",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String search,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<IsrSimulation> simulationPage;

        if (search == null || search.trim().isEmpty()) {
            simulationPage = isrRepository.findByUserId(userPrincipal.getId(), pageable);
        } else {
            simulationPage = isrRepository.searchIsrSimulation(search.trim(), userPrincipal.getId(),
                    pageable);
        }

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(simulationPage.getTotalPages());
        meta.setTotalElements(simulationPage.getTotalElements());
        meta.setPageNumber(simulationPage.getNumber() + 1);
        meta.setPageSize(simulationPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Simulaciones recuperadas exitosamente",
                simulationPage.getContent(),
                HttpStatus.OK,
                meta);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> deleteIsrSimulation(@PathVariable Long id, HttpServletRequest request) {
        Optional<IsrSimulation> foundSimulation = isrRepository.findById(id);
        if (foundSimulation.isPresent()) {
            isrRepository.deleteById(id);
            return new GlobalResponseHandler().handleResponse("Simulación eliminada exitosamente",
                    foundSimulation.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Simulación no encontrada",
                    id, HttpStatus.NOT_FOUND, request);
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getSimulationByUserId(@PathVariable Long userId, HttpServletRequest request) {
        List<IsrSimulation> simulation = isrRepository.findByUserId(userId);

        return new GlobalResponseHandler().handleResponse("Simulaciones recuperadas exitosamente",
                simulation, HttpStatus.OK, request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getSimulationById(@PathVariable Long id, HttpServletRequest request) {
        Optional<IsrSimulation> simulation = isrRepository.findById(id);
        if (simulation.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Simulación encontrada",
                    simulation,HttpStatus.OK, request);
        }

        return new GlobalResponseHandler().handleResponse("Simulación no encontrada",
                null,HttpStatus.NOT_FOUND, request);
    }

    //tiquete 46

    @GetMapping("/generate-pdf/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        Optional<IsrSimulation> foundSimulation = isrRepository.findById(id);

        if (foundSimulation.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        IsrSimulation simulation = foundSimulation.get();

        try {
            String htmlBody = simulationService.generateSimulation(simulation);
            String formattedHtmlContent = IsrSimulationService.generateHtmlContent(htmlBody);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            ConverterProperties converterProperties = new ConverterProperties();
            HtmlConverter.convertToPdf(formattedHtmlContent, pdfDocument, converterProperties);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("Simulación_" + id + ".pdf")
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
        String csvContent = simulationService.generateCsvContent(sim);

        byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("simulacion_" + id + ".csv")
                .build());

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }
    /*
    @GetMapping("/generate-csv/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<byte[]> downloadCsvById(@PathVariable Long id) {
        Optional<IsrSimulation> simulationOpt = isrRepository.findById(id);

        if (simulationOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        IsrSimulation simulation = simulationOpt.get();

        try {
            // Aquí generamos el contenido CSV
            String csvContent = simulationService.generateCsvContent(simulation);
            byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=simulacion_" + id + ".csv");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

            return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    */



}

