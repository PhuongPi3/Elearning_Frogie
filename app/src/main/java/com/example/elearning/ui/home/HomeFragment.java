package com.example.elearning.ui.home;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.elearning.DatabaseHelper;
import com.example.elearning.Exercise;
import com.example.elearning.Lesson;
import com.example.elearning.R;
// Import LessonPartFragment
import com.example.elearning.ui.home.LessonPartFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.elearning.ui.home.LessonPartFragment.OnLessonProgressListener;

import java.util.List;

public class HomeFragment extends Fragment implements OnLessonProgressListener{

    private LinearLayout lessonsContainer;
    private List<Lesson> lessons;
    private DatabaseHelper dbHelper;
    private SharedPreferences appPrefs; // Để kiểm tra lần chạy đầu tiên

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(getContext());
        // The SharedPreferences and first_run logic for data insertion is not needed here,
        // as DatabaseHelper's onCreate/onUpgrade already handles initial data insertion.
        // Keeping appPrefs if it's used for other fragment-specific preferences.
        appPrefs = getContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        dbHelper.ensureSampleDataExists();
        // Removed the insertSampleData() call as it's handled by DatabaseHelper's onCreate/onUpgrade.
        // The 'first_run' flag for data insertion is also no longer needed here.
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        lessonsContainer = root.findViewById(R.id.lessons_container);

