import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static Map<String, String[]> linkMap = new HashMap<>();

    public static void main(String[] args) throws Exception {
        linkMap.put("test", new String[]{ "code", "input" });
        linkMap.put("121877", new String[] { "𝐥𝐈Γ6Θℝ)₁>𝕔", "366" });

        HttpServer server = HttpServer.create(new InetSocketAddress(81), 0);
        server.createContext("/neim", new FrontendHandler());
        server.createContext("/api/neim", new BackendHandler());
        server.start();
        System.out.println("[MAIN] Server started.");

        System.out.println(new BackendHandler().handleQuery("input=6666666&code=\uD835\uDC25\uD835\uDC08Γ6Θℝ)₁>\uD835\uDD54"));
    }
}
