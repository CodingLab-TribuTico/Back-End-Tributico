package com.project.demo.logic.entity.llm;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LLMService {
    @Value("${spring.ai.ollama.chat.options.model}")
    private String model;

    @Autowired
    private ChatModel chatModel;

    public String generateInvoiceJson(String invoice, String type) {
        String prompt = String.format("""
                        From the OCR text between [[[ ]]] of a Costa Rican electronic invoice of type %s, complete ONLY the fields of the following JSON. Do not add explanations, headers, or comments. Return exclusively a valid and well-formed JSON (without any additional text). Use `null` if data is unavailable or illegible.
                        
                        If the invoice type is `"ingreso"`, include only the `receiver` (client) information.
                        If the invoice type is `"gasto"`, include only the `issuer` (supplier) information.
                        Completely omit the other field.
                        
                        Automatically classify each item in the `details` field into the `"category"` key using one of the following **category codes** according to the text content:
                        
                        VG-B   = Venta Gravada de Bienes (13%%)
                        VG-S   = Venta Gravada de Servicios (13%%)
                        VE     = Venta Exenta (Ley u oficio)
                        VX     = Venta Exonerada (usuario final exonerado)
                        EXP    = Exportación de Bienes o Servicios  
                        CBG    = Compra de Bienes Gravados (con derecho a crédito fiscal)  
                        CSG    = Compra de Servicios Gravados (con derecho a crédito fiscal)  
                        CX     = Compra Exenta o Exonerada (sin derecho a crédito)  
                        CBR    = Compra de Bienes con Tarifa Reducida (1%%, 2%%)  
                        CSR    = Compra de Servicios con Tarifa Reducida (1%%, 2%%)  
                        GSP    = Gasto de Servicios Públicos - Gravado  
                        GA     = Gasto Administrativo - Gravado  
                        SPS    = Servicios Profesionales Subcontratados - Gravado  
                        HP     = Honorarios Profesionales - Gravado  
                        GV     = Gasto de Transporte o Viáticos - Gravado  
                        PP     = Publicidad y Promoción - Gravado  
                        ALO    = Alquiler de Local u Oficina  
                        MR     = Mantenimiento y Reparaciones - Gravado  
                        CAF    = Compra de Activos Fijos - IVA prorrateable  
                        GF     = Gasto Financiero - No lleva IVA  
                        GS     = Gasto Salarial - No lleva IVA  
                        NCE    = Notas de Crédito Emitidas  
                        NCR    = Notas de Crédito Recibidas  
                        DON    = Donación Deducible - No lleva IVA  
                        MUL    = Multas, Sanciones o Gastos No Deducibles
                        
                        Requirements:
                        - All required fields must be present according to the invoice type.
                        - The JSON must be syntactically correct.
                        - Use correct data types.
                        - Do not include fields outside the schema.
                        - Do not include explanations, headers, or additional text.
                        - Do not touch the `type` field; it fills itself.
                        
                        Required format:
                        {
                          "consecutive": <string>,
                          "key": <string>,
                          "issueDate": "<yyyy-mm-dd>",
                        
                          "issuer": {
                            "name": "<string>",
                            "lastname": "<string>",
                            "identification": <int>,
                            "email": "<string>"
                          },
                          "receiver": {
                            "name": "<string>",
                            "lastname": "<string>",
                            "identification": <int>,
                            "email": "<string>"
                          },
                        
                          "details": [
                            {
                              "cabys": <string(13 digits)>,
                              "quantity": <float>,
                              "unit": "<string>",
                              "unitPrice": <float>,
                              "discount": <float>,
                              "tax": <float>,  // Only allowed values: %s, %s, %s, %s, %s, or %s,
                              "taxAmount": <float>,
                              "category": "<string (category code)>",
                              "total": <float>,
                              "description": "<string>",
                            }
                          ]
                        }
                        
                        OCR Text:
                        [[[
                        %s
                        ]]]
                        """, type, TaxRateEnum.TAX_RATE_GENERAL.getRate(), TaxRateEnum.TAX_RATE_REDUCED.getRate(),
                TaxRateEnum.TAX_RATE_SPECIAL.getRate(), TaxRateEnum.TAX_RATE_ADJUSTED.getRate(),
                TaxRateEnum.TAX_RATE_MINIMUM.getRate(), TaxRateEnum.TAX_RATE_ZERO.getRate(), invoice);


        OllamaOptions options = OllamaOptions.builder()
                .model(model)
                .build();

        ChatResponse response = chatModel.call(new Prompt(prompt, options));
        return response.getResult().getOutput().getText();
    }

    public String cleanJson(String input) {
        int start = input.indexOf('{');
        int end = input.lastIndexOf('}');
        if (start != -1 && end != -1 && start < end) {
            return input.substring(start, end + 1).trim();
        }
        throw new IllegalArgumentException("No se encontró un JSON válido en el texto.");
    }

    public String generateTaxRecommendations(String declaration, String objective, String targetDate, String userContext) {
        String prompt = String.format("""
    Genera consejos específicos y prácticos para el siguiente objetivo tributario en Costa Rica:
    
    OBJETIVO: %s
    TIPO: %s
    FECHA: %s
    
    INSTRUCCIONES ESPECÍFICAS:
    - Genera exactamente 3 consejos prácticos y específicos para este objetivo
    - Cada consejo debe ser MUY CORTO (máximo 1 línea)
    - Enfócate específicamente en cómo lograr "%s"
    - Si es IVA: consejos sobre crédito fiscal, compras, ventas, fechas de pago
    - Si es RENTA/ISR: consejos sobre deducciones, pagos a cuenta, documentos
    - Usa números y datos específicos cuando sea posible
    - NO uses asteriscos ni formateo especial
    
    FORMATO REQUERIDO:
    1. [Consejo específico de 1 línea]
    2. [Consejo específico de 1 línea]  
    3. [Consejo específico de 1 línea]
    
    Responde ÚNICAMENTE con los 3 consejos numerados, sin texto adicional.
    """, objective, declaration, targetDate, objective);

        OllamaOptions options = OllamaOptions.builder()
                .model(model)
                .temperature(0.4) // Balance entre creatividad y precisión
                .build();

        ChatResponse response = chatModel.call(new Prompt(prompt, options));
        return response.getResult().getOutput().getText().trim();

    }
}
