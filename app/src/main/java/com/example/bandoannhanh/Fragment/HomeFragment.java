package com.example.bandoannhanh.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.bandoannhanh.Adapter.ProductAdapter;
import com.example.bandoannhanh.Data.ProductData;
import com.example.bandoannhanh.Model.Product;
import com.example.bandoannhanh.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private Button test;
    private FirebaseFirestore db;
    private ProductData productData;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.exclusiveRecyclerView);

        /*test = view.findViewById(R.id.addFoodBtn);

        test.setOnClickListener(v -> {
            Product newProduct = new Product("Orange", 2.5, String.valueOf(R.drawable.organe), 3);
            Product newProduct1 = new Product("Meat", 6, String.valueOf(R.drawable.meat), 10);
            Product newProduct2 = new Product("Apple", 4.7, String.valueOf(R.drawable.apple), 20);
            Product newProduct3 = new Product("Snack Lays", 3, String.valueOf(R.drawable.snacklay), 15);
            Product newProduct4 = new Product("Milo milk", 2, String.valueOf(R.drawable.milo), 20);
            Product newProduct5 = new Product("Vinamilk", 2.3, String.valueOf(R.drawable.vinamilk), 30);
            Product newProduct6 = new Product("TH true milk", 2.5, String.valueOf(R.drawable.thmilk), 30);
            ProductData productData = new ProductData();
            productData.addProduct(newProduct);
            productData.addProduct(newProduct1);
            productData.addProduct(newProduct2);
            productData.addProduct(newProduct3);
            productData.addProduct(newProduct4);
            productData.addProduct(newProduct5);
            productData.addProduct(newProduct6);
        });*/

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), productList);
        recyclerView.setAdapter(productAdapter);

        productData = new ProductData();
        productData.loadProducts(new ProductData.LoadProductsCallback() {
            @Override
            public void onProductsLoaded(List<Product> products) {
                productList.clear();
                productList.addAll(products);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Load failed", Toast.LENGTH_SHORT).show();
                Log.e("HomeFragment", "Error loading product", e);
            }
        });

        return view;
    }
}