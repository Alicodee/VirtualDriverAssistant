package com.vda.Fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vda.Adapters.TabOneAdapter;
import com.vda.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabThreeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabThreeFragment extends Fragment {
    RecyclerView recyclerView;
    TabOneAdapter adapter;
    ArrayList<Drawable> imgList;
    private RecyclerView.LayoutManager layoutManager;
    public TabThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_three, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.fragment_three_rv);
        imgList = new ArrayList<>();
        imgList.add(view.getResources().getDrawable(R.drawable.h));
        imgList.add(view.getResources().getDrawable(R.drawable.h1));
        imgList.add(view.getResources().getDrawable(R.drawable.h2));
        imgList.add(view.getResources().getDrawable(R.drawable.h3));
        imgList.add(view.getResources().getDrawable(R.drawable.h4));
        imgList.add(view.getResources().getDrawable(R.drawable.h5));
        imgList.add(view.getResources().getDrawable(R.drawable.h6));
        imgList.add(view.getResources().getDrawable(R.drawable.h7));
        imgList.add(view.getResources().getDrawable(R.drawable.h8));
        imgList.add(view.getResources().getDrawable(R.drawable.h9));
        imgList.add(view.getResources().getDrawable(R.drawable.h10));
        imgList.add(view.getResources().getDrawable(R.drawable.h11));
        imgList.add(view.getResources().getDrawable(R.drawable.h12));
        imgList.add(view.getResources().getDrawable(R.drawable.h13));
        imgList.add(view.getResources().getDrawable(R.drawable.h14));
        imgList.add(view.getResources().getDrawable(R.drawable.h15));
        imgList.add(view.getResources().getDrawable(R.drawable.h16));
        imgList.add(view.getResources().getDrawable(R.drawable.h17));
        adapter = new TabOneAdapter(imgList,getContext());
        layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}
