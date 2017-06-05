package me.okx.neimonline;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;
import org.unbescape.html.HtmlEscape;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class FrontendHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange ex) throws IOException {
        Map<String, String> params = getParams(ex.getRequestURI().getRawQuery());

        String code = HtmlEscape.escapeHtml5(params.getOrDefault("code", ""));
        String input = HtmlEscape.escapeHtml5(params.getOrDefault("input", ""));

        System.out.println("CODE: " + code);

        InputStream in = getClass().getResourceAsStream("/neim.html");
        String html = IOUtils.toString(in, "UTF-8");

        html = String.format(html, code, input);
        ex.sendResponseHeaders(200, html.length());
        OutputStream os = ex.getResponseBody();
        os.write(html.getBytes());
        os.close();
    }

    private Map<String, String> getParams(String query) {
        Map<String, String> vals = new HashMap<>();
        if(query == null) {
            return vals;
        }

        System.out.println("+ " + query + " +");

        for(String a : query.split("&")) {
            String[] args = a.split("=");
            try {
                System.out.println("A: " + args[1]);
                System.out.println("D: " + URLDecoder.decode(args[1], "UTF-8"));
                vals.put(args[0], URLDecoder.decode(args[1], "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                vals.put(args[0], args[1]);
            }
        }

        return vals;
    }
}