        getParentFragmentManager().setFragmentResultListener("lesson_result", this, (requestKey, result) -> {
            boolean completed = result.getBoolean("lesson_completed", false);
            if (completed) {
                Log.d("HomeFragment", "Lesson result received. Refreshing lessons.");
                lesson();  // Gọi cập nhật UI ở đây
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dbHelper != null && lessonsContainer != null) {
            lessons = dbHelper.getAllLessons();
            buildLessonCircles();
        }
        ensureCorrectVisibility();

        getParentFragmentManager().setFragmentResultListener("lesson_result", this, (requestKey, result) -> {
            boolean completed = result.getBoolean("lesson_completed", false);
            if (completed) {
                Log.d("HomeFragment", "Lesson result received. Refreshing lessons.");
                dbHelper = new DatabaseHelper(getContext());
                lessons = dbHelper.getAllLessons();
                buildLessonCircles();
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lessons = dbHelper.getAllLessons();
        buildLessonCircles();
        getParentFragmentManager().setFragmentResultListener("lesson_result", this, (requestKey, result) -> {
            boolean completed = result.getBoolean("lesson_completed", false);
            if (completed) {
                Log.d("HomeFragment", "Lesson result received. Refreshing lessons.");
                lesson();  // Gọi lại để cập nhật UI bài học
            }
        });

    }

    public void lesson() {
        dbHelper = new DatabaseHelper(getContext());  // Tạo lại để chắc chắn dữ liệu mới
        lessons = dbHelper.getAllLessons();

        lessonsContainer.removeAllViews();  // Đổi tên đúng biến

        for (Lesson lesson : lessons) {
            Button btn = new Button(getContext());
            btn.setText(lesson.getTitle());

            if (lesson.isCompleted()) {
                btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            }

            btn.setOnClickListener(v -> {
                startLesson(lesson);
            });

            lessonsContainer.addView(btn);
        }
    }


    private void ensureCorrectVisibility() {
        if (getActivity() != null) {
            FrameLayout lessonContainer = getActivity().findViewById(R.id.lesson_fragment_container);
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navgation);

            if (lessonContainer != null) {
                lessonContainer.setVisibility(View.GONE);
                Log.d("HomeFragment", "Lesson container hidden");
            }

            if (bottomNav != null) {
                bottomNav.setVisibility(View.VISIBLE);
                Log.d("HomeFragment", "Bottom navigation shown");
            }
        }
    }

    private void buildLessonCircles() {
        DatabaseHelper freshDb = new DatabaseHelper(getContext()); // dùng DB mới
        this.lessons = freshDb.getAllLessons(); // danh sách mới nhất
        lessonsContainer.removeAllViews();
        int currentActiveLessonId = freshDb.getCurrentActiveLessonId();

        Log.d("HomeFragment", "Number of lessons: " + lessons.size());

        for (int i = 0; i < lessons.size(); i++) {
            final Lesson currentLesson = lessons.get(i);
            final int currentLessonIndex = i;
            Log.d("LessonCircle", "Lesson " + currentLesson.getId() + " - Completed: " + currentLesson.isCompleted());

            View lessonCircleView = getLayoutInflater().inflate(R.layout.lesson_circle_item, lessonsContainer, false);
            ImageView background = lessonCircleView.findViewById(R.id.lesson_circle_background);
            ImageView icon = lessonCircleView.findViewById(R.id.lesson_circle_icon);
            TextView progressText = lessonCircleView.findViewById(R.id.lesson_progress_text);

            if (currentLesson.isLocked()) {
                background.setImageResource(R.drawable.circle_locked_background);
                icon.setImageResource(R.drawable.frogie);
                icon.setColorFilter(getResources().getColor(android.R.color.darker_gray, null));
                progressText.setVisibility(View.GONE);
            } else if (currentLesson.isCompleted()) {
                background.setImageResource(R.drawable.circle_active_background);
                icon.setImageResource(R.drawable.ic_check);
                icon.setColorFilter(getResources().getColor(android.R.color.white, null));
                progressText.setVisibility(View.GONE);
            } else if (currentLesson.getId() == currentActiveLessonId) {
                background.setImageResource(R.drawable.circle_active_background);
                icon.setImageResource(R.drawable.frogie);
                icon.setColorFilter(getResources().getColor(android.R.color.white, null));
                progressText.setVisibility(View.GONE);
            } else {
                background.setImageResource(R.drawable.circle_active_background);
                icon.setImageResource(R.drawable.frogie);
                icon.setColorFilter(getResources().getColor(android.R.color.white, null));
                progressText.setVisibility(View.GONE);
            }

            lessonCircleView.setOnClickListener(v -> showLessonPopup(currentLesson, currentLessonIndex, lessonCircleView));
            lessonsContainer.addView(lessonCircleView);

            if (currentLessonIndex < lessons.size() - 1) {
                ImageView path = new ImageView(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.gravity = Gravity.CENTER_HORIZONTAL;
                path.setLayoutParams(params);
                path.setImageResource(R.drawable.path_line);
                lessonsContainer.addView(path);
            }
        }
    }

    private void showLessonPopup(Lesson lesson, int lessonIndex, View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView;

        if (lesson.isLocked()) {
            popupView = inflater.inflate(R.layout.popup_locked_lesson, null);
            TextView tvLockedTitle = popupView.findViewById(R.id.tv_locked_title);
            TextView tvLockedMessage = popupView.findViewById(R.id.tv_locked_message);
            Button btnLockedOk = popupView.findViewById(R.id.btn_locked_ok);

            tvLockedTitle.setText(lesson.getTitle());
            tvLockedMessage.setText("Hãy hoàn thành tất cả các cấp độ phía trên để mở khóa nhé!");

            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );
            popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);

            btnLockedOk.setOnClickListener(v -> popupWindow.dismiss());

            lessonsContainer.post(() -> {
                popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int popupWidth = popupView.getMeasuredWidth();
                int popupHeight = popupView.getMeasuredHeight();

                int[] anchorLocation = new int[2];
                anchorView.getLocationOnScreen(anchorLocation);
                int anchorX = anchorLocation[0];
                int anchorY = anchorLocation[1];

                int anchorWidth = anchorView.getWidth();
                int anchorHeight = anchorView.getHeight();

                int xOffset = anchorX + (anchorWidth / 2) - (popupWidth / 2);
                int yOffset = anchorY + anchorHeight;

                popupWindow.showAtLocation(getView(), Gravity.NO_GRAVITY, xOffset, yOffset);
            });

        } else { // Active or Completed lesson
            popupView = inflater.inflate(R.layout.popup_active_lesson, null);
            TextView tvActiveTitle = popupView.findViewById(R.id.tv_active_title);
            TextView tvLessonProgress = popupView.findViewById(R.id.tv_lesson_progress);
            Button btnStart = popupView.findViewById(R.id.btn_start);

            tvActiveTitle.setText(lesson.getTitle());

            if (lesson.isCompleted()) {
                tvLessonProgress.setText("Bài học đã hoàn thành!");
                btnStart.setText("ÔN TẬP LẠI");
            } else {
                tvLessonProgress.setText("Sẵn sàng để học!");
                btnStart.setText("BẮT ĐẦU");
            }

            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );
            popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);

            btnStart.setOnClickListener(v -> {
                popupWindow.dismiss();
                startLesson(lesson);
            });

            lessonsContainer.post(() -> {
                popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int popupWidth = popupView.getMeasuredWidth();
                int popupHeight = popupView.getMeasuredHeight();

                int[] anchorLocation = new int[2];
                anchorView.getLocationOnScreen(anchorLocation);
                int anchorX = anchorLocation[0];
                int anchorY = anchorLocation[1];

                int anchorWidth = anchorView.getWidth();
                int anchorHeight = anchorView.getHeight();

                int xOffset = anchorX + (anchorWidth / 2) - (popupWidth / 2);
                int yOffset = anchorY + anchorHeight;

                popupWindow.showAtLocation(getView(), Gravity.NO_GRAVITY, xOffset, yOffset);
            });
        }
    }

