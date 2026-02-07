package com.example.barberappointmentapp.logic;

public final class OverlapUtils {
    private OverlapUtils() {}

    public static boolean overlaps(long start1, long end1, long start2, long end2) {
        return start1 < end2 && start2 < end1;
    }

    public static boolean contains(long outerStart, long outerEnd, long innerStart, long innerEnd) {
        return outerStart <= innerStart && innerEnd <= outerEnd;
    }

}
