package com.example.elearning;

import java.util.ArrayList;
import java.util.List;

public class Lesson {

    private int id;
    private String title;
    private boolean isLocked;
    private boolean isCompleted;
    // Bỏ exerciseId và exerciseType vì một Lesson có thể có nhiều Exercises

    public Lesson(int id, String title, boolean isLocked, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.isLocked = isLocked;
        this.isCompleted = isCompleted;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public boolean isLocked() { return isLocked; }
    public boolean isCompleted() { return isCompleted; }

    // Bỏ getter cho exerciseId và exerciseType

    public void setCompleted(boolean completed) { isCompleted = completed; }
    public void setLocked(boolean locked) { isLocked = locked; }
}