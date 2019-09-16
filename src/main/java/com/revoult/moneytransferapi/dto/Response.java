package com.revoult.moneytransferapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.revoult.moneytransferapi.constant.ResponseCode;

public class Response {
    private String statusCode;
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

    public Response(String statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }

    public Response(ResponseCode responseCode) {
        this.statusCode = responseCode.getCode();
        this.description = responseCode.getDescription();
    }

    public Response(ResponseCode responseCode, Object data) {
        this.statusCode = responseCode.getCode();
        this.description = responseCode.getDescription();
        this.data = data;
    }

    public Response(String statusCode, String description, Object data) {
        this.statusCode = statusCode;
        this.description = description;
        this.data = data;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
