package com.example.bandoannhanh.Data;

import android.util.Log;

import com.example.bandoannhanh.Model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductData {
    private FirebaseFirestore db;

    public ProductData() {
        db = FirebaseFirestore.getInstance();
    }

    public void addProduct(Product product) {
        // Tạo document trong collection "Products"
        db.collection("Products")
                .add(product) // Thêm object Product vào Firestore
                .addOnSuccessListener(documentReference -> {
                    Log.d("ProductDatabase", "DocumentSnapshot added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("ProductDatabase", "Error adding document", e);
                });
    }

    public void loadProducts(LoadProductsCallback callback) {
        db.collection("Products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        products.add(product);
                    }
                    callback.onProductsLoaded(products);
                })
                .addOnFailureListener(e -> {
                    callback.onError(e);
                });
    }

    public interface LoadProductsCallback {
        void onProductsLoaded(List<Product> products);
        void onError(Exception e);
    }
}
