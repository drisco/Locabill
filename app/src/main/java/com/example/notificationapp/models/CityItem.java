package com.example.notificationapp.models;

import java.util.List;

public class CityItem {
    private String cityName;
    private List<Model_tenant> tenantList;

    public CityItem() {
    }

    public CityItem(String cityName, List<Model_tenant> tenantList) {
        this.cityName = cityName;
        this.tenantList = tenantList;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<Model_tenant> getTenantList() {
        return tenantList;
    }

    public void setTenantList(List<Model_tenant> tenantList) {
        this.tenantList = tenantList;
    }
}
