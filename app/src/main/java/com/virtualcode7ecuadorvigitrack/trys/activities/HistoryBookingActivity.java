package com.virtualcode7ecuadorvigitrack.trys.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.adapter.adapterHistoryBooking;
import com.virtualcode7ecuadorvigitrack.trys.includes.cToolbar;
import com.virtualcode7ecuadorvigitrack.trys.models.cHistoryBooking;
import com.virtualcode7ecuadorvigitrack.trys.provider.*;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryBookingActivity extends AppCompatActivity {

    private ShimmerRecyclerView shimmerRecycler;
    private ArrayList<cHistoryBooking> mArraysHistoryBookings;
    private adapterHistoryBooking mAdapterHistoryBooking;
    private cFirebaseHistoryBooking mFirebaseHistoryBooking;
    private cFirebaseProviderAuth mFirebaseProviderAuth;
    private Query mQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_booking);
        new cToolbar().showToolbar(HistoryBookingActivity.this,"HISTORIAL DE CARRERAS",true);
        shimmerRecycler = (ShimmerRecyclerView) findViewById(R.id.shimmer_recycler_view);
        shimmerRecycler.showShimmerAdapter();
        mFirebaseHistoryBooking = new cFirebaseHistoryBooking();
        mFirebaseProviderAuth = new cFirebaseProviderAuth();
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(HistoryBookingActivity.this);
        mLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        shimmerRecycler.setDemoLayoutManager(ShimmerRecyclerView.LayoutMangerType.LINEAR_VERTICAL);
        mArraysHistoryBookings = new ArrayList<>();
        mAdapterHistoryBooking = new adapterHistoryBooking(mArraysHistoryBookings,HistoryBookingActivity.this);
        shimmerRecycler.setLayoutManager(mLinearLayoutManager);
        shimmerRecycler.setAdapter(mAdapterHistoryBooking);
    }

    @Override
    protected void onPostResume()
    {
        mFirebaseHistoryBooking.readHistoryBooking(mFirebaseProviderAuth.getmFirebaseAuth().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot mDataSnapshot : snapshot.getChildren())
                {
                    if (mDataSnapshot.exists())
                    {
                        if (mDataSnapshot.child("id_client").getValue().toString().equals(mFirebaseProviderAuth
                                .getmFirebaseAuth().getCurrentUser().getUid()))
                        {
                            cHistoryBooking mHistoryBooking = new cHistoryBooking();
                            mHistoryBooking.setLat_ini(Double.parseDouble(mDataSnapshot.child("latitud_start")
                                    .getValue().toString()));
                            mHistoryBooking.setLong_ini(Double.parseDouble(mDataSnapshot.child("longitud_start")
                                    .getValue().toString()));
                            mHistoryBooking.setLat_end(Double.parseDouble(mDataSnapshot.child("latitud_end")
                                    .getValue().toString()));
                            mHistoryBooking.setLong_end(Double.parseDouble(mDataSnapshot.child("longitud_end")
                                    .getValue().toString()));
                            mHistoryBooking.setFecha(mDataSnapshot.child("fecha").getValue().toString());
                            /*mHistoryBooking.setPlate(mDataSnapshot.child("")
                                    .getValue().toString());*/
                            mHistoryBooking.setPrice(Double.parseDouble(mDataSnapshot.child("price")
                                    .getValue().toString()));
                            mArraysHistoryBookings.add(mHistoryBooking);
                            Log.e("ADD","ok");
                        }
                    }
                }
                if (mArraysHistoryBookings!=null && mArraysHistoryBookings.size()>0)
                {
                    Collections.reverse(mArraysHistoryBookings);
                }
                mAdapterHistoryBooking.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
        super.onPostResume();
    }
}