package org.jboss.fuse.tnb.salesforce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Account {

    @JsonProperty(value = "Id")
    String id;
    @JsonProperty(value = "Name")
    String name;
    @JsonProperty(value = "Phone")
    String phone;

    public Account(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public Account() {
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String toString() {
        return "Account(id=" + this.id + ", name=" + this.name + ", phone=" + this.phone + ")";
    }
}
