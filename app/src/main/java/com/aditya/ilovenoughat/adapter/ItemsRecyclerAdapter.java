package com.aditya.ilovenoughat.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aditya.ilovenoughat.R;
import com.aditya.ilovenoughat.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by aditya on 9/8/16.
 */
public class ItemsRecyclerAdapter extends RecyclerView.Adapter<ItemsRecyclerAdapter.ViewHolderStub> {

    private ArrayList<Item> itemArrayList;
    private Context mContext;
    public ItemsRecyclerAdapter(Context ctx, ArrayList<Item> mList) {
        mContext = ctx;
        itemArrayList = mList;
    }

    public ArrayList<Item> getItemArrayList() {
        return itemArrayList;
    }

    @Override
    public ViewHolderStub onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.item_layout, parent, false);
        ViewHolderStub viewHolderStub = new ViewHolderStub(rootView);
        return viewHolderStub;
    }

    @Override
    public void onBindViewHolder(ViewHolderStub holder, int position) {
        if (itemArrayList != null) {
            Item item = itemArrayList.get(position);
            holder.item_brand.setText(item.getBrandName());
            Picasso.with(mContext).load(item.getThumbnailImageUrl()).into(holder.item_thumbnail);
            holder.item_name.setText(item.getProductName());
            holder.item_price.setText(item.getPrice());
            holder.item_original_price.setText(item.getOriginalPrice());
            holder.item_percent_off.setText(item.getPercentOff()+" OFF!");
        }
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    public void refreshAdapterData(ArrayList<Item> newList) {
        itemArrayList.clear();
        itemArrayList.addAll(newList);
        notifyDataSetChanged();
    }

    public class ViewHolderStub extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView item_thumbnail;
        TextView item_brand;
        TextView item_name;
        TextView item_price;
        TextView item_original_price;
        TextView item_percent_off;

        public ViewHolderStub(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            item_brand = (TextView) itemView.findViewById(R.id.grid_item_brand_name);
            item_thumbnail = (ImageView) itemView.findViewById(R.id.grid_item_thumbnail);
            item_name = (TextView) itemView.findViewById(R.id.grid_item_name);
            item_price = (TextView) itemView.findViewById(R.id.grid_item_updated_price);
            item_original_price=(TextView) itemView.findViewById(R.id.grid_item_original_price);
            item_percent_off=(TextView) itemView.findViewById(R.id.grid_item_percent_off);

            item_original_price.setPaintFlags(item_original_price.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

}
