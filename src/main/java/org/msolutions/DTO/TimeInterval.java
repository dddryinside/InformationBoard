package org.msolutions.DTO;

import java.time.LocalTime;

public class TimeInterval {
    private LocalTime startTime;
    private LocalTime endTime;

    public TimeInterval(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isOverlap(TimeInterval timeInterval) {
        if (this.startTime.equals(timeInterval.startTime) && (this.endTime.equals(timeInterval.endTime))) {
            return true;
        } else if (this.startTime.isBefore(timeInterval.startTime) && (this.endTime.equals(timeInterval.startTime))) {
            return true;
        } else if (timeInterval.startTime.isBefore(this.startTime) && timeInterval.endTime.equals(this.startTime)) {
            return true;
        } else if (this.startTime.isBefore(timeInterval.startTime) && (this.endTime.isAfter(timeInterval.startTime))) {
            return true;
        } else if (timeInterval.startTime.isBefore(startTime) && timeInterval.endTime.isAfter(this.startTime)) {
            return true;
        } else {
             return false;
        }
    }

    public String getStartTime() {
        return String.valueOf(startTime);
    }

    public String getEndTime() {
        return String.valueOf(endTime);
    }

    public LocalTime getLocalTimeStartTime() {
        return startTime;
    }

    public LocalTime getLocalTimeEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return startTime + " - " + endTime;
    }
}
