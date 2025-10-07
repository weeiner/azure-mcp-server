package com.example.azuredevopsmcpserver.mcp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class StdioServer implements CommandLineRunner {

    private final McpServer mcpServer;

    public StdioServer(McpServer mcpServer) {
        this.mcpServer = mcpServer;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if we should run in stdio mode
        boolean stdioMode = false;
        for (String arg : args) {
            if ("--stdio".equals(arg)) {
                stdioMode = true;
                break;
            }
        }

        if (!stdioMode) {
            // If not in stdio mode, just return and let Spring Boot continue normally
            return;
        }

        runStdioServer();
    }

    private void runStdioServer() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    String response = mcpServer.handleMessage(line);
                    if (response != null && !response.isEmpty()) {
                        System.out.println(response);
                        System.out.flush();
                    }
                } catch (Exception e) {
                    // Log to stderr instead of stdout to avoid corrupting the JSON-RPC
                    // communication
                    System.err.println("Error processing message: " + e.getMessage());

                    // Send error response
                    String errorResponse = createErrorResponse(-32603, "Internal error: " + e.getMessage());
                    System.out.println(errorResponse);
                    System.out.flush();
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from stdin: " + e.getMessage());
        }
    }

    private String createErrorResponse(int code, String message) {
        return "{\"jsonrpc\":\"2.0\",\"id\":null,\"error\":{\"code\":" + code + ",\"message\":\"" +
                message.replace("\"", "\\\"") + "\"}}";
    }
}