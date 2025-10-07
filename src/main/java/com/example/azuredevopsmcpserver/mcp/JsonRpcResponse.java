package com.example.azuredevopsmcpserver.mcp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonRpcResponse extends JsonRpcMessage {
    @JsonProperty("result")
    private Object result;

    @JsonProperty("error")
    private JsonRpcError error;

    public JsonRpcResponse() {
    }

    public JsonRpcResponse(Object id, Object result) {
        super(id);
        this.result = result;
    }

    public JsonRpcResponse(Object id, JsonRpcError error) {
        super(id);
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public JsonRpcError getError() {
        return error;
    }

    public void setError(JsonRpcError error) {
        this.error = error;
    }
}