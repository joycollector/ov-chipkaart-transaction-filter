package com.kasoverskiy.ovchipkaart.pdf;

import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * Created by joycollector on 4/4/16.
 */
public class PdfGeneratorTest {

    @Test
    public void testCreatePdf() throws Exception {
        InputStream inputStream = PdfGeneratorTest.class.getResourceAsStream("/report.html");
        Document html = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        PdfGenerator pdfGenerator = new PdfGenerator();
    }
}