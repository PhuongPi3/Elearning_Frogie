package com.example.elearning.ui.exercise;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// Import any other views you use in your layout, e.g., GridLayout, TextView, Button, etc.
// import android.widget.TextView;
// import android.widget.Button;

import com.example.elearning.R;
import com.example.elearning.DatabaseHelper;
import com.example.elearning.Exercise;

public class MatchingWordsExerciseFragment extends Fragment {

    private static final String ARG_EXERCISE_ID = "exercise_id";
    private static final String ARG_PART_ID = "part_id";
    private static final String ARG_LESSON_ID = "lesson_id";

    private int exerciseId;
    private int partId;
    private int lessonId;

    public MatchingWordsExerciseFragment() {
        // Required empty public constructor
    }

    // Modify newInstance to accept an int (and remove other previous arguments if not needed directly)
    public static MatchingWordsExerciseFragment newInstance(int lessonId, int partId, int exerciseId) { // Thêm lessonId, partId
        MatchingWordsExerciseFragment fragment = new MatchingWordsExerciseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LESSON_ID, lessonId); // Thêm vào Bundle
        args.putInt(ARG_PART_ID, partId);     // Thêm vào Bundle
        args.putInt(ARG_EXERCISE_ID, exerciseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exerciseId = getArguments().getInt(ARG_EXERCISE_ID);
            partId = getArguments().getInt(ARG_PART_ID); // Lấy từ Bundle
            lessonId = getArguments().getInt(ARG_LESSON_ID); // Lấy từ Bundle
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        // Make sure you have fragment_matching_words_exercise.xml in your res/layout
        View view = inflater.inflate(R.layout.fragment_matching_words_exercise, container, false);

        // TODO: Load exercise data using exerciseId and populate UI for Matching Words
        // Fetch the Exercise object from your DatabaseHelper using exerciseId
        // and then set up your UI for matching (e.g., displaying words in two columns)

        return view;
    }

    // You might also need methods for handling drag-and-drop or click-based matching, checking pairs, etc.
}