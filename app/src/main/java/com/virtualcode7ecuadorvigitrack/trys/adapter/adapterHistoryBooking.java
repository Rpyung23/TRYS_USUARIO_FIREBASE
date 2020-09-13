package com.virtualcode7ecuadorvigitrack.trys.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.models.cHistoryBooking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class adapterHistoryBooking extends RecyclerView.Adapter<adapterHistoryBooking.cViewHolderHistoryBooking>
{

    private ArrayList<cHistoryBooking> historyBookings ;
    private Context context;

    public adapterHistoryBooking(ArrayList<cHistoryBooking> historyBookings, Context context) {
        this.historyBookings = historyBookings;
        this.context = context;
    }

    @NonNull
    @Override
    public cViewHolderHistoryBooking onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history_booking,parent,false);
        return new cViewHolderHistoryBooking(view);
    }

    @Override
    public void onBindViewHolder(@NonNull cViewHolderHistoryBooking holder, int position)
    {
        holder.mTextViewPrice.setText(String.valueOf(historyBookings.get(position).getPrice())+" $");
        holder.mTextViewFecha.setText(historyBookings.get(position).getFecha());
        if (Geocoder.isPresent())
        {
            try {
                List<Address> mAddressesStart = new Geocoder(context)
                        .getFromLocation(historyBookings.get(position).getLat_ini()
                                ,historyBookings.get(position).getLong_ini(),1);
                List<Address> mAddressesEnd = new Geocoder(context)
                        .getFromLocation(historyBookings.get(position).getLat_end()
                                ,historyBookings.get(position).getLong_end(),1);
                holder.mTextViewStart.setText(mAddressesStart.get(0).getAddressLine(0));
                holder.mTextViewEnd.setText(mAddressesEnd.get(0).getAddressLine(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return historyBookings.size();
    }

    public class cViewHolderHistoryBooking extends RecyclerView.ViewHolder
    {
        private TextView mTextViewPrice;
        private TextView mTextViewFecha;
        private TextView mTextViewStart;
        private TextView mTextViewEnd;
        private TextView mTextViewPlate;

        public cViewHolderHistoryBooking(@NonNull View itemView)
        {
            super(itemView);
            mTextViewPrice = itemView.findViewById(R.id.id_textview_price_card_history_booking);
            mTextViewFecha = itemView.findViewById(R.id.id_textview_fecha_card_history_booking);
            mTextViewStart = itemView.findViewById(R.id.id_textview_start_card_history_booking);
            mTextViewEnd = itemView.findViewById(R.id.id_textview_end_card_history_booking);
            mTextViewPlate = itemView.findViewById(R.id.id_textview_plate_card_history_booking);
        }
    }
}
