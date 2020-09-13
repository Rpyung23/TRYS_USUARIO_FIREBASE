package com.virtualcode7ecuadorvigitrack.trys.models;

public class cHistoryBooking
{
    private String fecha;
    private Double price;
    private Double lat_ini;
    private Double long_ini;
    private Double lat_end;
    private Double long_end;
    private String plate;

    public cHistoryBooking() {
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getLat_ini() {
        return lat_ini;
    }

    public void setLat_ini(Double lat_ini) {
        this.lat_ini = lat_ini;
    }

    public Double getLong_ini() {
        return long_ini;
    }

    public void setLong_ini(Double long_ini) {
        this.long_ini = long_ini;
    }

    public Double getLat_end() {
        return lat_end;
    }

    public void setLat_end(Double lat_end) {
        this.lat_end = lat_end;
    }

    public Double getLong_end() {
        return long_end;
    }

    public void setLong_end(Double long_end) {
        this.long_end = long_end;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }
}
