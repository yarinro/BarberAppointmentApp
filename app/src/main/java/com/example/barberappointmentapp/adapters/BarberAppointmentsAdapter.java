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

public class BarberAppointmentsAdapter extends RecyclerView.Adapter<BarberAppointmentsAdapter.myViewHolder>  {
    // List of appointments
    private ArrayList<Appointment> appointmentsList;
    public interface OnCancelClickListener {
        void onCancel(Appointment ap);
    }
    private OnCancelClickListener cancelListener;
    public BarberAppointmentsAdapter(ArrayList<Appointment> appointmentsList,  OnCancelClickListener cancelListener) {
        this.appointmentsList = appointmentsList;
        this.cancelListener = cancelListener;
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

        return new BarberAppointmentsAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarberAppointmentsAdapter.myViewHolder holder, int position) {
        Appointment ap = appointmentsList.get(position);

        holder.tvClientName.setText(ap.getClientName() != null ? ap.getClientName() : "Unknown client");
        holder.tvClientPhone.setText(ap.getClientPhone() != null ? ap.getClientPhone() : "unknown phone");
        holder.tvServiceName.setText(ap.getServiceName() != null ? ap.getServiceName() : "Unknown service");

        holder.tvDateTime.setText(TimeUtils.formatDateAndTimeRange(ap.getStartEpoch(), ap.calcEndEpoch())); // appointment date & time
        // Appointment status:
        if (ap.getCancelled()) {
            holder.tvStatus.setText("CANCELED");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_cancelled);

            holder.btnCancel.setVisibility(View.GONE);
            holder.btnCancel.setOnClickListener(null);

        } else {
            holder.tvStatus.setText("CONFIRMED");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_badge_confirmed);
            // cancel button
            holder.btnCancel.setVisibility(View.VISIBLE);
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
