package com.example.barberappointmentapp.adapters;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.models.Service;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.myViewHolder> {
    private ArrayList<Service> servicesList;

    public ServicesAdapter(ArrayList<Service> servicesList) {
        this.servicesList = servicesList;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardViewService;
        TextView tvServiceName;
        TextView tvServiceDuration;
        TextView tvServicePrice;
        MaterialButton btnRemoveService;

        public myViewHolder(View itemView) {
            super(itemView);
            cardViewService = itemView.findViewById(R.id.cardViewService);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServiceDuration = itemView.findViewById(R.id.tvServiceDuration);
            tvServicePrice = itemView.findViewById(R.id.tvServicePrice);
            btnRemoveService = itemView.findViewById(R.id.btnRemoveService);

            //================================ remove service button listener=========================================================
            btnRemoveService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition(); // get the live position
                    if (position == RecyclerView.NO_POSITION) return;

                    Service clickedService = servicesList.get(position);
                    // Alert dialog: "are you sure you want to cancel appointment?"
                    // // https://developer.android.com/develop/ui/views/components/dialogs#AddButtons
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Remove Service")
                            .setMessage("Are you sure you want to remove service?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Update Firebase
                                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                                    DatabaseReference ref = db.getReference("settings").child("services").child(clickedService.getId());

                                    // https://firebase.google.com/docs/database/android/read-and-write#delete_data
                                    ref.removeValue() // remove the service from the database
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    servicesList.remove(position); // remove from the list after removing from DB
                                                    // https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter#notifyDataSetChanged()
                                                    notifyDataSetChanged(); // user removed an item -> notify the change
                                                    Toast.makeText(v.getContext(), "Service removed successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("No", null) // click no -> listener=null -> close the dialog
                            .show();
                }
            });
            //========================================================================================================================
        }
    }

    @NonNull
    @Override
    public ServicesAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);

        return new ServicesAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesAdapter.myViewHolder holder, int position) {
        Service service = servicesList.get(position);

        holder.tvServiceName.setText(service.getName());
        holder.tvServiceDuration.setText(service.getDurationMinutes() + " min");
        holder.tvServicePrice.setText(service.getPrice() + "₪");
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }
}

