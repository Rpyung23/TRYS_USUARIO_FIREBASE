package com.virtualcode7ecuadorvigitrack.trys.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.virtualcode7ecuadorvigitrack.trys.R;
import com.virtualcode7ecuadorvigitrack.trys.models.cMessaging;
import com.virtualcode7ecuadorvigitrack.trys.provider.cFirebaseProviderAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class adapterChats extends RecyclerView.Adapter<adapterChats.cViewHolderChats>
{
    ArrayList<cMessaging> mMessagingArrayList;

    private cFirebaseProviderAuth mFirebaseProviderAuth = new cFirebaseProviderAuth();

    private final static  int tipo_LEFT = 1; /**LEFT**/
    private final static  int tipo_RIGHT = 2; /**RIGHT**/
    private final static  int tipo_LEFT_IMG = 3; /**LEFT_IMG**/
    private final static  int tipo_RIGHT_IMG = 4; /**RIGHT_IMG**/

    private Context contextChats;

    private View view_;

    public adapterChats(ArrayList<cMessaging> mMessagingArrayList, Context contextChats)
    {
        this.mMessagingArrayList = mMessagingArrayList;
        this.contextChats = contextChats;
    }

    @NonNull
    @Override
    public cViewHolderChats onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if (viewType == tipo_RIGHT )
        {
            view_ = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_msm_right,parent
                    ,false);
        }else if (viewType == tipo_LEFT)
        {
            view_ = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_msm_left,parent
                    ,false);
        }else if (viewType == tipo_RIGHT_IMG )
        {
            view_ = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_picture_right,parent
                    ,false);
            Log.e("ADAPTER","IMG_RIGHT");
        }
        else if (viewType == tipo_LEFT_IMG)
        {
            view_ = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_picture_left,parent
                    ,false);
            Log.e("ADAPTER","IMG_LEFT");
        }

        return new cViewHolderChats(view_);
    }

    @Override
    public void onBindViewHolder(@NonNull cViewHolderChats holder, int position)
    {
        /****/

        if (mMessagingArrayList.get(position).getId_reciver_send()
                .equals(mFirebaseProviderAuth.getmFirebaseAuth().getUid()))
        {
            Picasso.with(contextChats)
                    .load(mMessagingArrayList.get(position).getUrl_perfil_driver())
                    .error(R.drawable.error_image_load)
                    .placeholder(R.drawable.loading_photo)
                    .into(holder.mCircleImageView);
        }else
        {
            Picasso.with(contextChats)
                    .load(mMessagingArrayList.get(position).getUrl_perfil_client())
                    .error(R.drawable.error_image_load)
                    .placeholder(R.drawable.loading_photo)
                    .into(holder.mCircleImageView);
        }


        if (mMessagingArrayList.get(position).getType_msm().equals("text"))
        {
            if(mMessagingArrayList.get(position).getMessaging().length() > 60)
            {
                ViewGroup.LayoutParams layoutParams = holder.mTextViewMessaing.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.mTextViewMessaing.setLayoutParams(layoutParams);
            }else
                {

                    int actionBarHeight =60;
                    TypedValue tv = new TypedValue(); /** Variable para acceder a los datos de actionbarheight*/
                    if (contextChats.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                    {
                        actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                                contextChats.getResources().getDisplayMetrics());
                    }
                    ViewGroup.LayoutParams layoutParams = holder.mTextViewMessaing.getLayoutParams();
                    layoutParams.height = actionBarHeight;
                    holder.mTextViewMessaing.setLayoutParams(layoutParams);
                }
            holder.mTextViewMessaing.setText(mMessagingArrayList.get(position).getMessaging());
        }else
        {
            byte[] bytes = Base64.decode(mMessagingArrayList.get(position).getMessaging()
                    ,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            if (bitmap!=null)
            {
                holder.imageView_photo.setImageBitmap(bitmap);
            }
        }

    }

    @Override
    public int getItemCount()
    {
        return mMessagingArrayList.size();
    }

    public class cViewHolderChats extends RecyclerView.ViewHolder
    {
        private CircleImageView mCircleImageView;
        private TextView mTextViewMessaing;
        private ImageView imageView_photo;

        public cViewHolderChats(@NonNull View itemView)
        {
            super(itemView);

            imageView_photo = itemView.findViewById(R.id.image_id_msm_chat_);
            mCircleImageView = itemView.findViewById(R.id.id_circle_profile_chats);
            mTextViewMessaing = itemView.findViewById(R.id.textview_id_msm_chat);
        }

    }

    @Override
    public int getItemViewType(int position)
    {
        int val=0;

        if (mMessagingArrayList.get(position).getId_reciver_send()
                .equals(mFirebaseProviderAuth
                        .getmFirebaseAuth().getUid()) && mMessagingArrayList.get(position).getType_msm()
                .equals("text"))
        {
            val = tipo_LEFT;
        }else if (!mMessagingArrayList.get(position).getId_reciver_send()
                .equals(mFirebaseProviderAuth.getmFirebaseAuth().getUid())
                && mMessagingArrayList.get(position).getType_msm().equals("text"))
        {
            val = tipo_RIGHT;
        }else if (mMessagingArrayList.get(position).getId_reciver_send()
                .equals(mFirebaseProviderAuth
                        .getmFirebaseAuth().getUid()) && mMessagingArrayList.get(position).getType_msm()
                .equals("img"))
        {
            val = tipo_LEFT_IMG;
        }else if (!mMessagingArrayList.get(position).getId_reciver_send()
                .equals(mFirebaseProviderAuth.getmFirebaseAuth().getUid())
                && mMessagingArrayList.get(position).getType_msm().equals("img"))
        {
            val = tipo_RIGHT_IMG;
        }

        return val;
    }
}
