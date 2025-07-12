package com.project.demo.logic.entity.bill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;


@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    public Bill formatAndSave(InputStream inputStream) throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);

        document.getDocumentElement().normalize();

        Bill bill = new Bill();

        // Receptor
        bill.setNombreReceptor(getText(document, "Receptor", "Nombre"));

        System.out.println(bill);
        return bill;
    }

    private String getText(Document doc, String tag) {
        NodeList list = doc.getElementsByTagName(tag);
        return (list.getLength() > 0) ? list.item(0).getTextContent() : null;
    }

    private String getText(Document doc, String parent, String child) {
        NodeList parents = doc.getElementsByTagName(parent);
        if (parents.getLength() > 0) {
            Element parentEl = (Element) parents.item(0);
            NodeList children = parentEl.getElementsByTagName(child);
            return (children.getLength() > 0) ? children.item(0).getTextContent() : null;
        }
        return null;
    }

    private String getText(Document doc, String grandParent, String parent, String child) {
        NodeList grandList = doc.getElementsByTagName(grandParent);
        if (grandList.getLength() > 0) {
            Element grandEl = (Element) grandList.item(0);
            NodeList parentList = grandEl.getElementsByTagName(parent);
            if (parentList.getLength() > 0) {
                Element parentEl = (Element) parentList.item(0);
                NodeList childList = parentEl.getElementsByTagName(child);
                return (childList.getLength() > 0) ? childList.item(0).getTextContent() : null;
            }
        }
        return null;
    }


}
