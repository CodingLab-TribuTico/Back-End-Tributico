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

    public String generateInvoiceJson(String invoice) {
        String prompt = String.format("""
        A partir del texto OCR entre [[[ ]]], extrae y completa SOLO los campos del siguiente JSON. No incluyas explicaciones, encabezados ni comentarios. Devuelve únicamente un JSON válido y bien formado (sin texto adicional). Usa `null` si un dato no está disponible o es ilegible.
        
        Clasifica la factura automáticamente en el campo `"categoria"` usando una de las categorías válidas del régimen simplificado o profesional independiente. Elige la mejor categoría según el texto, entre estas:
        
        [
          "servicios_profesionales", "comisiones", "venta_bienes", "intereses_recibidos",
          "alquileres_cobrados", "otros_ingresos", "ingresos_no_gravables",
          "alquiler_local", "servicios_publicos", "materiales_oficina", "software_licencias",
          "publicidad", "transporte", "comunicaciones", "mantenimiento",
          "honorarios_profesionales", "costos_servicio_terceros", "intereses_creditos",
          "comisiones_bancarias", "depreciacion_equipos", "amortizacion",
          "aporte_pension_voluntaria", "otros_deducibles"
        ]
        
        Asegúrate de que:
        - Todos los campos requeridos estén presentes.
        - El JSON sea sintácticamente correcto.
        - Los valores tengan el tipo de dato correcto.
        - No incluyas campos adicionales fuera del esquema dado.
        
        Formato requerido:
        {
          "consecutivo": "<int>",
          "clave": "<int>",
          "fecha_emision": "<yyyy-mm-dd>",
        
          "cliente": {
            "nombre": "<string>",
            "cedula": "<int>",
            "telefono": "<string>",
            "email": "<string>"
          },
        
          "detalle": [
            {
              "cabys": "<int(13 digits)>",
              "descripcion": "<string>",
              "cantidad": "<float>",
              "unidad": "<string>",
              "precio_unitario": "<float>",
              "descuento": "<float>",
              "impuesto": "<float>",
              "monto_impuesto": "<float>",
              "total": "<float>",
              "categoria": "<string>"
            }
          ],
        
          "totales": {
            "total_servicios_gravados": "<float>",
            "total_servicios_exentos": "<float>",
            "total_servicios_exonerados": "<float>",
            "total_mercancias_gravadas": "<float>",
            "total_mercancias_exentas": "<float>",
            "total_mercancias_exoneradas": "<float>",
            "total_gravado": "<float>",
            "total_exento": "<float>",
            "total_exonerado": "<float>",
            "total_venta": "<float>",
            "total_descuento": "<float>",
            "total_venta_neta": "<float>",
            "total_impuestos": "<float>",
            "total_iva_devueltos": "<float>",
            "total_otros_cargos": "<float>",
            "total_comprobante": "<float>"
          },
        }

        Texto OCR:
        [[[
        %s
        ]]]
        """, invoice);

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
}
