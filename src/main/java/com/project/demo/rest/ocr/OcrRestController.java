package com.project.demo.rest.ocr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;

@RestController
@RequestMapping("/ocr")
public class OcrRestController {

    @PostMapping
    public ResponseEntity<?> extractText(@RequestParam("file") MultipartFile file, @RequestParam("type") String type, HttpServletRequest request) {
        try {
            String text;
            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                text = stripper.getText(document);
            }

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode resultWithType = mapper.createObjectNode();
            resultWithType.put("type", type);
            resultWithType.put("extractedText", text);

            return new GlobalResponseHandler().handleResponse("Texto extraído exitosamente", resultWithType,
                    HttpStatus.OK, request);
        } catch (IOException e) {
            return new GlobalResponseHandler().handleResponse("Error al procesar el archivo PDF", e.getMessage(),
                    HttpStatus.BAD_REQUEST, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error inesperado al generar el JSON", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request
            );
        }
    }
}