package com.hotel.pos.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
