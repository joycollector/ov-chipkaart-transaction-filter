package com.kasoverskiy.ovchipkaart.pdf;

import com.kasoverskiy.ovchipkaart.model.Transaction;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;
import java.util.List;

/**
 * Created by joycollector on 4/7/16.
 */
public class HtmlGenerator {

    /**
     * @param transactions list of transactions
     */
    public ByteArrayInputStream createHtml(List<Transaction> transactions) throws IOException {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("XHTML");
        templateResolver.setCharacterEncoding("UTF-8");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addDialect(new Java8TimeDialect());
        templateEngine.setTemplateResolver(templateResolver);

        Context ctx = new Context();
        ctx.setVariable("transactions", transactions);
        String process = templateEngine.process("report.xhtml", ctx);
        return new ByteArrayInputStream(process.getBytes("UTF-8"));
    }

}
