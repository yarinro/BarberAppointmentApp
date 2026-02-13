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

    public interface OnCancelClickListener {
        void onCancel(Appointment ap);
    }
    private OnCancelClickListener cancelListener;

    public ClientAppointmentsAdapter(ArrayList<Appointment> appointmentsList, OnCancelClickListener cancelListener) {
        this.appointmentsList = appointmentsList;
        this.cancelListener = cancelListener;
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

        String name = ap.getServiceName();
        holder.tvServiceName.setText(name != null ? name : "UNKNOWN SERVICE");
        holder.tvDateTime.setText(TimeUtils.formatDateAndTimeRange(ap.getStartEpoch(), ap.calcEndEpoch()));

        if (ap.getCancelled()) {
            holder.tvStatus.setText("CANCELED");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_cancelled);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnCancel.setOnClickListener(null);
        } else {
            holder.tvStatus.setText("CONFIRMED");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_confirmed);
            holder.btnCancel.setVisibility(View.VISIBLE);
            // Setting a cancel listener for the cancel appointment button
            holder.btnCancel.setOnClickListener(v -> {
                int pos = holder.getBindingAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;

                if (cancelListener != null) {
                    cancelListener.onCancel(appointmentsList.get(pos));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return appointmentsList.size();
    }
}
