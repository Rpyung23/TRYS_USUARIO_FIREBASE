package com.virtualcode7ecuadorvigitrack.trys.runnable;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.virtualcode7ecuadorvigitrack.trys.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class cRunnableTrazos implements Runnable
{
    private GoogleMap googleMap;
    private Context context;
    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue requestQueue;
    private double km_reco=0;
    private double minu_reco=0;
    private double Latitud_inicio;
    private double Longitud_inicio;
    private double Latitud_final;
    private double Longitud_final;
    ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    private double precio_pago_carrera;
    private Polyline polyline;
    private ProgressDialog progressDialog;
    private int day_night;

    private boolean ban=false;

    public cRunnableTrazos(Context context_,GoogleMap googleMap_)
    {
        this.context = context_;
        this.googleMap = googleMap_;
    }

    public void setBan(boolean ban) {
        this.ban = ban;
    }

    public double getKm_reco() {
        return km_reco;
    }

    public void setKm_reco(double km_reco) {
        this.km_reco = km_reco;
    }

    public double getMinu_reco() {
        return minu_reco;
    }

    public void setMinu_reco(double minu_reco) {
        this.minu_reco = minu_reco;
    }

    public double getLatitud_inicio() {
        return Latitud_inicio;
    }

    public void setLatitud_inicio(double latitud_inicio) {
        Latitud_inicio = latitud_inicio;
    }

    public double getLongitud_inicio() {
        return Longitud_inicio;
    }

    public void setLongitud_inicio(double longitud_inicio) {
        Longitud_inicio = longitud_inicio;
    }

    public double getLatitud_final() {
        return Latitud_final;
    }

    public void setLatitud_final(double latitud_final) {
        Latitud_final = latitud_final;
    }

    public double getLongitud_final() {
        return Longitud_final;
    }

    public void setLongitud_final(double longitud_final) {
        Longitud_final = longitud_final;
    }

    public double getPrecio_pago_carrera() {
        return precio_pago_carrera;
    }

    public void setPrecio_pago_carrera(double precio_pago_carrera) {
        this.precio_pago_carrera = precio_pago_carrera;
    }

    public int getDay_night() {
        return day_night;
    }

    public void setDay_night(int day_night) {
        this.day_night = day_night;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }
    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void run()
    {
        initProgressDialog();
        String url_ruta_corta = "https://maps.googleapis.com/maps/api/directions/json?origin="+getLatitud_inicio()
                +","+getLongitud_inicio()+"&destination="+getLatitud_final()+","+getLongitud_final()+"&key="+getContext().getString(R.string.api_key_google_maps);
        Log.e("URLRUTATRAZO",url_ruta_corta);
        jsonObjectRequest = new JsonObjectRequest(url_ruta_corta, null, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                try {
                    JSONArray jsonArray_routes = response.getJSONArray("routes");
                    for (int i=0;i<jsonArray_routes.length();i++)
                    {
                        JSONObject jsonObject = jsonArray_routes.getJSONObject(i);
                        JSONArray jsonArray_legs = jsonObject.getJSONArray("legs");
                        for (int j = 0; j < jsonArray_legs.length(); j++)
                        {
                            JSONObject jsonObject2 = jsonArray_legs.getJSONObject(i);
                            JSONObject jsonObject_distance = jsonObject2.getJSONObject("distance");

                            JSONObject jsonObject_duration = jsonObject2.getJSONObject("duration");

                            km_reco = (double) (jsonObject_distance.getInt("value")/1000);

                            minu_reco = (double) (jsonObject_duration.getInt("value")/60);

                            calcularPrecios(km_reco,minu_reco);

                            textview_mostar_Distance_valores_Tiempos();

                            JSONArray jsonArray_steps = jsonObject2.getJSONArray("steps");
                            for (int k = 0; k < jsonArray_steps.length(); k++)
                            {
                                JSONObject jsonObject_latitud_longitud = jsonArray_steps.getJSONObject(k);
                                JSONObject jsonObject_end_location = jsonObject_latitud_longitud.getJSONObject("end_location");
                                JSONObject jsonObject_start_location = jsonObject_latitud_longitud.getJSONObject("start_location");
                                latLngArrayList.add(new LatLng(jsonObject_start_location.getDouble("lat"),jsonObject_start_location.getDouble("lng")));
                                latLngArrayList.add(new LatLng(jsonObject_end_location.getDouble("lat"),jsonObject_end_location.getDouble("lng")));
                            }
                        }
                    }
                        /*for (int l=0;l<latLngArrayList.size();l++)
                        {
                            Log.e("LATLNG",latLngArrayList.get(l).latitude+" "+latLngArrayList.get(l).longitude);
                        }*/
                    graficarPolilineas();
                } catch (JSONException e)
                {
                    closeProgressDialog();
                    Log.e("Error","TRYCATHCTRAZARRUTA "+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                closeProgressDialog();
                Toast.makeText(context, "VOLLEY ERROR RUTA "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void initProgressDialog()
    {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("CALCULANDO COSTOS");
        progressDialog.setCancelable(false);
        progressDialog.create();
        progressDialog.show();
    }
    private void closeProgressDialog()
    {
        progressDialog.cancel();
        progressDialog.dismiss();
    }
    private void calcularPrecios(final double km_reco, final double minu_reco)
    {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Price");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    if (getDay_night()==1)/** DIA **/
                    {
                        setPrecio_pago_carrera((km_reco* Double.parseDouble(snapshot.child("km_day").getValue().toString()))
                                +(minu_reco*Double.parseDouble(snapshot.child("minute_go_day").getValue().toString()))
                                +Double.parseDouble(snapshot.child("tarifa_base_day").getValue().toString()));

                    }else /** NOCHE **/
                        {
                            setPrecio_pago_carrera((km_reco* Double.parseDouble(snapshot.child("km_night").getValue().toString()))
                                    +(minu_reco*Double.parseDouble(snapshot.child("minute_go_night").getValue().toString()))
                                    +Double.parseDouble(snapshot.child("tarifa_base_night").getValue().toString()));
                        }

                }else
                    {
                        Toast.makeText(context, "NODATASNAPSHOT : ", Toast.LENGTH_SHORT).show();
                    }
                closeProgressDialog();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                closeProgressDialog();
            }
        });
    }

    private void textview_mostar_Distance_valores_Tiempos()
    {

    }
    private void graficarPolilineas() {
        polyline = getGoogleMap().addPolyline(new PolylineOptions().addAll(latLngArrayList));
        /*Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(-35.016, 143.321),
                        new LatLng(-34.747, 145.592),
                        new LatLng(-34.364, 147.891),
                        new LatLng(-33.501, 150.217),
                        new LatLng(-32.306, 149.248),
                        new LatLng(-32.491, 147.309)));*/
        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.684, 133.903), 4));
    }
    public void deletepolilineas()
    {
        polyline.remove();
    }
}
