package com.example.elearning.ui.exercise;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elearning.DatabaseHelper;
import com.example.elearning.Exercise;
import com.example.elearning.R;
// Không cần import LessonPartFragment ở đây nếu chỉ dùng Interface
// import com.example.elearning.ui.home.LessonPartFragment;

public class FillInTheBlankExerciseFragment extends Fragment {

    private static final String ARG_EXERCISE_ID = "exercise_id";
    private static final String ARG_LESSON_ID = "lesson_id";

    private int exerciseId;
    private int lessonId;

    private DatabaseHelper dbHelper;
    private Exercise currentExercise;

    // UI elements
    private TextView tvQuestion;
    private EditText etAnswer;
    private Button btnCheck;

    // Interface để giao tiếp với Fragment cha (LessonPartFragment)
    public interface OnExerciseInteractionListener {
        void onExerciseCompleted(int lessonId, int exerciseId, boolean isCorrect);
    }

    private OnExerciseInteractionListener listener;

    public FillInTheBlankExerciseFragment() {
        // Required empty public constructor
    }

    public static FillInTheBlankExerciseFragment newInstance(int lessonId, int exerciseId) {
        FillInTheBlankExerciseFragment fragment = new FillInTheBlankExerciseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LESSON_ID, lessonId);
        args.putInt(ARG_EXERCISE_ID, exerciseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment parent = getParentFragment();
        if (parent instanceof OnExerciseInteractionListener) {
            listener = (OnExerciseInteractionListener) parent;
        } else {
            throw new RuntimeException("Parent fragment must implement OnExerciseInteractionListener");
        }
        dbHelper = new DatabaseHelper(context); // Khởi tạo DatabaseHelper
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exerciseId = getArguments().getInt(ARG_EXERCISE_ID);
            lessonId = getArguments().getInt(ARG_LESSON_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fill_in_the_blank_exercise, container, false);

        // Initialize UI elements
        tvQuestion = view.findViewById(R.id.tv_question);
        etAnswer = view.findViewById(R.id.et_answer);
        btnCheck = view.findViewById(R.id.btn_check);

        // Load exercise data when view is created
        loadExercise();

        btnCheck.setOnClickListener(v -> {
            checkAnswer(); // Chỉ gọi phương thức checkAnswer()
        });

        return view;
    }

    private void loadExercise() {
        currentExercise = dbHelper.getExerciseById(exerciseId); // Lấy bài tập trực tiếp bằng ID
        if (currentExercise != null) {
            tvQuestion.setText(currentExercise.getQuestion());
            etAnswer.setText(""); // Clear previous answer
            Log.d("FillInTheBlankFragment", "Loaded Exercise: " + currentExercise.getQuestion() + " (ID: " + currentExercise.getId() + ")");
        } else {
            Log.w("FillInTheBlankFragment", "Failed to load exercise with ID: " + exerciseId);
            Toast.makeText(getContext(), "Không tìm thấy bài tập.", Toast.LENGTH_SHORT).show();
            // Báo cho Activity/Fragment cha biết bài tập không tải được (coi như không đúng)
            if (listener != null) {
                listener.onExerciseCompleted(lessonId, exerciseId, false);
            }
        }
    }

    private void checkAnswer() {
        if (currentExercise == null) {
            Toast.makeText(getContext(), "Không có bài tập để kiểm tra.", Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onExerciseCompleted(lessonId, exerciseId, false);
            }
            return;
        }

        String userAnswer = etAnswer.getText().toString().trim();
        String correctAnswer = currentExercise.getCorrectAnswer().trim();
        boolean isCorrect = userAnswer.equalsIgnoreCase(correctAnswer);

        if (isCorrect) {
            Toast.makeText(getContext(), "Chính xác!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Sai rồi. Đáp án đúng là: " + correctAnswer, Toast.LENGTH_LONG).show();
        }

        // Gửi kết quả về LessonPartFragment để nó quyết định tiếp theo
        if (listener != null) {
            listener.onExerciseCompleted(lessonId, exerciseId, isCorrect);
        }

        // Không pop fragment ở đây nữa — để LessonPartFragment xử lý
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null; // Tránh rò rỉ bộ nhớ
        if (dbHelper != null) {
            dbHelper.close(); // Đóng kết nối database khi Fragment bị hủy
        }
    }
}