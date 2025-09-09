package com.example.carins.web.dto;

public class ErrorResponse {
    private String message;
    private String field;
    private int status;

    public ErrorResponse() {}

    public ErrorResponse(String message, String field, int status) {
        this.message = message;
        this.field = field;
        this.status = status;
    }

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
