package com.example.barberappointmentapp.ui.barber;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.barberappointmentapp.R;
import com.example.barberappointmentapp.models.Service;
import com.example.barberappointmentapp.models.TimeOff;
import com.example.barberappointmentapp.utils.TimeUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BarberAddTimeOffFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarberAddTimeOffFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LocalDateTime selectedDateTimeStart;
    private LocalDateTime selectedDateTimeEnd;

    public BarberAddTimeOffFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BarberAddTimeOffFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BarberAddTimeOffFragment newInstance(String param1, String param2) {
        BarberAddTimeOffFragment fragment = new BarberAddTimeOffFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_barber_add_time_off, container, false);

        //----------------------------------BACK BUTTON-------------------------------------------
        ImageButton btnBack = view.findViewById(R.id.btn_back_add_time_off);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity()
                        .getOnBackPressedDispatcher()
                        .onBackPressed();
            }
        });
        //----------------------------------------------------------------------------------------
        TextView tvSelectedDateStart = view.findViewById(R.id.tvSelectedDateStart);
        TextView tvSelectedDateEnd = view.findViewById(R.id.tvSelectedDateEnd);
        EditText etTimeOffReason = view.findViewById(R.id.etTimeOffReason);
        TextView tvError = view.findViewById(R.id.tvErrorAddTimeOff);
        Button btnAddTimeOff = view.findViewById(R.id.btnAddTimeOff);
        TextView tvSuccess = view.findViewById(R.id.tvSuccessAddTimeOff);
        ProgressBar progress = view.findViewById(R.id.progress_add_time_off);


        // Lisener for button pick date with date picker dialog
        // https://www.tutorialspoint.com/android/android_datepicker_control.htm
        //listener for start date
        tvSelectedDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSuccess.setVisibility(View.INVISIBLE);

                long realSystemMillis = System.currentTimeMillis();
                final Calendar c = Calendar.getInstance();
                c.setTimeInMillis(realSystemMillis);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                selectedDateTimeStart = LocalDateTime.of(year, monthOfYear + 1, dayOfMonth, hourOfDay, minute);
                                                String timeStr = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                                                String dateString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year + "  " + timeStr + " \uD83D\uDCC5";
                                                tvSelectedDateStart.setText(dateString);
                                            }
                                        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
                                timePickerDialog.show();
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(realSystemMillis); // Block past dates
                datePickerDialog.show();
            }
        });

        // Lisener for button pick date with date picker dialog
        //listener for end date
        tvSelectedDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSuccess.setVisibility(View.INVISIBLE);
                long realSystemMillis = System.currentTimeMillis();
                final Calendar c = Calendar.getInstance();
                c.setTimeInMillis(realSystemMillis);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                selectedDateTimeEnd = LocalDateTime.of(year, monthOfYear + 1, dayOfMonth, hourOfDay, minute);

                                                String timeStr = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                                                String dateString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year + "  " + timeStr + " \uD83D\uDCC5";

                                                tvSelectedDateEnd.setText(dateString);
                                            }
                                        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
                                timePickerDialog.show();
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(realSystemMillis); // Block past dates
                datePickerDialog.show();
            }
        });

        // listener for button add time off
        btnAddTimeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = etTimeOffReason.getText().toString().trim();

                // validation
                if (reason.isEmpty() || selectedDateTimeStart == null || selectedDateTimeEnd == null) {
                    tvError.setText("Please fill all fields!");
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }

                long startDateTime = TimeUtils.toLong(selectedDateTimeStart);
                long endDateTime = TimeUtils.toLong(selectedDateTimeEnd);

                if (endDateTime <= startDateTime) {
                    tvError.setText("End time must be after start time!");
                    tvError.setVisibility(View.VISIBLE);
                    return;
                }

                progress.setVisibility(View.VISIBLE);
                TimeOff newTimeoff = TimeOff.create(startDateTime, endDateTime, reason);

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference("settings").child("timeOffs").child(newTimeoff.getId());

                ref.setValue(newTimeoff).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progress.setVisibility(View.GONE);
                                tvError.setVisibility(View.INVISIBLE);
                                tvSuccess.setVisibility(View.VISIBLE);

                                tvSelectedDateStart.setText(R.string.hint_select_start_date);
                                tvSelectedDateEnd.setText(R.string.hint_select_end_date);
                                etTimeOffReason.setText("");

                                selectedDateTimeStart = null;
                                selectedDateTimeEnd = null;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progress.setVisibility(View.GONE);
                                tvSuccess.setVisibility(View.INVISIBLE);
                                Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });


        return view;
    }

}
