import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.unbescape.html.HtmlEscape;

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

        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>\n" +
                "<script>\n" +
                "$( document ).ready(function() {\n" +
                "  $(\".submit\").click(function() {\n" +
                "      var button = $(this);\n" +
                "      var code = encodeURIComponent($(\".code\").val());\n" +
                "      var input =  encodeURIComponent($(\".input\").val());\n" +
                "      var fullInput;\n" +
                "      if(input == \"\") {\n" +
                "        fullInput = \"\";\n" +
                "      } else {\n" +
                "        fullInput = \"&input=\" + input;\n" +
                "      }\n" +
                "    \n" +
                "      $.get(\"/api/neim?code=\" + code + fullInput, function( data ) {\n" +
                "        $(\".output\").val(data);\n" +
                "      });\n" +
                "  });\n" +
                "  $(\".link\").click(function() {\n" +
                "    var code = encodeURIComponent($(\".code\").val());\n" +
                "    var input =  encodeURIComponent($(\".input\").val());\n" +
                "    var text = window.location.hostname + \":81\" + window.location.pathname + \"?code=\" + code + \"&input=\" + input;\n" +
                "    //window.location.href = text;\n" +
                "    window.prompt(\"Copy to clipboard: Ctrl+C, Enter\", text);\n" +
                "  });\n" +
                "});\n" +
                "</script>\n" +
                "<body>\n" +
                "<span>Code:</span><br>\n" +
                "<textarea cols=32 rows=8 class=\"code\">" + HtmlEscape.escapeHtml5(code) + "</textarea><br>\n" +
                "<span>Input:</span><br>\n" +
                "<textarea cols=32 rows=8 class=\"input\">" + HtmlEscape.escapeHtml5(input) + "</textarea><br>\n" +
                "<button type=\"button\" class=\"submit\">Submit</button>\n" +
                "<button type=\"button\" class=\"link\">Permalink</button>\n" +
                "<br/>\n" +
                "<span>Output:</span><br>\n" +
                "<textarea cols=32 rows=8 class=\"output\" readonly></textarea>\n" +
                "</body>\n" +
                "</html>";

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