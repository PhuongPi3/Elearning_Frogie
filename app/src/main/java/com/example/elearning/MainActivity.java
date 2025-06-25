package com.example.elearning;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.elearning.ui.exercise.FillInTheBlankExerciseFragment;
import com.example.elearning.ui.home.HomeFragment;
import com.example.elearning.ui.home.LessonPartFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements
        FillInTheBlankExerciseFragment.OnExerciseInteractionListener,
        LessonPartFragment.OnLessonProgressListener {
    private BottomNavigationView bv;
    private NavController nc;
    private LinearLayout topNavBar;
    private ImageView iconHeart;
    private LinearLayout heartNotificationPanel;
    private FrameLayout overlayLayout;

    private ImageView iconFire;
    private TextView textFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        bv = findViewById(R.id.bottom_navgation);
        topNavBar = findViewById(R.id.topNavBar);
        iconHeart = findViewById(R.id.icon_heart);
        overlayLayout = findViewById(R.id.overlay_layout);

        iconFire = findViewById(R.id.icon_fire);
        textFire = findViewById(R.id.textfire);

        heartNotificationPanel = overlayLayout.findViewById(R.id.heart_notification_include);

        LinearLayout llWatchAd = heartNotificationPanel.findViewById(R.id.ll_watch_ad);
        LinearLayout llPracticeForHeart = heartNotificationPanel.findViewById(R.id.ll_practice_for_heart);
        Button btnCancel = heartNotificationPanel.findViewById(R.id.btn_cancel);

        llWatchAd.setOnClickListener(v -> {
            System.out.println("Xem quảng cáo - nhận trái tim clicked!");
            overlayLayout.setVisibility(View.GONE);
        });

        llPracticeForHeart.setOnClickListener(v -> {
            System.out.println("Luyện tập để nhận tim clicked!");
            overlayLayout.setVisibility(View.GONE);
        });

        btnCancel.setOnClickListener(v -> {
            overlayLayout.setVisibility(View.GONE);
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);

        if (navHostFragment != null) {
            nc = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bv, nc);
            nc.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.person) {
                    topNavBar.setVisibility(View.GONE);
                                  } else {
                    topNavBar.setVisibility(View.VISIBLE);
                    bv.setVisibility(View.VISIBLE);

                }
                overlayLayout.setVisibility(View.GONE);
            });

        } else {
            System.err.println("Error: NavHostFragment not found at R.id.main_fragment");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            topNavBar.setPadding(
                    topNavBar.getPaddingLeft(),
                    systemBars.top,
                    topNavBar.getPaddingRight(),
                    topNavBar.getPaddingBottom()
            );

            bv.setPadding(
                    bv.getPaddingLeft(),
                    bv.getPaddingTop(),
                    bv.getPaddingRight(),
                    systemBars.bottom
            );

            return insets;
        });

        iconHeart.setOnClickListener(v -> {
            if (overlayLayout.getVisibility() == View.GONE) {
                overlayLayout.setVisibility(View.VISIBLE);
            } else {
                overlayLayout.setVisibility(View.GONE);
            }
        });

        overlayLayout.setOnClickListener(v -> {
            overlayLayout.setVisibility(View.GONE);
        });

        heartNotificationPanel.setOnClickListener(v -> {
            // Do nothing, just to prevent the click event from propagating
        });

        View.OnClickListener showStreakCalendar = v -> {
            StreakCalendarDialogFragment dialogFragment = StreakCalendarDialogFragment.newInstance();
            dialogFragment.show(getSupportFragmentManager(), "streak_calendar_dialog");
        };

        iconFire.setOnClickListener(showStreakCalendar);
        textFire.setOnClickListener(showStreakCalendar);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Removed LessonPartFragment specific logic
                setEnabled(false);
                onBackPressed();
                setEnabled(true);
            }

        });
        SharedPreferences prefs =getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("logged_in_username", null);

        if (username != null) {
            DatabaseHelper db = new DatabaseHelper(this);
            int userId = db.getUserIdByUsername(username);
            int streak = db.getCurrentStreak(userId);
            textFire.setText(String.valueOf(streak));
        }



    }

    // You MUST implement this method from the OnExerciseInteractionListener interface
    @Override
    public void onExerciseCompleted(int lessonId, int exerciseId, boolean isCorrect) {
        Log.d("MainActivity", "Exercise Completed: Lesson " + lessonId + ", Exercise " + exerciseId + ", Correct: " + isCorrect);
        // Add any further logic here, such as updating UI or navigating
    }

    // Implementation of LessonPartFragment.OnLessonProgressListener
    @Override
    public void onLessonFinished(int lessonId, boolean allExercisesCompleted) {
        Log.d("MainActivity", "Lesson " + lessonId + " Finished. All exercises completed: " + allExercisesCompleted);

        // Truyền tín hiệu lesson_completed cho HomeFragment
        Bundle result = new Bundle();
        result.putBoolean("lesson_completed", true);
        getSupportFragmentManager().setFragmentResult("lesson_result", result);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.lesson_fragment_container, new HomeFragment())
                .commit();
        updateStreakDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStreakDisplay(); // mỗi lần quay lại activity thì cập nhật lại
    }

    private void updateStreakDisplay() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("logged_in_username", null);
        if (username != null) {
            DatabaseHelper db = new DatabaseHelper(this);
            int userId = db.getUserIdByUsername(username);
            int currentStreak = db.getCurrentStreak(userId);
            textFire.setText(String.valueOf(currentStreak));
        }
    }


    @Override
    public void onExitLesson() {
        Log.d("MainActivity", "Exit Lesson requested.");

        // Hiển thị lại HomeFragment (hoặc fragment chính của bạn)
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lesson_fragment_container, new HomeFragment())  // Thay fragment_container bằng ID của container trong activity_main.xml
                .commit();
    }
}