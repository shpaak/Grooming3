package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

public class ManagerFragment extends Fragment {
        private TextView pidTextView;
        private TextView descriptionTextView;
        private TextView priceTextView;
        private ImageView imageView;
        private TextView pnameTextView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_manager, container, false);

            pidTextView = view.findViewById(R.id.product_id);
            descriptionTextView = view.findViewById(R.id.product_description);
            priceTextView = view.findViewById(R.id.product_price);
            imageView = view.findViewById(R.id.product_image);
            pnameTextView = view.findViewById(R.id.product_name);

            Bundle bundle = getArguments();
            if (bundle != null) {
                String pid = bundle.getString("pid");
                String description = bundle.getString("description");
                String price = bundle.getString("price") + " рублей";
                String image = bundle.getString("image");
                String pname = bundle.getString("pname");

                pidTextView.setText(pid);
                descriptionTextView.setText(description);
                priceTextView.setText(price);
                Picasso.get().load(image).into(imageView);
                pnameTextView.setText(pname);
            }

            return view;
        }
    }
