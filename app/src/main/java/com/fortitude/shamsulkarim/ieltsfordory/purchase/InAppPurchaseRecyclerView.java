package com.fortitude.shamsulkarim.ieltsfordory.purchase;

import android.content.Context;
import android.os.TestLooperManager;
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
import java.util.List;

public class InAppPurchaseRecyclerView extends RecyclerView.Adapter<InAppPurchaseRecyclerView.productViewHolder>{

    private List<SkuDetails> skuDetails;
    private Context context;

    public InAppPurchaseRecyclerView(Context context, List<SkuDetails> skuDetails) {

        this.skuDetails = skuDetails;
        this.context = context;

    }



    public productViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_view,parent,false);

        productViewHolder viewHolder = new productViewHolder(view);
        return viewHolder;



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

        TextView pruductName, productDescription,productPrice;
        Button buyButton;

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
