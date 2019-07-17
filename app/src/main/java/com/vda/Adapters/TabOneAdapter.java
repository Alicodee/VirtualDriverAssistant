package com.vda.Adapters;

import android.content.Context;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;


import androidx.recyclerview.widget.RecyclerView;

import com.vda.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabOneAdapter extends RecyclerView.Adapter<TabOneAdapter.TabOneAdapterView>{

    Context mContext;
    ArrayList<Drawable> mDataset;
    public TabOneAdapter(ArrayList<Drawable> myDataset, Context context){
        mDataset = myDataset;
        mContext = context;
    }

    @Override
    public TabOneAdapterView onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tab_one_items, parent, false);

        return new TabOneAdapterView(itemView);
    }

    @Override
    public void onBindViewHolder(final TabOneAdapterView holder, final int position) {

        holder.img.setImageDrawable(mDataset.get(position));
        setFadeAnimation(holder.itemView);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }





    public class TabOneAdapterView extends RecyclerView.ViewHolder{

        ImageView img;

        public TabOneAdapterView(View view){
            super(view);
            img   = view.findViewById(R.id.sign_img);
        }

    }
    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(800);
        view.startAnimation(anim);
    }
}
