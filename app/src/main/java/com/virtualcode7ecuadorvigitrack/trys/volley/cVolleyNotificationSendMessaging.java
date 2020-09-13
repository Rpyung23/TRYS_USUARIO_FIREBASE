package com.virtualcode7ecuadorvigitrack.trys.volley;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class cVolleyNotificationSendMessaging
{
    private JsonObjectRequest mJsonObject;
    private RequestQueue mRequestQueue;
    private Context context;

    public cVolleyNotificationSendMessaging(Context context_)
    {
        this.context = context_;
    }

    public void sendNotificationVolley(String title,String body,String tokenphone)
    {
        String url ="https://fcm.googleapis.com/fcm/send";
        mJsonObject = new JsonObjectRequest(url, createJSON(title, body, tokenphone),
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                try {
                    if (response.getInt("success")==1)
                    {
                        Toasty.success(context,"MENSAJE ENVIADO",Toasty.LENGTH_LONG)
                                .show();
                    }else
                    {
                        Toasty.error(context,"SERVER NOTIFICATION ERROR MENSAJE",Toasty.LENGTH_LONG)
                                .show();
                    }
                } catch (JSONException e)
                {
                    Toasty.error(context,"TRY CATCH  MENSAJE: "+e.getMessage(),Toasty.LENGTH_LONG)
                            .show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e("VOLLEY","ERROR RESPONDE MESSAGING");
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("Content-Type","application/json; charset=utf-8");
                hashMap.put("Authorization","key=AAAAzly-y3E:APA91bGuoJW3nk44A0Z-Px9xf6wXMDKXlS4iLrVkUTNkFhP10OFrR2d9wvorM-PADwGc45HMCjB3EhVQae6-HDycwtsD4xWCwWWxDwuhEFQI5Meqcq93-QEvDcif4TIJwb1hoP6Znkj6");
                return hashMap;
            }
        };

        mRequestQueue = Volley.newRequestQueue(context);
        mRequestQueue.add(mJsonObject);
    }

    private JSONObject createJSON(String title, String body, String tokenphone)
    {
        JSONObject jsonObject1 = new JSONObject();

        HashMap<String,Object> hashMapData = new HashMap<>();
        hashMapData.put("tipo",109);

        HashMap<String,String> hashMapNotifi = new HashMap<>();
        hashMapNotifi.put("title",title);
        hashMapNotifi.put("body",body);

        try {
            jsonObject1.put("to",tokenphone);
            jsonObject1.put("data",new JSONObject(hashMapData));
            jsonObject1.put("notification",new JSONObject(hashMapNotifi));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject1;
    }

}
