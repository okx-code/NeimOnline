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

        String html = "<!DOCTYPE html><script src=https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js></script><script>$(document).ready(function(){$(\".submit\").click(function(){$(this);var o,n=encodeURIComponent($(\".code\").val()),t=encodeURIComponent($(\".input\").val());o=\"\"==t?\"\":\"&input=\"+t;var e=Date.now(),c=$(\".output\");c.css(\"cursor\",\"progress\"),$.get(\"/api/neim?code=\"+n+o,function(o){var n=Date.now();$(\".timer\").html(\"Took \"+(n-e)+\"ms.\"),c.val(o),c.css(\"cursor\",\"auto\")})}),$(\".link\").click(function(){var o=encodeURIComponent($(\".code\").val()),n=encodeURIComponent($(\".input\").val()),t=\"http://\"+window.location.hostname+\":80\"+window.location.pathname+\"?code=\"+o+\"&input=\"+n;window.prompt(\"Copy to clipboard: Ctrl+C, Enter\",t)})})</script><span>Code:</span><br><textarea class=code cols=32 rows=8></textarea><br><span>Input:</span><br><textarea class=input cols=32 rows=8></textarea><br><button class=submit type=button>Submit</button> <button class=link type=button>Permalink</button><br><span>Output:</span><br><textarea class=output cols=32 rows=8 readonly></textarea><br><span class=timer></span>";
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