package com.example.barberappointmentapp.testings;

import androidx.annotation.NonNull;

import com.example.barberappointmentapp.models.ScheduleSettings;
import com.example.barberappointmentapp.models.Service;
import com.example.barberappointmentapp.models.TimeOff;
import com.example.barberappointmentapp.models.WorkWindow;
import com.example.barberappointmentapp.utils.AppConfig;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public final class SeedDataClientBookAppointment {
    private SeedDataClientBookAppointment() {}

    public static void seedAll() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        // מומלץ לטסטינג: לנקות כדי שכל Seed יהיה "נקי"
        db.getReference("scheduleSettings").removeValue();
        db.getReference("services").removeValue();
        db.getReference("workWindows").removeValue();
        db.getReference("timeOffs").removeValue();
        // db.getReference("appointments").removeValue(); // אופציונלי

        seedScheduleSettings(db);
        seedServices(db);

        // חשוב: אם עכשיו 21:45 בישראל, תורים להיום יהיו ריקים -> נזרע למחר
        seedWorkWindowsForTomorrow(db);
        seedTimeOffsForTomorrow(db);
    }

    private static void seedScheduleSettings(@NonNull FirebaseDatabase db) {
        // ScheduleSettings(slotMinutes, maxDaysAhead)
        ScheduleSettings s = new ScheduleSettings(15, 14);
        if (!s.isValid()) {
            throw new IllegalStateException("Seed failed: ScheduleSettings is invalid");
        }
        db.getReference("scheduleSettings").setValue(s);
    }

    private static void seedServices(@NonNull FirebaseDatabase db) {
        Service haircut30 = Service.create("Haircut", 70, 30, true);
        Service beard15   = Service.create("Beard Trim", 40, 15, true);
        Service inactive  = Service.create("Coloring", 120, 45, false); // לא אמור להופיע ללקוח

        db.getReference("services").child(haircut30.getId()).setValue(haircut30);
        db.getReference("services").child(beard15.getId()).setValue(beard15);
        db.getReference("services").child(inactive.getId()).setValue(inactive);
    }

    private static void seedWorkWindowsForTomorrow(@NonNull FirebaseDatabase db) {
        long tomorrowStart = startOfDayMillis(+1);
        int dayOfWeek = dayOfWeekFromDayStart(tomorrowStart);

        WorkWindow w1 = WorkWindow.create(dayOfWeek, 9 * 60, 13 * 60);   // 09:00-13:00
        WorkWindow w2 = WorkWindow.create(dayOfWeek, 16 * 60, 20 * 60);  // 16:00-20:00

        db.getReference("workWindows")
                .child(String.valueOf(dayOfWeek))
                .child(w1.getId())
                .setValue(w1);

        db.getReference("workWindows")
                .child(String.valueOf(dayOfWeek))
                .child(w2.getId())
                .setValue(w2);
    }

    private static void seedTimeOffsForTomorrow(@NonNull FirebaseDatabase db) {
        long tomorrowStart = startOfDayMillis(+1);

        // Break מחר: 10:30–11:15
        long start = tomorrowStart + minutesToMillis(10 * 60 + 30);
        long end   = tomorrowStart + minutesToMillis(11 * 60 + 15);

        TimeOff off = TimeOff.create(start, end, "Break");
        db.getReference("timeOffs").child(off.getId()).setValue(off);
    }

    // ------------------ helpers ------------------

    private static long startOfDayMillis(int offsetDaysFromToday) {
        Calendar c = Calendar.getInstance(AppConfig.APP_TIMEZONE);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DAY_OF_MONTH, offsetDaysFromToday);
        return c.getTimeInMillis();
    }

    private static int dayOfWeekFromDayStart(long dayStartMillis) {
        Calendar c = Calendar.getInstance(AppConfig.APP_TIMEZONE);
        c.setTimeInMillis(dayStartMillis);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    private static long minutesToMillis(int minutes) {
        return minutes * 60_000L;
    }
}
