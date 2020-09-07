package com.virtualcode7ecuadorvigitrack.trys.models;

public class cMessaging
{
    private int tipo_right_left;
    private String id_reciver_send;
    private String messaging;
    private String url_perfil_driver;

    private String url_perfil_client;



    private String type_msm;


    public cMessaging() {
    }


    public String getType_msm() {
        return type_msm;
    }

    public void setType_msm(String type_msm) {
        this.type_msm = type_msm;
    }

    public String getUrl_perfil_driver() {
        return url_perfil_driver;
    }

    public void setUrl_perfil_driver(String url_perfil_driver) {
        this.url_perfil_driver = url_perfil_driver;
    }

    public String getUrl_perfil_client() {
        return url_perfil_client;
    }

    public void setUrl_perfil_client(String url_perfil_client) {
        this.url_perfil_client = url_perfil_client;
    }


    public int getTipo_right_left() {
        return tipo_right_left;
    }

    public void setTipo_right_left(int tipo_right_left) {
        this.tipo_right_left = tipo_right_left;
    }

    public String getId_reciver_send() {
        return id_reciver_send;
    }

    public void setId_reciver_send(String id_reciver_send) {
        this.id_reciver_send = id_reciver_send;
    }

    public String getMessaging() {
        return messaging;
    }

    public void setMessaging(String messaging) {
        this.messaging = messaging;
    }}
