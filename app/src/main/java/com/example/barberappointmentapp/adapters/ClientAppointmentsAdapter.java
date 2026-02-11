package com.example.barberappointmentapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.models.Appointment;
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
        Appointment ap = appointmentsList.get(position);

        holder.tvServiceName.setText(ap.getServiceId());
        holder.tvDateTime.setText(TimeUtils.formatDateAndTimeRange(ap.getStartEpoch(), ap.calcEndEpoch()));
        holder.tvStatus.setText(ap.isCancelled() ? "CANCELED" : "ACTIVE");
        holder.btnCancel.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return appointmentsList.size();
    }
}