    private void startLesson(Lesson lesson) {
        if (getActivity() == null) {
            Toast.makeText(getContext(), "Không thể bắt đầu bài học. Dữ liệu bị lỗi.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("HomeFragment", "Starting lesson: " + lesson.getId() + " - " + lesson.getTitle());

        // Kiểm tra xem lesson có exercises không
        List<Exercise> exercises = dbHelper.getExercisesForLesson(lesson.getId());
        if (exercises == null || exercises.isEmpty()) {
            Toast.makeText(getContext(), "Bài học này chưa có bài tập nào!", Toast.LENGTH_LONG).show();
            Log.w("HomeFragment", "No exercises found for lesson " + lesson.getId());
            return;
        }

        dbHelper.setCurrentActiveLessonId(lesson.getId());

        FrameLayout lessonContainer = getActivity().findViewById(R.id.lesson_fragment_container);
        BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navgation);

        if (lessonContainer != null) {
            lessonContainer.setVisibility(View.VISIBLE);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE);
            }

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

            // ***** START LessonPartFragment thay vì ExerciseFragment trực tiếp *****
            LessonPartFragment lessonPartFragment = LessonPartFragment.newInstance(lesson.getId());
            transaction.replace(R.id.lesson_fragment_container, lessonPartFragment);
            transaction.addToBackStack("lesson_" + lesson.getId());
            transaction.commit();

            Log.d("HomeFragment", "LessonPartFragment added to lesson_fragment_container.");
        } else {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy container bài học.", Toast.LENGTH_SHORT).show();
            Log.e("HomeFragment", "lesson_fragment_container is null.");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // No need to set lessonProgressListener here if HomeFragment itself is handling it
        // The LessonPartFragment will find HomeFragment as its parent context
    }

    @Override
    public void onLessonFinished(int lessonId, boolean allExercisesCompleted) {
        Log.d("HomeFragment", "Lesson " + lessonId + " finished. Completed: " + allExercisesCompleted);

        // Quay về giao diện chính
        getParentFragmentManager().beginTransaction()
                .replace(R.id.lesson_fragment_container, new HomeFragment())
                .commit();

        ensureCorrectVisibility();

        // Cập nhật bài học
        dbHelper = new DatabaseHelper(getContext());
        lessons = dbHelper.getAllLessons();
        buildLessonCircles();

        if (allExercisesCompleted) {
            Toast.makeText(getContext(), "Chúc mừng! Bạn đã hoàn thành bài học!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onExitLesson() {
        Log.d("HomeFragment", "Exit lesson requested");

        // Pop back stack để quay về HomeFragment
        if (getParentFragmentManager() != null) {
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.lesson_fragment_container, new HomeFragment());
            ft.commit();
        }

        // Đảm bảo visibility được set đúng
        ensureCorrectVisibility();

        // Cập nhật lại UI
        if (dbHelper != null && lessonsContainer != null) {
            lessons = dbHelper.getAllLessons();
            buildLessonCircles();
        }

        Toast.makeText(getContext(), "Đã thoát khỏi bài học.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}