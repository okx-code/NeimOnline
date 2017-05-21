package me.okx.neimonline;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class FrontendHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange ex) throws IOException {
        Map<String, String> params = getParams(ex.getRequestURI().getQuery());

        String code = params.getOrDefault("code", "");
        String input = params.getOrDefault("input", "");

        String html = "<!DOCTYPE html><html><head><script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script><script>$( document ).ready(function(){$(\".submit\").click(function(){var button=$(this); var code=encodeURIComponent($(\".code\").val()); var input=encodeURIComponent($(\".input\").val()); var fullInput; if(input==\"\"){fullInput=\"\";}else{fullInput=\"&input=\" + input;}var start=Date.now() var output=$(\".output\"); output.css(\"cursor\", \"progress\"); $.get(\"/api/neim?code=\" + code + fullInput, function( data ){var end=Date.now() $(\".timer\").html(\"Took \" + (end-start) + \"ms.\"); output.val(data); output.css(\"cursor\", \"auto\");});}); $(\".link\").click(function(){var code=encodeURIComponent($(\".code\").val()); var input=encodeURIComponent($(\".input\").val()); var text=\"http://\" + window.location.hostname + \":80\" + window.location.pathname + \"?code=\" + code + \"&input=\" + input; //window.location.href=text; window.prompt(\"Copy to clipboard: Ctrl+C, Enter\", text);});});</script><body><span>Code:</span><br><textarea cols=32 rows=8 class=\"code\"></textarea><br><span>Input:</span><br><textarea cols=32 rows=8 class=\"input\"></textarea><br><button type=\"button\" class=\"submit\">Submit</button><button type=\"button\" class=\"link\">Permalink</button><br/><span>Output:</span><br><textarea cols=32 rows=8 class=\"output\" readonly></textarea><br/><span class=\"timer\"></span></body></html>";

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

        for(String a : query.split("&")) {
            String[] args = a.split("=");
            try {
                vals.put(args[0], URLDecoder.decode(args[1], "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                vals.put(args[0], args[1]);
            }
        }

        return vals;
    }

    public static String escapeHTML(String s) {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}