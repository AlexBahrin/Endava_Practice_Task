package com.example.carins.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response data")
public class ErrorResponse {
    @Schema(description = "Error message", example = "Car not found")
    private String message;
    @Schema(description = "Field that caused the error", example = "description")
    private String field;
    @Schema(description = "HTTP status code", example = "404")
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
