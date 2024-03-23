package com.example.notificationapp.models;

public class Model_code_pin {
    String id;
    String code;
    public Model_code_pin() {
    }

    public Model_code_pin(String id, String code) {
        this.id = id;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
