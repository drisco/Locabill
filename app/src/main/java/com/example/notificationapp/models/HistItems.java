package com.example.notificationapp.models;


import java.util.List;

public class HistItems {
    private String cityName;
    private List<Model_ticket> tenantList;

    public HistItems() {
    }

    public HistItems(String cityName, List<Model_ticket> tenantList) {
        this.cityName = cityName;
        this.tenantList = tenantList;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<Model_ticket> getTenantList() {
        return tenantList;
    }

    public void setTenantList(List<Model_ticket> tenantList) {
        this.tenantList = tenantList;
    }
}

