// ui/home/LessonPartFragment.java
package com.example.elearning.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.elearning.DatabaseHelper;
import com.example.elearning.Exercise;
import com.example.elearning.MainActivity;
import com.example.elearning.R;
import com.example.elearning.ui.exercise.FillInTheBlankExerciseFragment;
import com.example.elearning.ui.exercise.ListenAndChooseExerciseFragment;

import java.util.List;
import java.util.ArrayList;

public class LessonPartFragment extends Fragment implements
        FillInTheBlankExerciseFragment.OnExerciseInteractionListener,
        ListenAndChooseExerciseFragment.OnExerciseInteractionListener{

    private static final String ARG_LESSON_ID = "lesson_id";
    private static final String TAG = "LessonPartFragment";

    private int lessonId;
    private DatabaseHelper dbHelper;
    private List<Exercise> exercisesForLesson;
    private int currentExerciseIndex = 0;
    private int heartsCount = 5;

    private ProgressBar lessonProgressBar;
    private TextView tvHeartsCount;
    private ImageView btnExit;

    // Listener for communicating with MainActivity
    private OnLessonProgressListener lessonProgressListener;

    // Interface to communicate lesson progress/completion back to MainActivity
    public interface OnLessonProgressListener {
        void onLessonFinished(int lessonId, boolean allExercisesCompleted);
        void onExitLesson();
    }

    public LessonPartFragment() {
        // Required empty public constructor
    }

    public static LessonPartFragment newInstance(int lessonId) {
        LessonPartFragment fragment = new LessonPartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LESSON_ID, lessonId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Tìm HomeFragment trong parent fragments
        Fragment parentFragment = getParentFragment();
        while (parentFragment != null) {
            if (parentFragment instanceof OnLessonProgressListener) {
                lessonProgressListener = (OnLessonProgressListener) parentFragment;
                break;
            }
            parentFragment = parentFragment.getParentFragment();
        }

        // Nếu không tìm thấy trong parent fragments, thử tìm trong activity
        if (lessonProgressListener == null && context instanceof OnLessonProgressListener) {
            lessonProgressListener = (OnLessonProgressListener) context;
        }

        if (lessonProgressListener == null) {
            Log.e(TAG, "No OnLessonProgressListener found!");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lessonId = getArguments().getInt(ARG_LESSON_ID);
        }

        Log.d(TAG, "Creating LessonPartFragment for lesson ID: " + lessonId);

        dbHelper = new DatabaseHelper(getContext());
        exercisesForLesson = new ArrayList<>();

        // Load exercises for the current lesson from the database
        try {
            exercisesForLesson = dbHelper.getExercisesForLesson(lessonId);
            Log.d(TAG, "Loaded " + exercisesForLesson.size() + " exercises for lesson " + lessonId);

            // Log each exercise for debugging
            for (int i = 0; i < exercisesForLesson.size(); i++) {
                Exercise ex = exercisesForLesson.get(i);
                Log.d(TAG, "Exercise " + i + ": " + ex.getType() + " - " + ex.getQuestion());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading exercises: " + e.getMessage());
            exercisesForLesson = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "Creating view for LessonPartFragment");
        View view = inflater.inflate(R.layout.fragment_lesson_part, container, false);

        // Initialize all UI components
        lessonProgressBar = view.findViewById(R.id.lesson_progress_bar);
        tvHeartsCount = view.findViewById(R.id.tv_hearts_count);
        btnExit = view.findViewById(R.id.btn_exit_lesson);

        // Check if UI components were found
        if (lessonProgressBar == null) Log.e(TAG, "lessonProgressBar not found in layout");
        if (tvHeartsCount == null) Log.e(TAG, "tvHeartsCount not found in layout");
        if (btnExit == null) Log.e(TAG, "btnExit not found in layout");

        // Set up exit button
        if (btnExit != null) {
            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Exit button clicked");
                    exitLesson();
                }
            });
        }

        // Set up progress bar
        if (exercisesForLesson != null && !exercisesForLesson.isEmpty()) {
            if (lessonProgressBar != null) {
                lessonProgressBar.setMax(exercisesForLesson.size());
                Log.d(TAG, "Progress bar max set to: " + exercisesForLesson.size());
            }
        } else {
            if (lessonProgressBar != null) {
                lessonProgressBar.setMax(1); // Tránh chia cho 0
            }
        }

        updateProgressBar();
        updateHeartsCount();

        // Start first exercise only if this is a new instance
        if (savedInstanceState == null) {
            if (exercisesForLesson != null && !exercisesForLesson.isEmpty()) {
                Log.d(TAG, "Starting first exercise");
                displayExercise(exercisesForLesson.get(currentExerciseIndex));
            } else {
                Log.w(TAG, "No exercises found for lesson " + lessonId);
                showNoExercisesError();
            }
        }

        return view;
    }

    private void showNoExercisesError() {
        Toast.makeText(getContext(), "Không có bài tập nào cho bài học này.", Toast.LENGTH_SHORT).show();
        exitLesson();
    }

    private void exitLesson() {
        if (lessonProgressListener != null) {
            lessonProgressListener.onExitLesson();
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    private void displayExercise(Exercise exercise) {
        Log.d(TAG, "Displaying exercise: " + exercise.getType() + " - " + exercise.getQuestion());

        Fragment exerciseFragment = null;
        try {
            switch (exercise.getType()) {
                case FILL_IN_THE_BLANK:
                    exerciseFragment = FillInTheBlankExerciseFragment.newInstance(lessonId, exercise.getId());
                    break;
                case LISTEN_AND_CHOOSE:
                    exerciseFragment = ListenAndChooseExerciseFragment.newInstance(lessonId, exercise.getId());
                    break;
                default:
                    Log.w(TAG, "Unknown exercise type: " + exercise.getType());
                    handleUnknownExerciseType(exercise);
                    return;
            }

            if (exerciseFragment != null) {
                // Replace the current content with the exercise fragment
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.exercise_content_container, exerciseFragment);
                transaction.commit();
                Log.d(TAG, "Exercise fragment added to container");
            } else {
                Log.e(TAG, "Failed to create exercise fragment");
                moveToNextExercise();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error displaying exercise: " + e.getMessage());
            moveToNextExercise();
        }
    }

    private void handleUnknownExerciseType(Exercise exercise) {
        Toast.makeText(getContext(), "Loại bài tập không xác định: " + exercise.getType(), Toast.LENGTH_SHORT).show();
        // Skip this exercise and move to the next one
        moveToNextExercise();
    }

    private void moveToNextExercise() {
        currentExerciseIndex++;
        updateProgressBar();

        Log.d(TAG, "Moving to exercise index: " + currentExerciseIndex);

        if (currentExerciseIndex < exercisesForLesson.size()) {
            displayExercise(exercisesForLesson.get(currentExerciseIndex));
        } else {
            finishLesson(true);
        }
    }


    @Override
    public void onExerciseCompleted(int lessonId, int exerciseId, boolean isCorrect) {
        Log.d(TAG, "Exercise " + exerciseId + " in Lesson " + lessonId + " completed. Correct: " + isCorrect);

        if (!isCorrect) {
            heartsCount--;
            updateHeartsCount();
            if (heartsCount <= 0) {
                Toast.makeText(getContext(), "Bạn đã hết tim! Thử lại sau.", Toast.LENGTH_LONG).show();
                finishLesson(false);
                return;  // Trường hợp sai + hết tim thì kết thúc luôn
            }
            return;
        }

        moveToNextExercise();

        // Check if all exercises are completed
        if (currentExerciseIndex >= exercisesForLesson.size()) {
            Log.d(TAG, "All exercises in lesson " + lessonId + " completed.");
            Toast.makeText(getContext(), "Bạn đã hoàn thành bài học!", Toast.LENGTH_LONG).show();

            // Mark lesson as completed
            try {
                if (dbHelper != null) {
                    dbHelper.markLessonAsCompleted(lessonId);
                    dbHelper.unlockNextLesson(lessonId);

                    // --- Bổ sung XP và streak ---
                    SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    String username = prefs.getString("logged_in_username", null);

                    if (username != null) {
                        int userId = dbHelper.getUserIdByUsername(username);

                        int earnedXp = 30;   // +30 điểm kinh nghiệm
                        int streak = dbHelper.calculateAndInsertStreak(userId); // dùng logic mới
                        dbHelper.updateUserProgress(userId, lessonId, earnedXp, streak);
                        Log.d(TAG, "XP và streak đã được cập nhật cho userId: " + userId);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error marking lesson as completed: " + e.getMessage());
            }

            finishLesson(true);
        }
    }

    private void finishLesson(boolean allExercisesCompleted) {
        Log.d(TAG, "Finishing lesson " + lessonId + ", all completed: " + allExercisesCompleted);
        // Gửi thông báo về HomeFragment
        Bundle result = new Bundle();
        result.putBoolean("lesson_completed", true);
        getParentFragmentManager().setFragmentResult("lesson_result", result);

        if (lessonProgressListener != null) {
            lessonProgressListener.onLessonFinished(lessonId, allExercisesCompleted);
        } else {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }


    private void updateProgressBar() {
        if (lessonProgressBar != null) {
            lessonProgressBar.setProgress(currentExerciseIndex);
            Log.d(TAG, "Progress updated: " + currentExerciseIndex + "/" + exercisesForLesson.size());
        }
    }

    private void updateHeartsCount() {
        if (tvHeartsCount != null) {
            tvHeartsCount.setText(String.valueOf(heartsCount));
            Log.d(TAG, "Hearts count updated: " + heartsCount);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        lessonProgressListener = null;
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}