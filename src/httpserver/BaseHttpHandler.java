package httpserver;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected static void sendText(HttpExchange exchange,
                                   String responseString,
                                   int responseCode) throws IOException {
        exchange.sendResponseHeaders(responseCode, 0);
        exchange.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        exchange.getResponseBody().write(responseString.getBytes(DEFAULT_CHARSET));
        exchange.close();
    }

    protected static void sendAnswerJson(HttpExchange exchange,
                                         String responseString) throws IOException {
        exchange.sendResponseHeaders(200, responseString.length());
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.getResponseBody().write(responseString.getBytes(DEFAULT_CHARSET));
        exchange.close();
    }

    protected static void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }

    protected static void sendHasInteractions(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }
}
