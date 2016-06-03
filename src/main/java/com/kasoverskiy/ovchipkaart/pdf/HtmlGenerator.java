package com.kasoverskiy.ovchipkaart.pdf;

import com.kasoverskiy.ovchipkaart.OvException;
import com.kasoverskiy.ovchipkaart.model.Transaction;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by joycollector on 4/7/16.
 */
public class HtmlGenerator {

    /**
     * @param transactions list of transactions
     * @param personalInfo
     * @param beginPeriod
     * @param endPeriod
     */
    public ByteArrayInputStream createHtml(List<Transaction> transactions, Map<String, String> personalInfo,
                                           LocalDate beginPeriod, LocalDate endPeriod) {
        try {
            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setTemplateMode("XHTML");
            templateResolver.setCharacterEncoding("UTF-8");

            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.addDialect(new Java8TimeDialect());
            templateEngine.setTemplateResolver(templateResolver);

            Context ctx = new Context();

            ctx.setVariable("personalInfo", personalInfo);
            ctx.setVariable("beginPeriod", beginPeriod);
            ctx.setVariable("endPeriod", endPeriod);
            ctx.setVariable("transactions", transactions);
            ctx.setVariable("amount", transactions.stream().mapToDouble(s -> s.getAmount()).toArray());

            String process = templateEngine.process("report.xhtml", ctx);
            return new ByteArrayInputStream(process.getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e) {
            throw new OvException("HTML can't be generated.", e);
        }
    }

}
