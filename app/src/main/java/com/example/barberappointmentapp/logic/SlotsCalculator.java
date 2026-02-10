package com.example.barberappointmentapp.logic;

import com.example.barberappointmentapp.models.Appointment;
import com.example.barberappointmentapp.models.TimeInterval;
import com.example.barberappointmentapp.models.ScheduleSettings;
import com.example.barberappointmentapp.models.Service;
import com.example.barberappointmentapp.models.Slot;
import com.example.barberappointmentapp.models.TimeOff;
import com.example.barberappointmentapp.models.WorkWindow;
import com.example.barberappointmentapp.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public final class SlotsCalculator {
    private SlotsCalculator() {}

    private static List<TimeInterval> getBlockedIntervals(List<Appointment> appointmentsForDay, List<TimeOff> timeOffsForDay) {
        List<TimeInterval> blocked = new ArrayList<>();

        if (appointmentsForDay != null) {
            for (Appointment ap : appointmentsForDay) {
                TimeInterval i = TimeIntervalUtils.appointmentToInterval(ap);
                if (i != null) blocked.add(i);
            }
        }

        if (timeOffsForDay != null) {
            for (TimeOff off : timeOffsForDay) {
                TimeInterval i = TimeIntervalUtils.timeOffToInterval(off);
                if (i != null) blocked.add(i);
            }
        }

        TimeIntervalUtils.sortAndMerge(blocked);

        return blocked;
    }

    private static boolean isSlotBlocked(long slotStart, long slotEnd, List<TimeInterval> blocked) {
        // IMPORTANT: 'blocked' has to be sorted and merged
        for (TimeInterval blockedInterval : blocked) {
            if (blockedInterval.getStart() >= slotEnd) return false;
            if (OverlapUtils.overlaps(slotStart, slotEnd, blockedInterval.getStart(), blockedInterval.getEnd())) return true;
        }
        return false;
    }

    public static List<Slot> getAvailableSlotsForDay(
            List<Appointment> appointmentsForDay,
            List<WorkWindow> workWindowsForDay,
            List<TimeOff> timeOffsForDay,
            ScheduleSettings scheduleSettings,
            Service service,
            long dayStartEpoch    // from UI
            ){
        // cases that returns empty ArrayList
        if (scheduleSettings == null || service == null) return new ArrayList<>();
        if (workWindowsForDay == null || workWindowsForDay.isEmpty()) return new ArrayList<>();
        if (scheduleSettings.getSlotMinutes() <= 0) return new ArrayList<>();
        if (service.getDurationMinutes() <= 0) return new ArrayList<>();

        List<TimeInterval> blocked = getBlockedIntervals(appointmentsForDay, timeOffsForDay);
        // converting WorkWindows to TimeIntervals
        List<TimeInterval> workIntervals = new ArrayList<>();
        for (WorkWindow w : workWindowsForDay) {
            TimeInterval wi = TimeIntervalUtils.workWindowToInterval(w, dayStartEpoch);
            if (wi != null) workIntervals.add(wi);
        }
        TimeIntervalUtils.sortAndMerge(workIntervals);

        // calculating available slots
        List<Slot> availableSlots = new ArrayList<>();

        long slotStepMillis = TimeUtils.minutesToMillis(scheduleSettings.getSlotMinutes());
        long serviceDurationMillis = TimeUtils.minutesToMillis(service.getDurationMinutes());
        // iterating through work intervals
        for (TimeInterval workInterval : workIntervals) {
            long workIntervalStart = workInterval.getStart();
            long workIntervalEnd = workInterval.getEnd();
            // iterating through work intervals and checking if slot is available (i.e. not blocked)
            // slot =  + service duration
            for (long candidateStart = workIntervalStart;
                 candidateStart + serviceDurationMillis <= workIntervalEnd;
                 candidateStart += slotStepMillis) {

                long candidateEnd = candidateStart + serviceDurationMillis;

                if (!isSlotBlocked(candidateStart, candidateEnd, blocked)) {
                    availableSlots.add(new Slot(candidateStart, candidateEnd));
                }
            }
        }

        return availableSlots;
    }

}
