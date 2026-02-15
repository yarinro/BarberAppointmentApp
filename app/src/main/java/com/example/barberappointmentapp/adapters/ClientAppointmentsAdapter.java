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

public class ClientAppointmentsAdapter extends RecyclerView.Adapter<ClientAppointmentsAdapter.myViewHolder> {

    private ArrayList<Appointment> appointmentsList;

    public ClientAppointmentsAdapter(ArrayList<Appointment> appointmentsList) {
        this.appointmentsList = appointmentsList;
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName;
        TextView tvDateTime;
        TextView tvStatus;
        MaterialButton btnCancel;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnCancel = itemView.findViewById(R.id.btnCancel);
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

        String name = appointment.getServiceName();
        holder.tvServiceName.setText(name != null ? name : "UNKNOWN SERVICE");
        holder.tvDateTime.setText(TimeUtils.formatDateAndTimeRange(appointment.getStartDateTime(), appointment.calcEndEpoch())); // change this!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        // if appointment is cancelled - cancelled=true
        if (appointment.getCancelled()) {
            holder.tvStatus.setText("CANCELED");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_cancelled);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnCancel.setOnClickListener(null);

        } else { // if appointment is confirmed - cancelled=false
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
