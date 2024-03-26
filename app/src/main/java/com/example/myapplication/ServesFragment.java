package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Admin.AdminAddNewProductActivity;
import com.example.myapplication.Admin.UpdateOrDeleteActivity;
import com.example.myapplication.Model.Products;
import com.example.myapplication.R;
import com.example.myapplication.ViewHolder.ServicerHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ServesFragment extends Fragment {

    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Products, ServicerHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_serves, container, false);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView = view.findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef, Products.class).build();

        adapter = new FirebaseRecyclerAdapter<Products, ServicerHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ServicerHolder holder, int i, @NonNull Products model) {
                holder.txtProductName.setText(model.getPname());
                holder.txtProductDescription.setText(model.getDescription());
                holder.txtProductPrice.setText(model.getPrice() + " рублей");
                Picasso.get().load(model.getImage()).into(holder.imageView);

                // Set click listener for each item
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ManagerFragment managerFragment = new ManagerFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("pid", model.getPid());
                        bundle.putString("description", model.getDescription());
                        bundle.putString("price", model.getPrice());
                        bundle.putString("image", model.getImage());
                        bundle.putString("pname", model.getPname());
                        managerFragment.setArguments(bundle);

                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, managerFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
            }

            @NonNull
            @Override
            public ServicerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.servise_items_layout, parent, false);
                return new ServicerHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}