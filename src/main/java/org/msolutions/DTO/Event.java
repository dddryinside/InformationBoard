package org.msolutions.DTO;

import java.time.LocalDate;
import java.util.List;

public class Event {
    private int id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TimeInterval> timeIntervals;

    public Event(String name, LocalDate startDate, LocalDate endDate, List<TimeInterval> timeIntervals) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeIntervals = timeIntervals;
    }


    public Event(int id, String generalInformation, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = generalInformation;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public List<TimeInterval> getTimeIntervals() {
        return timeIntervals;
    }

    public void setIntervals(List<TimeInterval> intervals) {
        this.timeIntervals = intervals;
    }

    public void printInfo() {
        System.out.println("id = " + id + "\nname = " + name + "\n" + this.toString());

        if (this.timeIntervals != null) {
            for (TimeInterval timeInterval : this.timeIntervals) {
                System.out.println(timeInterval.toString());
            }
        }
    }

    @Override
    public String toString() {
        return name + " (" + startDate + " - " + endDate + ")";
    }
}
