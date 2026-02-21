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
import com.example.barberappointmentapp.models.TimeOff;
import com.example.barberappointmentapp.utils.TimeUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TimeOffsAdapter extends RecyclerView.Adapter<TimeOffsAdapter.myViewHolder> {
    private ArrayList<TimeOff> timeoffsList;

    public TimeOffsAdapter(ArrayList<TimeOff> timeoffsList) {
        this.timeoffsList = timeoffsList;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView tvReason;
        TextView tvStartDateTime;
        TextView tvEndDateTime;
        MaterialButton btnRemoveTimeoff;

        public myViewHolder(View itemView) {
            super(itemView);

            tvReason = itemView.findViewById(R.id.tvReason);
            tvStartDateTime = itemView.findViewById(R.id.tvStartDateTime);
            tvEndDateTime = itemView.findViewById(R.id.tvEndDateTime);
            btnRemoveTimeoff = itemView.findViewById(R.id.btnRemoveTimeoff);

            //================================ remove timeoff button listener=========================================================
            btnRemoveTimeoff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition(); // get the live position
                    if (position == RecyclerView.NO_POSITION) return;

                    TimeOff clickedTimeOff = timeoffsList.get(position);
                    // Alert dialog: "are you sure you want to cancel appointment?"
                    // // https://developer.android.com/develop/ui/views/components/dialogs#AddButtons
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Remove Time Off")
                            .setMessage("Are you sure you want to remove time off?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Update Firebase
                                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                                    DatabaseReference ref = db.getReference("settings").child("timeOffs").child(clickedTimeOff.getId());

                                    // https://firebase.google.com/docs/database/android/read-and-write#delete_data
                                    ref.removeValue() // remove the service from the database
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(v.getContext(), "Time off removed successfully", Toast.LENGTH_SHORT).show();
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
    public TimeOffsAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeoff, parent, false);

        return new TimeOffsAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeOffsAdapter.myViewHolder holder, int position) {
        TimeOff timeoff = timeoffsList.get(position);

        holder.tvReason.setText(timeoff.getReason());
        holder.tvStartDateTime.setText("Start: " + TimeUtils.formatDate(timeoff.getStartDateTime()) + " " + TimeUtils.formatHHmm(timeoff.getStartDateTime()));
        holder.tvEndDateTime.setText("End: " + TimeUtils.formatDate(timeoff.getEndDateTime()) + " " + TimeUtils.formatHHmm(timeoff.getEndDateTime()));
    }

    @Override
    public int getItemCount() {
        return timeoffsList.size();
    }
}
