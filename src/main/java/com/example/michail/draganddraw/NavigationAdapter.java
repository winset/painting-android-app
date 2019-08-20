package com.example.michail.draganddraw;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavigationViewHolder> {
    private ArrayList<Item> items;
    public static OnItemClickListener listener;
    private int lastPosition = -1;



    public NavigationAdapter(ArrayList<Item> items) {
        this.items = items;
    }


    @Override
    public void onBindViewHolder(NavigationViewHolder navigationViewHolder, int position) {
        Item currentItem = items.get(position);
        navigationViewHolder.itemText.setText(currentItem.getItemText());
        navigationViewHolder.itemIcon.setImageResource(currentItem.getIonId());
        setAnimation(navigationViewHolder.itemView, position);
    }


    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler, viewGroup, false);

        return new NavigationViewHolder(itemView);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            lastPosition = position;
        }
    }

    public class NavigationViewHolder extends RecyclerView.ViewHolder {
        public TextView itemText;
        public ImageView itemIcon;


        public NavigationViewHolder(final View itemView) {
            super(itemView);

            itemText = itemView.findViewById(R.id.item_text);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NavigationAdapter.listener != null) {
                        NavigationAdapter.listener.onItemClick(itemView, getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
}
