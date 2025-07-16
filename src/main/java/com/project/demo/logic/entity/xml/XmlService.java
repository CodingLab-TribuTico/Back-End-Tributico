package com.project.demo.logic.entity.xml;

import com.project.demo.logic.entity.detailsBill.DetailsBill;
import com.project.demo.logic.entity.electronicBill.ElectronicBill;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class XmlService {


    public ElectronicBill formatAndSave(InputStream inputStream) throws Exception {
        try {
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(inputStream);
            document.getDocumentElement().normalize();

            ElectronicBill electronicBill = new ElectronicBill();

            String consecutive = getText(document, "NumeroConsecutivo");
            electronicBill.setConsecutive(Long.parseLong(consecutive));

            String issueDate = getText(document, "FechaEmision");
            if (issueDate != null && !issueDate.isEmpty()) {
                String datePart = issueDate.split("T")[0];
                electronicBill.setIssueDate(LocalDate.parse(datePart));
            }

            String code = getText(document, "CodigoActividad");
            electronicBill.setCode(code != null && !code.isEmpty() ?
                    Integer.parseInt(code) : 0);

            electronicBill.setDetails(extractDetails(document));

            return electronicBill;
        } catch (Exception e) {
            System.err.println("Error procesando XML: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private List<DetailsBill> extractDetails(Document document) {
        List<DetailsBill> detailList = new ArrayList<>();
        NodeList nodeList = document.getElementsByTagName("LineaDetalle");

        for (int i = 0; i < nodeList.getLength(); i++) {
            try {
                Element element = (Element) nodeList.item(i);
                DetailsBill detail = new DetailsBill();

                String codigo = getText(element, "Codigo");
                detail.setDetailCode(Long.parseLong(codigo));

                detail.setDetailDescription(getText(element, "Detalle"));
                detail.setQuantity(Double.parseDouble(getText(element, "Cantidad")));
                detail.setUnitPrice(Double.parseDouble(getText(element, "PrecioUnitario")));

                String unidad = getText(element, "UnidadMedida");
                detail.setUnit("Unid".equals(unidad) ? 1 : 0);

                NodeList taxList = element.getElementsByTagName("Impuesto");
                if (taxList.getLength() > 0) {
                    Element taxElement = (Element) taxList.item(0);
                    detail.setTax(Double.parseDouble(getText(taxElement, "Tarifa")));
                    detail.setTaxAmount(Double.parseDouble(getText(taxElement, "Monto")));
                }

                detail.setTotal(Double.parseDouble(getText(element, "MontoTotalLinea")));

                detailList.add(detail);
            } catch (Exception e) {
                System.err.println("Error procesando línea " + i + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        return detailList;
    }

    private String getText(Document doc, String tag) {
        NodeList list = doc.getElementsByTagName(tag);
        return (list.getLength() > 0) ? list.item(0).getTextContent() : null;
    }

    private String getText(Element parent, String childTag) {
        NodeList children = parent.getElementsByTagName(childTag);
        return (children.getLength() > 0) ? children.item(0).getTextContent().trim() : null;
    }

}
