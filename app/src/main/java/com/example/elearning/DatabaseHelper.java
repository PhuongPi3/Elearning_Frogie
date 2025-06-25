// DatabaseHelper.java
package com.example.elearning;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;


public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "e_learning_db";
    private static final int DATABASE_VERSION = 1;

    // Tên bảng và cột cho bảng LESSONS
    private static final String TABLE_LESSONS = "lessons";
    private static final String COLUMN_LESSON_ID = "id";
    private static final String COLUMN_LESSON_TITLE = "title";
    private static final String COLUMN_LESSON_IS_LOCKED = "is_locked";
    private static final String COLUMN_LESSON_IS_COMPLETED = "is_completed";

    // Tên bảng và cột cho bảng EXERCISES
    private static final String TABLE_EXERCISES = "exercises";
    private static final String COLUMN_EXERCISE_ID = "id";
    private static final String COLUMN_EXERCISE_LESSON_ID = "lesson_id";
    private static final String COLUMN_EXERCISE_TYPE = "type";
    private static final String COLUMN_EXERCISE_QUESTION = "question";
    private static final String COLUMN_EXERCISE_CORRECT_ANSWER = "correct_answer";
    private static final String COLUMN_EXERCISE_OPTIONS = "options";
    private static final String COLUMN_EXERCISE_IS_COMPLETED = "is_completed";
    private static final String COLUMN_EXERCISE_AUDIO_PATH = "audio_path";

    //Tên bảng và cột cho bảng USER
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_CREATED_AT = "created_at";

    // Tên bảng và cột cho bảng user_progress
    private static final String TABLE_USER_PROGRESS = "user_progress";
    private static final String COLUMN_PROGRESS_ID = "id";
    private static final String COLUMN_PROGRESS_USER_ID = "user_id";
    private static final String COLUMN_PROGRESS_LESSON_ID = "lesson_id";
    private static final String COLUMN_PROGRESS_IS_COMPLETED = "is_completed";
    private static final String COLUMN_PROGRESS_COMPLETION_TIME = "completion_time";
    private static final String COLUMN_PROGRESS_STREAK_COUNT = "streak_count";
    private static final String COLUMN_PROGRESS_XP_EARNED = "xp_earned";

    // Tên bảng và cột cho bảng streaks
    private static final String TABLE_STREAKS = "streaks";
    private static final String COLUMN_STREAK_ID = "id";
    private static final String COLUMN_STREAK_USER_ID = "user_id";
    private static final String COLUMN_STREAK_DATE = "date";
    private static final String COLUMN_STREAK_TIMESTAMP = "timestamp";


    // SQL để tạo bảng LESSONS
    private static final String CREATE_TABLE_LESSONS =
            "CREATE TABLE " + TABLE_LESSONS + "("
                    + COLUMN_LESSON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_LESSON_TITLE + " TEXT,"
                    + COLUMN_LESSON_IS_LOCKED + " INTEGER DEFAULT 0,"
                    + COLUMN_LESSON_IS_COMPLETED + " INTEGER DEFAULT 0"
                    + ")";

    // SQL để tạo bảng EXERCISES
    private static final String CREATE_TABLE_EXERCISES =
            "CREATE TABLE " + TABLE_EXERCISES + "("
                    + COLUMN_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_EXERCISE_LESSON_ID + " INTEGER,"
                    + COLUMN_EXERCISE_TYPE + " TEXT,"
                    + COLUMN_EXERCISE_QUESTION + " TEXT,"
                    + COLUMN_EXERCISE_CORRECT_ANSWER + " TEXT,"
                    + COLUMN_EXERCISE_OPTIONS + " TEXT,"
                    + COLUMN_EXERCISE_IS_COMPLETED + " INTEGER DEFAULT 0,"
                    + COLUMN_EXERCISE_AUDIO_PATH + " TEXT,"
                    + "FOREIGN KEY(" + COLUMN_EXERCISE_LESSON_ID + ") REFERENCES " +
                    TABLE_LESSONS + "(" + COLUMN_LESSON_ID + ") ON DELETE CASCADE"
                    + ")";
    // SQL để tạo bảng User
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + "("
                    + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USERNAME + " TEXT UNIQUE,"
                    + COLUMN_EMAIL + " TEXT,"
                    + COLUMN_PASSWORD + " TEXT,"
                    + COLUMN_CREATED_AT + " TEXT"
                    + ")";

    private static final String CREATE_TABLE_USER_PROGRESS =
            "CREATE TABLE " + TABLE_USER_PROGRESS + "("
                    + COLUMN_PROGRESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_PROGRESS_USER_ID + " INTEGER,"
                    + COLUMN_PROGRESS_LESSON_ID + " INTEGER,"
                    + COLUMN_PROGRESS_IS_COMPLETED + " INTEGER DEFAULT 0,"
                    + COLUMN_PROGRESS_COMPLETION_TIME + " TEXT,"
                    + COLUMN_PROGRESS_STREAK_COUNT + " INTEGER DEFAULT 0,"
                    + COLUMN_PROGRESS_XP_EARNED + " INTEGER DEFAULT 0,"
                    + "FOREIGN KEY(" + COLUMN_PROGRESS_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "),"
                    + "FOREIGN KEY(" + COLUMN_PROGRESS_LESSON_ID + ") REFERENCES " + TABLE_LESSONS + "(" + COLUMN_LESSON_ID + ")"
                    + ")";

    private static final String CREATE_TABLE_STREAKS =
            "CREATE TABLE " + TABLE_STREAKS + "("
                    + COLUMN_STREAK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_STREAK_USER_ID + " INTEGER,"
                    + COLUMN_STREAK_DATE + " TEXT,"
                    + COLUMN_STREAK_TIMESTAMP + " INTEGER,"
                    + "FOREIGN KEY(" + COLUMN_STREAK_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                    + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        // Logic chèn dữ liệu mẫu ban đầu được chuyển đến onCreate và onUpgrade
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LESSONS);
        db.execSQL(CREATE_TABLE_EXERCISES);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_STREAKS);
        db.execSQL(CREATE_TABLE_USER_PROGRESS);
        Log.d(TAG, "All tables created.");
        // Chèn dữ liệu mẫu khi cơ sở dữ liệu được tạo lần đầu
        insertInitialSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Xóa các bảng cũ
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        // Tạo lại các bảng
        onCreate(db); // onCreate sẽ tạo lại bảng VÀ chèn dữ liệu mẫu

        // Đặt lại cờ 'first_run' để đảm bảo logic chèn dữ liệu mẫu của HomeFragment chạy lại
        // (nếu bạn vẫn muốn HomeFragment quản lý việc này một phần)
        // Tuy nhiên, với việc insertInitialSampleData() được gọi trong onCreate/onUpgrade,
        // logic 'first_run' trong HomeFragment có thể được đơn giản hóa hoặc loại bỏ.
        // Tạm thời, tôi sẽ để lại cờ này.
        SharedPreferences appPrefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        appPrefs.edit().putBoolean("first_run", true).apply(); // Đặt lại về true sau khi nâng cấp
        Log.d(TAG, "Database upgraded by dropping and recreating all tables, and re-inserting sample data.");
    }

    /**
     * Chèn dữ liệu mẫu ban đầu vào cơ sở dữ liệu.
     * Phương thức này không kiểm tra 'first_run' hoặc 'lessons exist',
     * nó được gọi khi cơ sở dữ liệu được tạo hoặc nâng cấp.
     */

    public boolean registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Kiểm tra tên người dùng đã tồn tại
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false; // Người dùng đã tồn tại
        }

        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_CREATED_AT, String.valueOf(System.currentTimeMillis()));

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }
    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        boolean isLoggedIn = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return isLoggedIn;
    }


    public void insertInitialSampleData(SQLiteDatabase db) {
        Log.d(TAG, "Inserting initial sample data.");
        try {
            db.beginTransaction();

            long lesson1Id = insertLessonInternal(db, "Lesson 1: Greetings", false, false);
            long lesson2Id = insertLessonInternal(db, "Lesson 2: Farewells", true, false);
            long lesson3Id = insertLessonInternal(db, "Lesson 3: Introductions", true, false);
            long lesson4Id = insertLessonInternal(db, "Lesson 4: Age", true, false);
            long lesson5Id = insertLessonInternal(db, "Lesson 5: Numbers", true, false);

            insertExerciseInternal(db, (int) lesson1Id, "FILL_IN_THE_BLANK", "Hello, how ___ you?", "are", null, null);
            insertExerciseInternal(db, (int) lesson1Id, "FILL_IN_THE_BLANK", "Nice to ___ you!", "See", null, null);

            // Lesson 2 exercises
            insertExerciseInternal(db, (int) lesson2Id,  "FILL_IN_THE_BLANK", "Good ___!", "bye", null,null);
            insertExerciseInternal(db, (int) lesson2Id, "FILL_IN_THE_BLANK", "See you ___!", "later", null, null);

            // Lesson 3 exercises
            insertExerciseInternal(db, (int) lesson3Id,  "FILL_IN_THE_BLANK", "My ___ is John.", "name", null, null);
            insertExerciseInternal(db, (int) lesson3Id, "FILL_IN_THE_BLANK", "Match 'Nice' with:", "to meet you",null, null); // Cần xử lý options cho matching

            // Lesson 4 exercises
            insertExerciseInternal(db, (int) lesson4Id, "FILL_IN_THE_BLANK", "I am ___ years old.", "ten", null, null);

            // Lesson 5 exercises
            insertExerciseInternal(db, (int) lesson5Id,  "FILL_IN_THE_BLANK", "One, two, ___.", "three", null, null);

            db.setTransactionSuccessful();
            Log.d(TAG, "Initial sample data inserted successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error inserting initial sample data: " + e.getMessage());
        } finally {
            if (db.inTransaction()) {
                db.endTransaction();
            }
        }
    }

    // Phương thức chèn bài học (nội bộ, cho dữ liệu mẫu)
    private long insertLessonInternal(SQLiteDatabase db, String title, boolean isLocked, boolean isCompleted) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_LESSON_TITLE, title);
        values.put(COLUMN_LESSON_IS_LOCKED, isLocked ? 1 : 0);
        values.put(COLUMN_LESSON_IS_COMPLETED, isCompleted ? 1 : 0);
        long newRowId = db.insert(TABLE_LESSONS, null, values);
        Log.d(TAG, "Inserted lesson internally: " + title + " with ID: " + newRowId);
        return newRowId;
    }

    // Phương thức chèn bài tập (nội bộ, cho dữ liệu mẫu)
    private long insertExerciseInternal(SQLiteDatabase db, int lessonId, String type, String question, String correctAnswer, String[] options, String audioPath) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXERCISE_LESSON_ID, lessonId);
        values.put(COLUMN_EXERCISE_TYPE, type);
        values.put(COLUMN_EXERCISE_QUESTION, question);
        values.put(COLUMN_EXERCISE_CORRECT_ANSWER, correctAnswer);

        if (options != null) {
            JSONArray jsonArray = new JSONArray();
            for (String option : options) {
                jsonArray.put(option);
            }
            values.put(COLUMN_EXERCISE_OPTIONS, jsonArray.toString());
        } else {
            values.put(COLUMN_EXERCISE_OPTIONS, "");
        }

        values.put(COLUMN_EXERCISE_IS_COMPLETED, 0);
        values.put(COLUMN_EXERCISE_AUDIO_PATH, audioPath);
        long newRowId = db.insert(TABLE_EXERCISES, null, values);
        Log.d(TAG, "Inserted exercise internally for lesson " + lessonId + " with ID: " + newRowId);
        return newRowId;
    }

    /**
     * Phương thức này kiểm tra xem có bất kỳ bài học nào tồn tại trong DB hay không.
     * KHÔNG ĐÓNG DB trong phương thức này; để caller xử lý.
     */
    public boolean checkIfLessonsExist(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            String query = "SELECT COUNT(*) FROM " + TABLE_LESSONS;
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
            return false;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            // Không đóng db ở đây
        }
    }


    // Lấy tất cả các bài học từ cơ sở dữ liệu
    public List<Lesson> getAllLessons() {
        List<Lesson> lessonList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_LESSONS;
        SQLiteDatabase db = this.getReadableDatabase(); // Dùng getReadableDatabase cho truy vấn đọc
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LESSON_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LESSON_TITLE));
                    boolean isLocked = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LESSON_IS_LOCKED)) == 1;
                    boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LESSON_IS_COMPLETED)) == 1;

                    lessonList.add(new Lesson(id, title, isLocked, isCompleted));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all lessons: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close(); // Đóng db sau khi sử dụng
        }
        return lessonList;
    }

    /**
     * Lấy một bài tập cụ thể từ cơ sở dữ liệu theo ID.
     */
    public Exercise getExerciseById(int exerciseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Exercise exercise = null;

        try {
            String selectQuery = "SELECT * FROM " + TABLE_EXERCISES +
                    " WHERE " + COLUMN_EXERCISE_ID + " = ?";
            cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(exerciseId)});

            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_ID));
                int lessonId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_LESSON_ID));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_TYPE));
                String question = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_QUESTION));
                String correctAnswer = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_CORRECT_ANSWER));
                String optionsJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_OPTIONS));
                String audioPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_AUDIO_PATH));
                boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_IS_COMPLETED)) == 1;

                String[] options = null;
                if (optionsJson != null && !optionsJson.isEmpty()) {
                    try {
                        JSONArray jsonArray = new JSONArray(optionsJson);
                        options = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            options[i] = jsonArray.getString(i);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing options JSON for exercise " + exerciseId + ": " + e.getMessage());
                    }
                }
                exercise = new Exercise(id, lessonId, Exercise.ExerciseType.valueOf(type), question, correctAnswer, audioPath, options, isCompleted);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting exercise by ID " + exerciseId + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return exercise;
    }


    // Lấy tất cả các bài tập cho một bài học cụ thể
    public List<Exercise> getExercisesForLesson(int lessonId) {
        List<Exercise> exerciseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String selectQuery = "SELECT * FROM " + TABLE_EXERCISES +
                    " WHERE " + COLUMN_EXERCISE_LESSON_ID + " = " + lessonId;
            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_ID));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_TYPE));
                    String question = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_QUESTION));
                    String correctAnswer = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_CORRECT_ANSWER));
                    String optionsJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_OPTIONS));
                    String audioPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_AUDIO_PATH));
                    boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXERCISE_IS_COMPLETED)) == 1;

                    String[] options = null;
                    if (optionsJson != null && !optionsJson.isEmpty()) {
                        try {
                            JSONArray jsonArray = new JSONArray(optionsJson);
                            options = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                options[i] = jsonArray.getString(i);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing options JSON: " + e.getMessage());
                        }
                    }

                    // Corrected constructor call with all 8 arguments
                    exerciseList.add(new Exercise(id, lessonId, Exercise.ExerciseType.valueOf(type), question, correctAnswer, audioPath, options, isCompleted));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting exercises for lesson " + lessonId + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return exerciseList;
    }

    public int getTotalXpForUser(int userId) {
        int totalXp = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(xp_earned) FROM user_progress WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );
        if (cursor.moveToFirst()) {
            totalXp = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return totalXp;
    }
    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
        }
        cursor.close();
        db.close();
        return userId;
    }

    public int getMaxStreak(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT MAX(streak_count) FROM user_progress WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );
        int maxStreak = 0;
        if (cursor.moveToFirst()) {
            maxStreak = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return maxStreak;
    }

    // Lấy các ngày học (streak) trong 1 tháng nhất định cho người dùng
    public Set<Integer> getStreakDaysInMonth(int userId, int year, int month) {
        Set<Integer> streakDays = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Format ngày đầu và cuối tháng theo yyyy-MM-dd
        String startDate = String.format(Locale.US, "%04d-%02d-01", year, month + 1);
        String endDate = String.format(Locale.US, "%04d-%02d-31", year, month + 1);

        String query = "SELECT " + COLUMN_STREAK_DATE +
                " FROM " + TABLE_STREAKS +
                " WHERE " + COLUMN_STREAK_USER_ID + " = ?" +
                " AND " + COLUMN_STREAK_DATE + " BETWEEN ? AND ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId), startDate, endDate
        });

        if (cursor.moveToFirst()) {
            do {
                String dateStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STREAK_DATE));
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    java.util.Date date = sdf.parse(dateStr);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month) {
                        streakDays.add(cal.get(Calendar.DAY_OF_MONTH));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return streakDays;
    }


    public void updateLessonCompletion(int lessonId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LESSON_IS_COMPLETED, isCompleted ? 1 : 0);
        db.update(TABLE_LESSONS, values, COLUMN_LESSON_ID + " = ?",
                new String[]{String.valueOf(lessonId)});
        Log.d(TAG, "Updated lesson " + lessonId + " completion to " + isCompleted);
        db.close();
    }

    public void updateExerciseCompletion(int exerciseId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXERCISE_IS_COMPLETED, isCompleted ? 1 : 0);
        db.update(TABLE_EXERCISES, values, COLUMN_EXERCISE_ID + " = ?",
                new String[]{String.valueOf(exerciseId)});
        Log.d(TAG, "Updated exercise " + exerciseId + " completion to " + isCompleted);
        db.close();
    }
    public void updateUserProgress(int userId, int lessonId, int xp, int streak) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROGRESS_USER_ID, userId);
        values.put(COLUMN_PROGRESS_LESSON_ID, lessonId);
        values.put(COLUMN_PROGRESS_IS_COMPLETED, 1);
        values.put(COLUMN_PROGRESS_COMPLETION_TIME, String.valueOf(System.currentTimeMillis()));
        values.put(COLUMN_PROGRESS_XP_EARNED, xp);
        values.put(COLUMN_PROGRESS_STREAK_COUNT, streak);
        db.insert(TABLE_USER_PROGRESS, null, values);
        db.close();
    }
    public void updateUsername(String oldUsername, String newUsername) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", newUsername);
        db.update("users", values, "username = ?", new String[]{oldUsername});
        db.close();
    }
    public boolean checkPassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"user_id"},
                "username = ? AND password = ?", new String[]{username, password},
                null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }
    public void updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        db.update("users", values, "username = ?", new String[]{username});
        db.close();
    }

    public int getCurrentActiveLessonId() {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        int lessonId = prefs.getInt("current_active_lesson_id", -1); // -1 nếu chưa có

        if (lessonId != -1) {
            Log.d(TAG, "Loaded active lesson ID from prefs: " + lessonId);
            return lessonId;
        }

        // Fallback từ DB nếu SharedPreferences không có
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int activeLessonId = -1;

        try {
            String query = "SELECT " + COLUMN_LESSON_ID + " FROM " + TABLE_LESSONS +
                    " WHERE " + COLUMN_LESSON_IS_COMPLETED + " = 0 AND " + COLUMN_LESSON_IS_LOCKED + " = 0" +
                    " ORDER BY " + COLUMN_LESSON_ID + " ASC LIMIT 1";
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                activeLessonId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LESSON_ID));
            }
            Log.d(TAG, "Fallback active lesson ID from DB: " + activeLessonId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting active lesson ID: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return activeLessonId;
    }


    public void unlockNextLesson(int completedLessonId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LESSON_IS_LOCKED, 0); // Đặt is_locked về false (0)
        // Mở khóa bài học tiếp theo (giả sử ID bài học tuần tự)
        int nextLessonId = completedLessonId + 1;
        int rowsAffected = db.update(TABLE_LESSONS, values, COLUMN_LESSON_ID + " = ?",
                new String[]{String.valueOf(nextLessonId)});
        if (rowsAffected > 0) {
            Log.d(TAG, "Unlocked lesson with ID: " + nextLessonId);
        } else {
            Log.d(TAG, "No lesson found to unlock with ID: " + nextLessonId);
        }
        db.close();
    }
    public void markLessonAsCompleted(int lessonId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_completed", 1);
        db.update("lessons", values, "id = ?", new String[]{String.valueOf(lessonId)});
        values.put(COLUMN_LESSON_IS_COMPLETED, 1);
        db.update(TABLE_LESSONS, values, COLUMN_LESSON_ID + " = ?", new String[]{String.valueOf(lessonId)});
        Log.d(TAG, "Lesson " + lessonId + " marked as completed.");

        db.close();
    }
    public void setCurrentActiveLessonId(int lessonId) {
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putInt("current_active_lesson_id", lessonId).apply();
        Log.d(TAG, "Current active lesson ID set to: " + lessonId);
    }
    public void ensureSampleDataExists() {
        SQLiteDatabase db = this.getWritableDatabase();
        if (!checkIfLessonsExist(db)) {
            insertInitialSampleData(db);
            Log.d(TAG, "Sample data inserted via ensureSampleDataExists()");
        } else {
            Log.d(TAG, "Sample data already exists. Skipping insertion.");
        }
    }
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"user_id"}, "username = ?", new String[]{username}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Downgrading database from version " + oldVersion + " to " + newVersion + ". All data will be lost.");
        db.execSQL("DROP TABLE IF EXISTS lessons");  // hoặc các bảng khác nếu cần
        onCreate(db);
    }
    public int calculateAndInsertStreak(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Calendar today = Calendar.getInstance();
        Calendar yesterday = (Calendar) today.clone();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String todayStr = sdf.format(today.getTime());
        String yesterdayStr = sdf.format(yesterday.getTime());

        // Kiểm tra nếu hôm nay đã học → không tăng
        Cursor todayCursor = db.rawQuery(
                "SELECT COUNT(*) FROM streaks WHERE user_id = ? AND date = ?",
                new String[]{String.valueOf(userId), todayStr});
        if (todayCursor.moveToFirst() && todayCursor.getInt(0) > 0) {
            todayCursor.close();
            db.close();
            return getCurrentStreak(userId); // đã học hôm nay rồi
        }
        todayCursor.close();

        int newStreak = 1;
        // Kiểm tra nếu hôm qua đã học → tăng
        Cursor yesterdayCursor = db.rawQuery(
                "SELECT COUNT(*) FROM streaks WHERE user_id = ? AND date = ?",
                new String[]{String.valueOf(userId), yesterdayStr});
        if (yesterdayCursor.moveToFirst() && yesterdayCursor.getInt(0) > 0) {
            newStreak = getCurrentStreak(userId) + 1;
        }
        yesterdayCursor.close();

        // Chèn ngày hôm nay vào bảng streaks
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("date", todayStr);
        values.put("timestamp", System.currentTimeMillis());
        db.insert("streaks", null, values);

        db.close();
        return newStreak;
    }
    public int getCurrentStreak(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int streak = 0;
        Calendar cal = Calendar.getInstance();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US);

        for (;;) {
            String dateStr = sdf.format(cal.getTime());
            Cursor cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM streaks WHERE user_id = ? AND date = ?",
                    new String[]{String.valueOf(userId), dateStr});

            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                streak++;
                cal.add(Calendar.DAY_OF_YEAR, -1); // lùi về 1 ngày
            } else {
                cursor.close();
                break;
            }
            cursor.close();
        }

        db.close();
        return streak;
    }

}