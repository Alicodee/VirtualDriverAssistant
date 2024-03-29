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
 * Use the {@link TabTwoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabTwoFragment extends Fragment {

    RecyclerView recyclerView;
    TabOneAdapter adapter;
    ArrayList<Drawable> imgList;
    private RecyclerView.LayoutManager layoutManager;
    public TabTwoFragment() {
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
        return inflater.inflate(R.layout.fragment_tab_two, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.fragment_two_rv);
        imgList = new ArrayList<>();
        imgList.add(view.getResources().getDrawable(R.drawable.w1));
        imgList.add(view.getResources().getDrawable(R.drawable.w2));
        imgList.add(view.getResources().getDrawable(R.drawable.w3));
        imgList.add(view.getResources().getDrawable(R.drawable.w4));
        imgList.add(view.getResources().getDrawable(R.drawable.w5));
        imgList.add(view.getResources().getDrawable(R.drawable.w6));
        imgList.add(view.getResources().getDrawable(R.drawable.w7));
        imgList.add(view.getResources().getDrawable(R.drawable.w8));
        imgList.add(view.getResources().getDrawable(R.drawable.w9));
        imgList.add(view.getResources().getDrawable(R.drawable.w10));
        imgList.add(view.getResources().getDrawable(R.drawable.w111));
        imgList.add(view.getResources().getDrawable(R.drawable.w12));
        imgList.add(view.getResources().getDrawable(R.drawable.w13));
        imgList.add(view.getResources().getDrawable(R.drawable.w14));
        imgList.add(view.getResources().getDrawable(R.drawable.w15));
        adapter = new TabOneAdapter(imgList,getContext());
        layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}
