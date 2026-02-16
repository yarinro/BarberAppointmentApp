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
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BarberAppointmentsAdapter extends RecyclerView.Adapter<BarberAppointmentsAdapter.myViewHolder>  {
    // List of appointments
    private ArrayList<Appointment> appointmentsList;
    public BarberAppointmentsAdapter(ArrayList<Appointment> appointmentsList) {
        this.appointmentsList = appointmentsList;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cvAppointmentCard;
        TextView tvClientName;
        TextView tvClientPhone;
        TextView tvServiceName;
        TextView tvDateTime;
        TextView tvStatus;
        MaterialButton btnCancel;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            // https://developer.android.com/develop/ui/views/layout/recyclerview?#implement-adapter
            cvAppointmentCard = itemView.findViewById(R.id.cardViewAppointmentBarber);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvClientPhone = itemView.findViewById(R.id.tvClientPhone);
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
    public BarberAppointmentsAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_barber, parent, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarberAppointmentsAdapter.myViewHolder holder, int position) {
        Appointment appointment = appointmentsList.get(position);

        holder.tvClientName.setText(appointment.getClientName());
        holder.tvClientPhone.setText(appointment.getClientPhone());
        holder.tvServiceName.setText(appointment.getServiceName());
        holder.tvDateTime.setText(TimeUtils.formatDateAndTimeRange(appointment.getStartDateTime(), appointment.getEndDateTime())); // date + appointment time range text view

        // ================= appointment status badge- confirmed/cancelled/completed =================
        // if appointment is cancelled
        if (appointment.getCancelled()) { // if appointment is cancelled - cancelled=true
            holder.cvAppointmentCard.setCardBackgroundColor(0xFFFFFFFF);
            holder.tvStatus.setText("CANCELED");
            holder.tvStatus.setTextColor(0xFFFFFFFF);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_cancelled);
            holder.tvStatus.setVisibility(View.VISIBLE);
            // cancelled appointments cannot be cancelled again
            holder.btnCancel.setVisibility(View.GONE);

        }
        else if (appointment.isPast()) { // if appointment was not cancelled but it's in the past -> hide cancel button (we can't cancel past appointments)
            holder.cvAppointmentCard.setCardBackgroundColor(0xFFF5F5F5); // light grey background for the whole cardview if appointment is past
            holder.tvStatus.setText("COMPLETED");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_completed);
            holder.tvStatus.setTextColor(0xFF424242);
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.GONE); // past appointments cannot be cancelled
        }

        else if (appointment.isHappeningNow()) { // if appointment is happening now -> hide status badge and cancel button
            holder.cvAppointmentCard.setCardBackgroundColor(0xFFFFFFFF);
            holder.tvStatus.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
        }

        else { // if appointment is in the future
            holder.tvStatus.setText("CONFIRMED");
            holder.cvAppointmentCard.setCardBackgroundColor(0xFFFFFFFF);
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_confirmed);
            holder.tvStatus.setTextColor(0xFFFFFFFF);
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return appointmentsList.size();
    }
}
