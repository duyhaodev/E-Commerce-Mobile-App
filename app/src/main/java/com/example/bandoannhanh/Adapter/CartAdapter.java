package com.example.bandoannhanh.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bandoannhanh.Model.CartItem;
import com.example.bandoannhanh.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Context context;
    private CartItemListener cartItemListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface CartItemListener {
        void onQuantityChanged();
        void onItemRemoved();
    }

    public CartAdapter(Context context, List<CartItem> cartItems, CartItemListener listener) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        this.context = context;
        this.cartItems = cartItems;
        this.cartItemListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem  item = cartItems.get(position);

        int imageResId = context.getResources().getIdentifier(
                item.getImageUrl().replace("drawable:", ""),
                "drawable",
                context.getPackageName()
        );
        holder.productImage.setImageResource(imageResId != 0 ? imageResId : R.drawable.banana);
        holder.productName.setText(item.getName());
        holder.productDetails.setText("Price: $" + item.getPrice());
        holder.productPrice.setText(String.format("$%.2f", item.getPrice()));
        holder.quantityText.setText(String.valueOf(item.getQuantity()));

        holder.btnIncrease.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                CartItem cartItem = cartItems.get(pos);
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                notifyItemChanged(pos);

                db.collection("cart")
                        .document(String.valueOf(item.getProductId()))
                        .update("quantity", item.getQuantity());

                if (cartItemListener != null) {
                    cartItemListener.onQuantityChanged();
                }
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                CartItem cartItem = cartItems.get(pos);

                db.collection("cart")
                        .document(String.valueOf(item.getProductId()))
                        .update("quantity", item.getQuantity());

                if (cartItem.getQuantity() > 1) {
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    notifyItemChanged(pos);
                    if (cartItemListener != null) {
                        cartItemListener.onQuantityChanged();
                    }
                }
            }
        });

        holder.removeItem.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {

                db.collection("cart")
                        .document(String.valueOf(item.getProductId()))
                        .delete();

                cartItems.remove(pos);
                notifyItemRemoved(pos);
                if (cartItemListener != null) {
                    cartItemListener.onItemRemoved();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productDetails;
        TextView productPrice;
        TextView quantityText;
        ImageButton btnIncrease;
        ImageButton btnDecrease;
        ImageButton removeItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productDetails = itemView.findViewById(R.id.product_details);
            productPrice = itemView.findViewById(R.id.product_price);
            quantityText = itemView.findViewById(R.id.tv_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            removeItem = itemView.findViewById(R.id.remove_item);
        }
    }
}
