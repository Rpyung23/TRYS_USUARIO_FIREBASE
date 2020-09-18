package com.virtualcode7ecuadorvigitrack.trys.volley;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.models.cSolicitudTaxi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class cVolleyNotification
{
    private Context context;
    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;

    public cVolleyNotification(Context context_)
    {
        this.context = context_;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void createNotificationServer(cSolicitudTaxi OsolicitudTaxi)
    {

        String url = getContext()
                .getString(R.string.url_create_FCM)+"?fecha="+OsolicitudTaxi.getFecha()
                +"&id_client="+OsolicitudTaxi.getId_token_client()
                +"&id_driver="+OsolicitudTaxi.getId_token_drivers()
                +"&lat_end="+OsolicitudTaxi.getLatitud_end()
                +"&lat_start="+OsolicitudTaxi.getLatitud_start()
                +"&long_end="+OsolicitudTaxi.getLongitud_end()
                +"&long_start="+OsolicitudTaxi.getLongitud_start()
                +"&price="+OsolicitudTaxi.getPrice()
                +"&status="+OsolicitudTaxi.getStatus()
                +"&token_client_phone="+OsolicitudTaxi.getToken_client_phone()
                +"&token_driver_phone="+OsolicitudTaxi.getToken_driver_phone()
                +"&tipo=1&time="+OsolicitudTaxi.getTime()
                +"&distance="+OsolicitudTaxi.getDistance();
        Log.i("URLFIREPUSH",url);
        jsonObjectRequest = new JsonObjectRequest(url
                , null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    if (response.getString("status").equals("ok"))
                    {
                        Toast.makeText(getContext(), "NOTIFICAION ENVIADA"
                                , Toast.LENGTH_SHORT).show();
                    }else
                        {
                            Toast.makeText(getContext(), "ERROR EN NOTIFICACION PUSH"
                                    , Toast.LENGTH_SHORT).show();
                        }
                } catch (JSONException e)
                {
                    Toast.makeText(getContext(), "TRYCATH : "+e.getMessage()
                            , Toast.LENGTH_SHORT).show();
                    if (requestQueue!=null)
                    {
                        requestQueue.getCache().clear();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                if (requestQueue!=null)
                {
                    requestQueue.getCache().clear();
                }
                Toast.makeText(getContext(), "VOLLEY ERROR : "+error.getMessage()
                        , Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }


    /** METODO CLOUD MESSAGING FIREBASE ENVIO**/
    public void createNotificationServerFirebase(cSolicitudTaxi OsoSolicitudTaxi)
    {
        String url ="https://fcm.googleapis.com/fcm/send";
        jsonObjectRequest = new JsonObjectRequest(url, createJSONSEND(OsoSolicitudTaxi), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                try {
                    if (response.getInt("success")==1)
                    {
                        Toasty.success(context,"SOLICITUD ENVIADA",Toasty.LENGTH_LONG)
                                .show();
                    }else
                        {
                            Toasty.error(context,"SERVER NOTIFICATION ERROR SOLICITUD",Toasty.LENGTH_LONG)
                                    .show();
                        }
                } catch (JSONException e)
                {
                    Toasty.error(context,"TRY CATCH : "+e.getMessage(),Toasty.LENGTH_LONG)
                            .show();
                }
            }
        }
        , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toasty.error(context,"SERVER ERROR RESPONSE",Toasty.LENGTH_LONG)
                        .show();
                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
                //Log.e("VOLEYERROR",error.getMessage().toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("Content-Type","application/json; charset=utf-8");
                hashMap.put("Authorization","key=AAAAzly-y3E:APA91bGuoJW3nk44A0Z-Px9xf6wXMDKXlS4iLrVkUTNkFhP10OFrR2d9wvorM-PADwGc45HMCjB3EhVQae6-HDycwtsD4xWCwWWxDwuhEFQI5Meqcq93-QEvDcif4TIJwb1hoP6Znkj6");
                return hashMap;
            }
        };
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

    private JSONObject createJSONSEND(cSolicitudTaxi osoSolicitudTaxi)
    {
        JSONObject jsonObject1 = new JSONObject();
        HashMap<String,Object> hashMapData = new HashMap<>();
        hashMapData.put("tipo",osoSolicitudTaxi.getTipo());
        hashMapData.put("latitud_start",osoSolicitudTaxi.getLatitud_start());
        hashMapData.put("longitud_start",osoSolicitudTaxi.getLongitud_start());
        hashMapData.put("latitud_end",osoSolicitudTaxi.getLatitud_end());
        hashMapData.put("longitud_end",osoSolicitudTaxi.getLongitud_end());
        hashMapData.put("price",osoSolicitudTaxi.getPrice());
        hashMapData.put("id_client",osoSolicitudTaxi.getId_token_client());
        hashMapData.put("id_drivers",osoSolicitudTaxi.getId_token_drivers());
        hashMapData.put("token_client_phone",osoSolicitudTaxi.getToken_client_phone());
        hashMapData.put("token_driver_phone",osoSolicitudTaxi.getToken_driver_phone());
        hashMapData.put("fecha",osoSolicitudTaxi.getFecha());
        hashMapData.put("status",osoSolicitudTaxi.getStatus());
        hashMapData.put("time",osoSolicitudTaxi.getTime());
        hashMapData.put("distance",osoSolicitudTaxi.getDistance());

        HashMap<String,String> hashMapNotifi = new HashMap<>();
        hashMapNotifi.put("title","NUEVA SOLICITUD DE CARRERA");
        hashMapNotifi.put("click_action","PreviewSolicitudActivity");

        /** SIN ESPECIFICAR EL DESTINO**/

        if (osoSolicitudTaxi.getLatitud_end()==0 && osoSolicitudTaxi.getLongitud_end() ==0)
        {
            if (Geocoder.isPresent())
            {
                List<Address>addresses = null;
                try {
                    addresses = new Geocoder(context).getFromLocation(osoSolicitudTaxi.getLatitud_start()
                            ,osoSolicitudTaxi.getLongitud_start(),1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                hashMapNotifi.put("body","UBICACIÓN INICIAL : "+addresses.get(0).getAddressLine(0)
                        +" SIN ESPECIFICAR DESTINO \n DISTANCIA : SIN ESPECIFICAR"
                        +" \n TIEMPO : SIN ESPECIFICAR");
            }
        }else
            {
                if (Geocoder.isPresent())
                {
                    List<Address>addresses = null;
                    List<Address>addressesEnd = null;
                    try {
                        addresses = new Geocoder(context).getFromLocation(osoSolicitudTaxi.getLatitud_start()
                                ,osoSolicitudTaxi.getLongitud_start(),1);
                        addressesEnd = new Geocoder(context).getFromLocation(osoSolicitudTaxi.getLatitud_end()
                                ,osoSolicitudTaxi.getLongitud_end(),1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    hashMapNotifi.put("body","UBICACIÓN INICIAL : "+addresses.get(0).getAddressLine(0)
                            +" DESTINO : "+addressesEnd.get(0).getAddressLine(0)+" \n DISTANCIA : "
                            +osoSolicitudTaxi.getDistance()+" \n TIEMPO : "+osoSolicitudTaxi.getTime()+" minutos");
                }
            }



        try
        {
            jsonObject1.put("to",osoSolicitudTaxi.getToken_driver_phone());
            jsonObject1.put("data",new JSONObject(hashMapData));
            jsonObject1.put("notification",new JSONObject(hashMapNotifi));
        }catch (JSONException e)
        {
            Log.e("ERRORJSON",e.getMessage());
        }

        Log.e("JSON",jsonObject1.toString());
        return jsonObject1;
    }
}
