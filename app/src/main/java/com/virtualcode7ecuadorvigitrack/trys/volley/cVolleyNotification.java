package com.virtualcode7ecuadorvigitrack.trys.volley;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.models.cSolicitudTaxi;

import org.json.JSONException;
import org.json.JSONObject;

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
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getContext(), "VOLLEY ERROR : "+error.getMessage()
                        , Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }
}
