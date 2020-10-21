package com.fortitude.shamsulkarim.ieltsfordory.purchase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.SkuDetails;
import com.fortitude.shamsulkarim.ieltsfordory.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InAppPurchaseRecyclerView extends RecyclerView.Adapter<InAppPurchaseRecyclerView.productViewHolder>{

    private final List<SkuDetails> skuDetails;

    public InAppPurchaseRecyclerView(Context context, List<SkuDetails> skuDetails) {

        this.skuDetails = skuDetails;

    }



    public @NotNull productViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_view,parent,false);

        return new productViewHolder(view);



    }

    @Override
    public void onBindViewHolder(@NonNull productViewHolder holder, int position) {

        holder.pruductName.setText(skuDetails.get(position).getTitle());
        holder.productDescription.setText(skuDetails.get(position).getDescription());
        holder.productPrice.setText(skuDetails.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        return skuDetails.size();
    }


    static class productViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView pruductName;
        final TextView productDescription;
        final TextView productPrice;
        final Button buyButton;

        public productViewHolder(@NonNull View itemView) {
            super(itemView);

            pruductName = itemView.findViewById(R.id.product_name);
            productDescription = itemView.findViewById(R.id.product_description);
            productPrice = itemView.findViewById(R.id.product_price);
            buyButton = itemView.findViewById(R.id.buy_button);
            buyButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            Toast.makeText(view.getContext(),"Buy",Toast.LENGTH_SHORT).show();
        }
    }
}
