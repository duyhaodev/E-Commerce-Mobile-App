package com.example.bandoannhanh.Data;

import com.example.bandoannhanh.Model.CartItem;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CartData {
    private FirebaseFirestore db;

    public CartData() {
        db = FirebaseFirestore.getInstance();
    }

    public void addToCart(CartItem cartItem, OnCartOperationCallback callback) {
        String id = String.valueOf(cartItem.getProductId());

        DocumentReference cartRef = db.collection("cart").document(id);

        cartRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                int oldQty = snapshot.getLong("quantity").intValue();
                cartRef.update("quantity", oldQty + 1)
                        .addOnSuccessListener(aVoid -> callback.onSuccess("Đã cập nhật số lượng trong giỏ hàng"))
                        .addOnFailureListener(callback::onFailure);
            } else {
                cartRef.set(cartItem)
                        .addOnSuccessListener(aVoid -> callback.onSuccess("Đã thêm vào giỏ hàng"))
                        .addOnFailureListener(callback::onFailure);
            }
        }).addOnFailureListener(callback::onFailure);
    }

    public interface OnCartOperationCallback {
        void onSuccess(String message);
        void onFailure(Exception e);
    }
}
