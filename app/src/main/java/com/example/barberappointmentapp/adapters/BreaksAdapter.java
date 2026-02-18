package com.example.barberappointmentapp.adapters;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.models.Break;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BreaksAdapter extends RecyclerView.Adapter<com.example.barberappointmentapp.adapters.BreaksAdapter.myViewHolder> {
    private ArrayList<Break> breaksList;

    public BreaksAdapter(ArrayList<Break> breaksList) {
        this.breaksList = breaksList;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView tvBreakTimeRange;
        ImageButton btnRemoveBreak;

        public myViewHolder(View itemView) {
            super(itemView);

            tvBreakTimeRange = itemView.findViewById(R.id.tvBreakTimeRange);
            btnRemoveBreak = itemView.findViewById(R.id.btnRemoveBreak);

            //================================ remove break button listener=========================================================
            btnRemoveBreak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition(); // get the live position
                    if (position == RecyclerView.NO_POSITION) return;

                    Break clickedBreak = breaksList.get(position);
                    // Alert dialog: "are you sure you want to cancel appointment?"
                    // // https://developer.android.com/develop/ui/views/components/dialogs#AddButtons
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Remove Break")
                            .setMessage("Are you sure you want to remove break?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Update Firebase
                                    String dayOfWeek = clickedBreak.getStringDayOfWeek();
                                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                                    DatabaseReference ref = db.getReference("settings").child("workingdays").child(dayOfWeek).child("breaks").child(clickedBreak.getId());

                                    // https://firebase.google.com/docs/database/android/read-and-write#delete_data
                                    ref.removeValue() // remove the service from the database
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    breaksList.remove(position); // remove from the list after removing from DB
                                                    // https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter#notifyDataSetChanged()
                                                    notifyDataSetChanged(); // user removed an item -> notify the change
                                                    Toast.makeText(v.getContext(), "Break removed successfully", Toast.LENGTH_SHORT).show();
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
    public BreaksAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_break, parent, false);

        return new BreaksAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BreaksAdapter.myViewHolder holder, int position) {
        Break breakObj = breaksList.get(position);

        holder.tvBreakTimeRange.setText(breakObj.toString());
    }

    @Override
    public int getItemCount() {
        return breaksList.size();
    }
}

