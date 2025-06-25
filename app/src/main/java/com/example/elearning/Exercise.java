package com.example.elearning;

import org.json.JSONArray;
import org.json.JSONException;

public class Exercise {
    // Enum để định nghĩa các loại bài tập
    public enum ExerciseType {
        FILL_IN_THE_BLANK, // Điền từ trong câu
        LISTEN_AND_CHOOSE, // Nghe - chọn từ
        MATCHING_WORDS,    // Ghép từ nghĩa
         }

    private int id;
    private int lessonId; // NEW: ID của bài học mà bài tập này thuộc về
    private ExerciseType type;
    private String question;
    private String correctAnswer;
    private String audioPath;
    private String[] options;
    private boolean isCompleted;

    // Constructor mới bao gồm lessonId, audioPath và nhận ExerciseType trực tiếp
    public Exercise(int id, int lessonId, ExerciseType type, String question, String correctAnswer, String audioPath, String[] options, boolean isCompleted) {
        this.id = id;
        this.lessonId = lessonId; // Gán lessonId
        this.type = type;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.audioPath = audioPath;
        this.options = options;
        this.isCompleted = isCompleted;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public int getLessonId() { // NEW: Getter cho lessonId
        return lessonId;
    }

    public ExerciseType getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public String[] getOptions() {
        return options;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    // --- Setters ---
    public void setId(int id) {
        this.id = id;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
    }

    public void setType(ExerciseType type) {
        this.type = type;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // Helper method to convert JSON string to String array for options
    public static String[] fromJsonStringToArray(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return new String[0];
        }
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            String[] arr = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                arr[i] = jsonArray.getString(i);
            }
            return arr;
        } catch (JSONException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    // Helper method to convert String array to JSON string for options
    public static String fromArrayToJsonString(String[] array) {
        if (array == null) {
            return "";
        }
        JSONArray jsonArray = new JSONArray();
        for (String item : array) {
            jsonArray.put(item);
        }
        return jsonArray.toString();
    }
}