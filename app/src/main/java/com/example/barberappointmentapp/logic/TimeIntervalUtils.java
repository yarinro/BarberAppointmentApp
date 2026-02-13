package com.example.barberappointmentapp.logic;

import com.example.barberappointmentapp.models.Appointment;
import com.example.barberappointmentapp.models.TimeInterval;
import com.example.barberappointmentapp.models.TimeOff;
import com.example.barberappointmentapp.models.WorkWindow;
import com.example.barberappointmentapp.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public final class TimeIntervalUtils {
    private TimeIntervalUtils() {}

    // converts a WorkWindow (which in minutes format) to an Interval (which in Epoch time format)
    public static TimeInterval workWindowToInterval(WorkWindow w, long dayStartEpoch) {
        if (w == null) return null;

        long start = dayStartEpoch + TimeUtils.minutesToMillis(w.getStartMinute());
        long end   = dayStartEpoch + TimeUtils.minutesToMillis(w.getEndMinute());

        if (end <= start) return null;

        return new TimeInterval(start, end);
    }

    // converts an Appointment (which in minutes format) to an Interval (which in Epoch time format)
    public static TimeInterval appointmentToInterval(Appointment ap) {
        if (ap == null) return null;
        if (ap.getCancelled()) return null;

        long start = ap.getStartEpoch();
        long end = start + TimeUtils.minutesToMillis(ap.getDurationMinutes());

        if (end <= start) return null;

        return new TimeInterval(start, end);
    }

    // converts a TimeOff (which in minutes format) to an Interval (which in Epoch time format)
    public static TimeInterval timeOffToInterval(TimeOff off) {
        if (off == null) return null;

        long start = off.getStartEpoch();
        long end = off.getEndEpoch();

        if (end <= start) return null;

        return new TimeInterval(start, end);
    }

    // merges overlapping intervals and sorts them in ascending order
    public static void sortAndMerge(List<TimeInterval> intervals) {
        if (intervals == null || intervals.size() <= 1) return;

        intervals.sort((a, b) -> Long.compare(a.getStart(), b.getStart()));

        List<TimeInterval> merged = new ArrayList<>();
        TimeInterval cur = intervals.get(0);

        for (int i = 1; i < intervals.size(); i++) {
            TimeInterval next = intervals.get(i);

            // check if intervals overlap. if overlap then merge them
            if (next.getStart() <= cur.getEnd()) {
                cur.setEnd(Math.max(cur.getEnd(), next.getEnd()));
            } else {
                merged.add(cur);
                cur = next;
            }
        }
        merged.add(cur);

        intervals.clear();
        intervals.addAll(merged);
    }
}
