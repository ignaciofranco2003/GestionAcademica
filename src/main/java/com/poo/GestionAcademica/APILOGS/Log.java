package com.poo.GestionAcademica.APILOGS;

public class Log {
    private String timestamp;
    private String courseId;
    private String courseName;
    private String event;

    public Log() {
    }

    // Getters y Setters
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String course) {
        this.courseId = course;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}