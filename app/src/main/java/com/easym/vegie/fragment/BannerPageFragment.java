package com.easym.vegie.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.easym.vegie.R;

public class BannerPageFragment extends Fragment {
    private static final String ARG_RESOURCE_ID = "resource_id";
    private int id; // resource id of the static image to display in this page

    public BannerPageFragment() {
        // Required empty public constructor
    }

    // Your program should call this to create each instance of this Fragment.
    public static BannerPageFragment newInstance(int id) {
        BannerPageFragment fragment = new BannerPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RESOURCE_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ARG_RESOURCE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.banner_view_pager_item, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_BannerImage);
        imageView.setImageResource(id);
    }
}