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
import com.example.barberappointmentapp.models.Appointment;
import com.example.barberappointmentapp.utils.TimeUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

public class ClientAppointmentsAdapter extends RecyclerView.Adapter<ClientAppointmentsAdapter.myViewHolder> {

    private ArrayList<Appointment> appointmentsList;

    public ClientAppointmentsAdapter(ArrayList<Appointment> appointmentsList) {
        this.appointmentsList = appointmentsList;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName;
        TextView tvDateTime;
        TextView tvStatus;
        MaterialButton btnCancel;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            // https://developer.android.com/develop/ui/views/layout/recyclerview?#implement-adapter
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnCancel = itemView.findViewById(R.id.btnCancel);

            //================================ cancel appointment button listener=========================================================
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition(); // Always use the live position
                    if (position == RecyclerView.NO_POSITION) return;

                    Appointment clickedAppointment = appointmentsList.get(position);
                    // Alert dialog: "are you sure you want to cancel appointment?"
                    // // https://developer.android.com/develop/ui/views/components/dialogs#AddButtons
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Cancel Appointment")
                            .setMessage("Are you sure you want to cancel appointment?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Update Firebase
                                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                                    db.getReference("appointments")
                                            .child(clickedAppointment.getId())
                                            .child("cancelled")
                                            .setValue(true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(v.getContext(), "Appointment cancelled successfully", Toast.LENGTH_SHORT).show();
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
    public ClientAppointmentsAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_client, parent, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientAppointmentsAdapter.myViewHolder holder, int position) {
        Appointment appointment = appointmentsList.get(position);

        String serviceName = appointment.getServiceName();
        holder.tvServiceName.setText(appointment.getServiceName());
        holder.tvDateTime.setText(TimeUtils.formatDateAndTimeRange(appointment.getStartDateTime(), appointment.getEndDateTime())); // date + appointment time range text view

        // if appointment is cancelled - cancelled=true
        if (appointment.getCancelled()) {
            holder.tvStatus.setText("CANCELED");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_cancelled);
            holder.btnCancel.setVisibility(View.GONE);

        }
        else { // if appointment is confirmed - cancelled=false
            holder.tvStatus.setText("CONFIRMED");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_confirmed);
            holder.btnCancel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return appointmentsList.size();
    }
}
