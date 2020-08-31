package com.virtualcode7ecuadorvigitrack.trys.models;

public class cUser
{
    private String id_token_;
    private String name;
    private String phone;
    private String photo_url;
    private String email;
    private String placa;

    public cUser(){}

    public String getId_token_() {
        return id_token_;
    }

    public void setId_token_(String id_token_) {
        this.id_token_ = id_token_;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
}
