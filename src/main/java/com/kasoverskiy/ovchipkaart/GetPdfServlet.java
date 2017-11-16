package com.kasoverskiy.ovchipkaart;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;


/**
 * Created by vadelic on 28.04.2016.
 */
public class GetPdfServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // установить MIME-type и кодировку ответа
        resp.setContentType("application/pdf; charset=UTF8");
        OutputStream os = resp.getOutputStream();

        OvApp ovApp = new OvApp(
                req.getParameter("username"),
                req.getParameter("password"),
                req.getParameter("card"),
                LocalDate.parse(req.getParameter("begin")),
                LocalDate.parse(req.getParameter("end")));
        ByteArrayInputStream is = ovApp.getPdfStream();
        this.copyStream(is, os);
        os.close();
    }


    public void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }
}
