package com.example.bandoannhanh;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bandoannhanh.Data.CartData;
import com.example.bandoannhanh.Model.CartItem;
import com.google.android.material.button.MaterialButton;

public class DetailPageActivity extends AppCompatActivity {
    private int quantity = 1;
    private TextView quantityTextView, productNameTextView, priceTextView;
    private ImageButton backBtn, increaseBtn, decreaseBtn;
    private ImageView productImage;
    private MaterialButton addToBasketBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Nhận dữ liệu từ Intent
        String productId = getIntent().getStringExtra("product_id");
        String name = getIntent().getStringExtra("product_name");
        double price = getIntent().getDoubleExtra("product_price", 0.0);
        String imagePath = getIntent().getStringExtra("product_image");

        productNameTextView = findViewById(R.id.tvProductName);
        priceTextView = findViewById(R.id.tvPrice);
        productImage = findViewById(R.id.ivProductImage);
        addToBasketBtn = findViewById(R.id.btnAddToBasket);

        productNameTextView.setText(name);
        priceTextView.setText("$" + price);

        int imageResId = getResources().getIdentifier(
                imagePath.replace("drawable:", ""), "drawable", getPackageName());
        if (imageResId != 0) {
            productImage.setImageResource(imageResId);
        } else {
            productImage.setImageResource(R.drawable.banana); // fallback
        }

        backBtn = findViewById(R.id.btnBack);
        increaseBtn = findViewById(R.id.btnIncrease);
        decreaseBtn = findViewById(R.id.btnDecrease);
        quantityTextView = findViewById(R.id.tvQuantity);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    quantityTextView.setText(String.valueOf(quantity));
                }
            }
        });

        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                quantityTextView.setText(String.valueOf(quantity));
            }
        });

        addToBasketBtn.setOnClickListener(v -> {
            CartItem cartItem = new CartItem(
                    productId,
                    name,
                    price,
                    quantity,
                    imagePath
            );

            CartData cartData = new CartData();

            cartData.addToCart(cartItem, new CartData.OnCartOperationCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(DetailPageActivity.this, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(DetailPageActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}