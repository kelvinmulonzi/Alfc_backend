package com.example.Alfc.prayer;

public enum PrayerStatus {
    /** Visible according to visibility setting. */
    ACTIVE,
    /** Hidden from the wall (admin moderation or auto-hide on too many reports). */
    HIDDEN,
    /** Removed by the submitter or aged out. */
    ARCHIVED
}
