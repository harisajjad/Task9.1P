package com.example.Task91P;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdvertAdapter extends RecyclerView.Adapter<AdvertAdapter.AdvertViewHolder> {

    private List<Advert> advertList;
    private OnItemClickListener itemClickListener;

    // Constructor to initialize the adapter with the advert list and item click listener
    public AdvertAdapter(List<Advert> advertList, OnItemClickListener itemClickListener) {
        this.advertList = advertList;
        this.itemClickListener = itemClickListener;
    }

    // Method to set the advert list for the adapter
    public void setAdvertList(List<Advert> advertList) {
        this.advertList = advertList;
    }

    @NonNull
    @Override
    public AdvertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and create a view holder
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_advert, parent, false);
        return new AdvertViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdvertViewHolder holder, int position) {
        // Bind the data to the view holder
        Advert advert = advertList.get(position);
        holder.bind(advert);
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the advert list
        return advertList.size();
    }

    public class AdvertViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textViewLostOrFound;
        private TextView textViewName;
        private TextView textViewPhone;
        private TextView textViewDescription;
        private TextView textViewDate;
        private TextView textViewLocation;

        public AdvertViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            textViewLostOrFound = itemView.findViewById(R.id.textViewLostOrFound);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);

            // Set click listener for the item view
            itemView.setOnClickListener(this);
        }

        public void bind(Advert advert) {
            // Bind the advert data to the views
            textViewLostOrFound.setText(advert.getLostOrFound());
            textViewName.setText(advert.getName());
            textViewPhone.setText(advert.getPhone());
            textViewDescription.setText(advert.getDescription());
            textViewDate.setText(advert.getDate());
            textViewLocation.setText(advert.getLocation());
        }

        @Override
        public void onClick(View v) {
            // Handle item click event
            if (itemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Get the advert ID and pass it to the item click listener
                    long advertId = advertList.get(position).getId();
                    itemClickListener.onItemClickListener(advertId);
                }
            }
        }
    }

    // Interface for defining the item click listener
    public interface OnItemClickListener {
        void onItemClickListener(long advertId);
    }
}
