package com.kasoverskiy.ovchipkaart.pdf;

import com.kasoverskiy.ovchipkaart.OvException;
import com.lowagie.text.DocumentException;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;
import org.xml.sax.InputSource;

import java.io.*;

/**
 * Created by joycollector on 4/4/16.
 */
public class PdfGenerator {

    /**
     * @param is source HTML
     * @param os resulting PDF
     */
    public void createPdf(InputStream is, OutputStream os) {
        try {
            Document document = XMLResource.load(is).getDocument();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(document, null);
            renderer.layout();
            renderer.createPDF(os);
        } catch (DocumentException e) {
            throw new OvException("PDF can't be generated.", e);
        }
    }

}
