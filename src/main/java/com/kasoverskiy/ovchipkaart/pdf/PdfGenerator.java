package com.kasoverskiy.ovchipkaart.pdf;

import com.kasoverskiy.ovchipkaart.OvException;
import com.lowagie.text.DocumentException;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.OutputStream;

/**
 * Created by joycollector on 4/4/16.
 */
public class PdfGenerator {

    public void createPdf(Document document, OutputStream os) {
        try {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(document, null);
            renderer.layout();
            renderer.createPDF(os);
        } catch (DocumentException e) {
            throw new OvException("PDF can't be generated.", e);
        }
    }

}
