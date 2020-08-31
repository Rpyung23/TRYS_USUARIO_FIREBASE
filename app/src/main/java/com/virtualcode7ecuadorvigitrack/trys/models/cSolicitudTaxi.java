package com.virtualcode7ecuadorvigitrack.trys.models;

public class cSolicitudTaxi
{
    private String id_token_client;
    private String id_token_drivers;
    private double latitud_start;
    private double longitud_start;
    private double latitud_end;
    private double longitud_end;
    private double price;
    private String status;
    private String fecha;

    private double distance;
    private double time;

    private String token_client_phone;
    private String token_driver_phone;


    public cSolicitudTaxi() {}

    public String getId_token_client() {
        return id_token_client;
    }

    public void setId_token_client(String id_token_client) {
        this.id_token_client = id_token_client;
    }

    public String getId_token_drivers() {
        return id_token_drivers;
    }

    public void setId_token_drivers(String id_token_drivers) {
        this.id_token_drivers = id_token_drivers;
    }

    public double getLatitud_start() {
        return latitud_start;
    }

    public void setLatitud_start(double latitud_start) {
        this.latitud_start = latitud_start;
    }

    public double getLongitud_start() {
        return longitud_start;
    }

    public void setLongitud_start(double longitud_start) {
        this.longitud_start = longitud_start;
    }

    public double getLatitud_end() {
        return latitud_end;
    }

    public void setLatitud_end(double latitud_end) {
        this.latitud_end = latitud_end;
    }

    public double getLongitud_end() {
        return longitud_end;
    }

    public void setLongitud_end(double longitud_end) {
        this.longitud_end = longitud_end;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken_client_phone() {
        return token_client_phone;
    }

    public void setToken_client_phone(String token_client_phone) {
        this.token_client_phone = token_client_phone;
    }

    public String getToken_driver_phone() {
        return token_driver_phone;
    }

    public void setToken_driver_phone(String token_driver_phone) {
        this.token_driver_phone = token_driver_phone;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
