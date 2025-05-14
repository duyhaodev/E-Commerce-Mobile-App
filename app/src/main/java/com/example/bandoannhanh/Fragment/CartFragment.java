package com.example.bandoannhanh.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bandoannhanh.Adapter.CartAdapter;
import com.example.bandoannhanh.Model.CartItem;
import com.example.bandoannhanh.Model.Product;
import com.example.bandoannhanh.R;
import com.example.bandoannhanh.SuccessActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment implements CartAdapter.CartItemListener {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private TextView totalPriceView;
    private Button checkoutButton;

    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRecyclerView = view.findViewById(R.id.cart_recycler_view);
        totalPriceView = view.findViewById(R.id.total_price);
        checkoutButton = view.findViewById(R.id.checkout_button);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartItems = new ArrayList<>();

        cartAdapter = new CartAdapter(getContext(), cartItems, this);
        cartRecyclerView.setAdapter(cartAdapter);

        db = FirebaseFirestore.getInstance();

        fetchCartItemsFromFirestore();

        updateTotalPrice();

        checkoutButton.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Đang xử lý đơn hàng...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference cartRef = db.collection("cart");

            cartRef.get().addOnSuccessListener(cartSnapshot -> {
                WriteBatch batch = db.batch();
                boolean hasProducts = false;

                for (DocumentSnapshot cartDoc : cartSnapshot.getDocuments()) {
                    Object productIdObject = cartDoc.get("productId");

                    if (productIdObject != null) {
                        hasProducts = true;
                        String productId = productIdObject.toString();

                        Long cartQtyLong = cartDoc.getLong("quantity");
                        long cartQty = cartQtyLong != null ? cartQtyLong : 0;

                        DocumentReference productRef = db.collection("Products").document(productId);
                        batch.update(productRef, "quantity", FieldValue.increment(-cartQty));
                        batch.delete(cartDoc.getReference());
                    }
                }

                if (!hasProducts) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Không có sản phẩm nào để thanh toán", Toast.LENGTH_SHORT).show();
                    return;
                }

                batch.commit().addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Checkout thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), SuccessActivity.class);
                    startActivity(intent);
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Có lỗi xảy ra khi thanh toán: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    Log.e("CartFragment", "Checkout error: ", e);
                });

            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Không thể lấy giỏ hàng: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e("CartFragment", "Error getting cart: ", e);
            });
        });

        return view;
    }

    private void fetchCartItemsFromFirestore() {
        CollectionReference cartRef = db.collection("cart");

        cartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    cartItems.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CartItem item = document.toObject(CartItem.class);
                        cartItems.add(item);
                    }
                    cartAdapter.notifyDataSetChanged();
                    updateTotalPrice();
                } else {
                    Toast.makeText(getContext(), "Failed to load cart data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem  item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        totalPriceView.setText(String.format("$%.2f", total));
    }

    @Override
    public void onQuantityChanged() {
        updateTotalPrice();
    }

    @Override
    public void onItemRemoved() {
        updateTotalPrice();
    }
}