package com.example.barberappointmentapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.models.Appointment;
import com.example.barberappointmentapp.ui.main.MainActivity;
import com.example.barberappointmentapp.utils.TimeUtils;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;

public class BarberAppointmentsAdapter extends RecyclerView.Adapter<BarberAppointmentsAdapter.myViewHolder>  {
    // List of appointments
    private ArrayList<Appointment> appointmentsList;
    public BarberAppointmentsAdapter(ArrayList<Appointment> appointmentsList) {
        this.appointmentsList = appointmentsList;
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientName;
        TextView tvClientPhone;
        TextView tvServiceName;
        TextView tvDateTime;
        TextView tvStatus;
        MaterialButton btnCancel;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvClientPhone = itemView.findViewById(R.id.tvClientPhone);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnCancel = itemView.findViewById(R.id.btnCancel);
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

        long now = System.currentTimeMillis(); // current time in epoch milliseconds
        boolean isPast = appointment.calcEndEpoch() <= now; // appointment already ended

        holder.tvClientName.setText(appointment.getClientName());
        holder.tvClientPhone.setText(appointment.getClientPhone());
        holder.tvServiceName.setText(appointment.getServiceName());
        holder.tvDateTime.setText(TimeUtils.formatDateAndTimeRange(appointment.getStartDateTime(), appointment.calcEndEpoch())); // appointment date & time

        // ================= appointment status badge- confirmed/cancelled/completed =================
        // appointment is cancelled
        if (appointment.getCancelled()) { // if appointment is cancelled - cancelled=true
            holder.tvStatus.setText("CANCELED");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_cancelled);

            // cancelled appointments cannot be cancelled again
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnCancel.setOnClickListener(null);

        } // appointment is in the past -> completed
        else if (isPast) { //CHANGE THIS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            // appointment already happened
            holder.tvStatus.setText("COMPLETED");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_completed);
            holder.tvStatus.setTextColor(0xFF424242);
            // past appointments cannot be cancelled
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnCancel.setOnClickListener(null);
        }
        else { // appointment is not cancelled and not in the past -> hence confirmed
            holder.tvStatus.setText("CONFIRMED");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_confirmed);
            holder.btnCancel.setVisibility(View.VISIBLE);

            // cancel appointment button listener
            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity mainActivity = (MainActivity) view.getContext();
                    mainActivity.cancelAppointment(appointment);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return appointmentsList.size();
    }
}
