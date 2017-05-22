package me.okx.neimonline;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.okx.neim.token.TokenManager;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BackendHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange ex) {
        Thread thread = new Thread(() -> {
            try {
                String html = handleQuery(ex.getRequestURI().getRawQuery());
                int header;

                if (html == null) {
                    html = "400 Bad Request";
                    header = 400;
                } else {
                    header = 200;
                }

                ex.sendResponseHeaders(header, html.length());
                OutputStream os = ex.getResponseBody();
                os.write(html.getBytes());
                os.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(thread.isAlive()) {
                    try {
                        thread.interrupt();
                        String html = "Timed out";
                        ex.sendResponseHeaders(200, html.length());
                        OutputStream os = ex.getResponseBody();
                        os.write(html.getBytes());
                        os.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 10000);
    }

    public String handleQuery(String query) {
        System.out.println("[BACKEND] Recieved query " + query);
        if (query == null) {
            return null;
        }
        Map<String, String> vals = getParams(query);

        String code = vals.getOrDefault("code", "");
        String input = vals.getOrDefault("input", "");

        if(code.isEmpty()) {
            System.out.println("[BACKEND] [WARNING] Code is empty");
        } if(input.isEmpty()) {
            System.out.println("[BACKEND] [WARNING] Input is empty");
        }

        //input += "\n";

        TokenManager tm = new TokenManager();
        tm.registerTokens(100);

        try {
            InputStream is = new ByteArrayInputStream( input.getBytes("UTF-8") );

            tm.getInput().setInputStream(is);

            System.out.println("Code: " + code);
            System.out.println("Input: " + input);

            tm.handleTokens(code);

            System.out.println("[BACKEND] Fired " + tm.stackAsString());
            return tm.stackAsString();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }
    }

    private Map<String, String> getParams(String query) {
        Map<String, String> vals = new HashMap<>();

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
}
