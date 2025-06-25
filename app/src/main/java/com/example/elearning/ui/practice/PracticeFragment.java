package com.example.elearning.ui.practice;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.elearning.DatabaseHelper;
import com.example.elearning.Exercise;
import com.example.elearning.R;
import com.example.elearning.ui.home.LessonPartFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PracticeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PracticeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PracticeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PracticeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PracticeFragment newInstance(String param1, String param2) {
        PracticeFragment fragment = new PracticeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_practice, container, false);

        Button btnNextLevel = root.findViewById(R.id.btn_next_level);
        btnNextLevel.setOnClickListener(v -> {
            int lessonId = 1; // Giả sử lessonId là 1, bạn có thể thay bằng ID thực tế hoặc truyền vào fragment
            startLesson(lessonId);
        });

        return root;
    }
    private void startLesson(int lessonId) {
        if (getActivity() == null) return;

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        List<Exercise> exercises = dbHelper.getExercisesForLesson(lessonId);
        if (exercises == null || exercises.isEmpty()) {
            Toast.makeText(getContext(), "Không có bài tập cho bài học này.", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.setCurrentActiveLessonId(lessonId);

        FrameLayout lessonContainer = getActivity().findViewById(R.id.lesson_fragment_container);
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navgation);

        if (lessonContainer != null) {
            lessonContainer.setVisibility(View.VISIBLE);
            if (bottomNav != null) bottomNav.setVisibility(View.GONE);

            LessonPartFragment lessonPartFragment = LessonPartFragment.newInstance(lessonId);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.lesson_fragment_container, lessonPartFragment)
                    .addToBackStack("lesson_" + lessonId)
                    .commit();
        } else {
            Toast.makeText(getContext(), "Không tìm thấy container bài học.", Toast.LENGTH_SHORT).show();
        }
    }


}