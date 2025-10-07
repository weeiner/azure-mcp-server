package com.example.azuredevopsmcpserver.azuredevops.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkItem {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("rev")
    private Integer rev;

    @JsonProperty("fields")
    private Map<String, Object> fields;

    @JsonProperty("url")
    private String url;

    public WorkItem() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRev() {
        return rev;
    }

    public void setRev(Integer rev) {
        this.rev = rev;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return fields != null ? (String) fields.get("System.Title") : null;
    }

    public String getWorkItemType() {
        return fields != null ? (String) fields.get("System.WorkItemType") : null;
    }

    public String getState() {
        return fields != null ? (String) fields.get("System.State") : null;
    }

    public String getAssignedTo() {
        if (fields != null && fields.containsKey("System.AssignedTo")) {
            Object assignedTo = fields.get("System.AssignedTo");
            if (assignedTo instanceof Map) {
                return (String) ((Map<?, ?>) assignedTo).get("displayName");
            }
            return (String) assignedTo;
        }
        return null;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class WorkItemsResponse {
    @JsonProperty("value")
    private List<WorkItem> value;

    @JsonProperty("count")
    private int count;

    public List<WorkItem> getValue() {
        return value;
    }

    public void setValue(List<WorkItem> value) {
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}