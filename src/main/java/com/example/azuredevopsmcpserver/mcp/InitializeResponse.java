package com.example.azuredevopsmcpserver.mcp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InitializeResponse {
    @JsonProperty("protocolVersion")
    private String protocolVersion;

    @JsonProperty("capabilities")
    private Map<String, Object> capabilities;

    @JsonProperty("serverInfo")
    private ServerInfo serverInfo;

    public InitializeResponse() {
        this.protocolVersion = "2025-06-18";
        this.capabilities = new HashMap<>();
        this.serverInfo = new ServerInfo();
        this.serverInfo.setName("azure-devops-mcp-server");
        this.serverInfo.setVersion("1.0.0");

        // Set server capabilities
        Map<String, Object> tools = new HashMap<>();
        tools.put("listChanged", true);
        this.capabilities.put("tools", tools);
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public Map<String, Object> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Map<String, Object> capabilities) {
        this.capabilities = capabilities;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ServerInfo {
        @JsonProperty("name")
        private String name;

        @JsonProperty("version")
        private String version;

        public ServerInfo() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}