package me.okx.neimonline;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class Main {
    public static int PORT = 81;

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/neim", new FrontendHandler());
        server.createContext("/api/neim", new BackendHandler());
        server.start();
        System.out.println("[MAIN] Server started.");

        System.out.println(new BackendHandler().handleQuery("input=6666666&code=\uD835\uDC25\uD835\uDC08Γ6Θℝ)₁>\uD835\uDD54"));
    }
}
