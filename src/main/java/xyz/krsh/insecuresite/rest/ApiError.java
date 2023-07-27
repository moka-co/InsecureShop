package xyz.krsh.insecuresite.rest;

import org.springframework.http.HttpStatus;

public class ApiError {
    private Integer status;
    private String error;

    public ApiError(String e) {
        error = e;
    }

    public ApiError(String e, HttpStatus status) {
        error = e;
        this.status = status.value();
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public void setStatus(HttpStatus status) {
        this.status = status.value();
    }

    public void setError(String e) {
        error = e;
    }

}
