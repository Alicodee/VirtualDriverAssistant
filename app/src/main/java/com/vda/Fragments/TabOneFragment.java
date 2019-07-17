package com.vda.Fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vda.Adapters.TabOneAdapter;
import com.vda.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabOneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabOneFragment extends Fragment {

    RecyclerView recyclerView;
    TabOneAdapter adapter;
    ArrayList<Drawable> imgList;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_one, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.fragment_one_rv);
        imgList = new ArrayList<>();
        imgList.add(view.getResources().getDrawable(R.drawable.m1));
        imgList.add(view.getResources().getDrawable(R.drawable.m2));
        imgList.add(view.getResources().getDrawable(R.drawable.m3));
        imgList.add(view.getResources().getDrawable(R.drawable.m4));
        imgList.add(view.getResources().getDrawable(R.drawable.m5));
        imgList.add(view.getResources().getDrawable(R.drawable.m6));
        imgList.add(view.getResources().getDrawable(R.drawable.m7));
        imgList.add(view.getResources().getDrawable(R.drawable.m8));
        imgList.add(view.getResources().getDrawable(R.drawable.m9));
        imgList.add(view.getResources().getDrawable(R.drawable.m10));
        imgList.add(view.getResources().getDrawable(R.drawable.m11));
        imgList.add(view.getResources().getDrawable(R.drawable.m12));
        imgList.add(view.getResources().getDrawable(R.drawable.m13));
        imgList.add(view.getResources().getDrawable(R.drawable.m14));
        imgList.add(view.getResources().getDrawable(R.drawable.m15));
        adapter = new TabOneAdapter(imgList,getContext());
        layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }
}
