package com.example.elearning.ui.exercise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log; // Import Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView; // Nếu bạn có TextView cho câu hỏi
import android.widget.Toast;

import com.example.elearning.DatabaseHelper;
import com.example.elearning.Exercise;
import com.example.elearning.R;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class ListenAndChooseExerciseFragment extends Fragment {

    private static final String ARG_EXERCISE_ID = "exercise_id";
    private static final String ARG_LESSON_ID = "lesson_id"; // Giữ lại lessonId

    private int exerciseId;
    private int lessonId;

    private DatabaseHelper dbHelper;
    private Exercise currentExercise;
    private MediaPlayer mediaPlayer;

    private ImageButton btnPlayAudio; // Đổi tên biến cho chuẩn
    private LinearLayout optionsContainer;
    private TextView tvQuestion; // Nếu bạn có TextView cho câu hỏi
    private Button btnCheck; // Thêm nút kiểm tra

    private String selectedAnswer = null;

    // Interface để giao tiếp với Fragment cha (LessonPartFragment)
    public interface OnExerciseInteractionListener {
        void onExerciseCompleted(int lessonId, int exerciseId, boolean isCorrect); // Thống nhất interface
    }
    private OnExerciseInteractionListener listener; // Đổi tên biến cho thống nhất

    public ListenAndChooseExerciseFragment() {}

    // Cập nhật newInstance
    public static ListenAndChooseExerciseFragment newInstance(int lessonId, int exerciseId) {
        ListenAndChooseExerciseFragment fragment = new ListenAndChooseExerciseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LESSON_ID, lessonId);
        args.putInt(ARG_EXERCISE_ID, exerciseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Kiểm tra xem context có phải là cha của Fragment không (ví dụ: LessonPartFragment)
        if (getParentFragment() instanceof OnExerciseInteractionListener) {
            listener = (OnExerciseInteractionListener) getParentFragment();
        } else {
            // Có thể là Activity nếu Fragment này được thêm trực tiếp vào Activity
            if (context instanceof OnExerciseInteractionListener) {
                listener = (OnExerciseInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString() + " or its parentFragment must implement OnExerciseInteractionListener");
            }
        }
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exerciseId = getArguments().getInt(ARG_EXERCISE_ID);
            lessonId = getArguments().getInt(ARG_LESSON_ID);
            Log.d("ListenAndChoose", "Received Exercise ID: " + exerciseId + ", Lesson ID: " + lessonId);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listen_and_choose_exercise, container, false);

        btnPlayAudio = view.findViewById(R.id.btn_play_audio);
        optionsContainer = view.findViewById(R.id.options_container);
        // Nếu có TextView cho câu hỏi: // Đảm bảo ID này tồn tại trong layout
        btnCheck = view.findViewById(R.id.btn_check); // Đảm bảo ID này tồn tại

        loadExerciseData();

        btnPlayAudio.setOnClickListener(v -> playAudio());
        btnCheck.setOnClickListener(v -> checkAnswer()); // Gắn listener cho nút kiểm tra

        return view;
    }

    private void loadExerciseData() {
        currentExercise = dbHelper.getExerciseById(exerciseId);

        if (currentExercise != null) {
            // Đặt câu hỏi (nếu có, ví dụ: "Nghe và chọn từ đúng:")
            if (tvQuestion != null) {
                tvQuestion.setText(currentExercise.getQuestion());
            }

            if (currentExercise.getOptions() != null) {
                setupOptions(java.util.Arrays.asList(currentExercise.getOptions()));
            } else {
                optionsContainer.removeAllViews();
                Toast.makeText(getContext(), "Không có lựa chọn nào cho bài tập này.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w("ListenAndChoose", "Không tìm thấy dữ liệu bài tập với ID: " + exerciseId);
            Toast.makeText(getContext(), "Không tìm thấy dữ liệu bài tập.", Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onExerciseCompleted(lessonId, exerciseId, false); // Báo lỗi cho LessonPartFragment
            }
        }
    }

    private void playAudio() {
        if (currentExercise != null && currentExercise.getAudioPath() != null && !currentExercise.getAudioPath().isEmpty()) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            // Giả sử audioPath là tên file trong thư mục 'raw'
            // Chuyển đổi "audio/hello_how_are_you.mp3" thành "hello_how_are_you" để tìm trong raw
            String audioFileName = currentExercise.getAudioPath().substring(currentExercise.getAudioPath().lastIndexOf('/') + 1);
            if (audioFileName.contains(".")) {
                audioFileName = audioFileName.substring(0, audioFileName.lastIndexOf('.'));
            }

            int audioResId = getResources().getIdentifier(audioFileName, "raw", requireContext().getPackageName());
            Log.d("ListenAndChoose", "Audio Path: " + currentExercise.getAudioPath() + ", Converted to Res Name: " + audioFileName + ", Res ID: " + audioResId);

            if (audioResId != 0) {
                mediaPlayer = MediaPlayer.create(getContext(), audioResId);
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(mp -> {
                        mp.release();
                        mediaPlayer = null;
                    });
                } else {
                    Toast.makeText(getContext(), "Không thể tạo MediaPlayer. Kiểm tra file âm thanh.", Toast.LENGTH_SHORT).show();
                    Log.e("ListenAndChoose", "MediaPlayer.create returned null for resId: " + audioResId);
                }
            } else {
                Toast.makeText(getContext(), "Không tìm thấy file âm thanh trong 'raw': " + currentExercise.getAudioPath(), Toast.LENGTH_SHORT).show();
                Log.e("ListenAndChoose", "Audio Resource ID not found for: " + audioFileName);
            }
        } else {
            Toast.makeText(getContext(), "Không có file âm thanh để phát.", Toast.LENGTH_SHORT).show();
            Log.w("ListenAndChoose", "Audio path is null or empty for exercise ID: " + exerciseId);
        }
    }

    private void setupOptions(List<String> options) {
        optionsContainer.removeAllViews();

        List<String> shuffledOptions = new ArrayList<>(options);
        Collections.shuffle(shuffledOptions);

        for (String option : shuffledOptions) {
            Button optionButton = (Button) getLayoutInflater().inflate(R.layout.option_button_template, optionsContainer, false);
            optionButton.setText(option);
            optionButton.setOnClickListener(v -> {
                // Bỏ chọn tất cả các nút khác
                for (int i = 0; i < optionsContainer.getChildCount(); i++) {
                    View child = optionsContainer.getChildAt(i);
                    if (child instanceof Button) {
                        child.setBackgroundResource(R.drawable.option_button_normal_background);
                    }
                }
                // Chọn nút hiện tại
                optionButton.setBackgroundResource(R.drawable.option_button_selected_background);
                selectedAnswer = optionButton.getText().toString();
            });
            optionsContainer.addView(optionButton);
        }
    }

    private void checkAnswer() {
        if (selectedAnswer == null) {
            Toast.makeText(getContext(), "Vui lòng chọn một đáp án.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentExercise != null) {
            boolean isCorrect = selectedAnswer.equalsIgnoreCase(currentExercise.getCorrectAnswer());

            if (isCorrect) {
                Toast.makeText(getContext(), "Chính xác!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Sai rồi. Đáp án đúng là: " + currentExercise.getCorrectAnswer(), Toast.LENGTH_LONG).show();
            }

            if (listener != null) {
                listener.onExerciseCompleted(lessonId, exerciseId, isCorrect);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Giải phóng MediaPlayer khi Fragment không còn hiển thị
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        if (dbHelper != null) {
            dbHelper.close();
        }
        if (mediaPlayer != null) { // Đảm bảo giải phóng cả trong onDetach
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}