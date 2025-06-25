package com.example.elearning.ui.video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.elearning.DatabaseHelper;
import com.example.elearning.R;
import com.example.elearning.ui.home.LessonPartFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class VideoFragment extends Fragment {

    private YouTubePlayerView youTubePlayerNGU;
    private YouTubePlayerView youTubePlayerdino;
    private YouTubePlayerView youTubePlayerUnholy;

    public VideoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        View quizStartLayout = view.findViewById(R.id.quizstart);
        quizStartLayout.setOnClickListener(v -> {
            int lessonId = 1; // ID bài học liên kết với quiz này, bạn có thể thay bằng ID thực tế
            startLesson(lessonId);
        });

        // Initialize YouTubePlayerViews
        youTubePlayerNGU = view.findViewById(R.id.youtube_player_view1);
        youTubePlayerdino = view.findViewById(R.id.youtube_player_view2);
        youTubePlayerUnholy = view.findViewById(R.id.youtube_player_view3);

        // Add LifecycleObservers
        getLifecycle().addObserver(youTubePlayerNGU);
        getLifecycle().addObserver(youTubePlayerdino);
        getLifecycle().addObserver(youTubePlayerUnholy);

        // Load YouTube videos
        // Replace with your actual YouTube video IDs
        loadYouTubeVideo(youTubePlayerNGU, "dQw4w9WgXcQ");
        loadYouTubeVideo(youTubePlayerdino, "NkEE2dBhj4o");
        loadYouTubeVideo(youTubePlayerUnholy, "Uq9gPaIzbe8");

        return view;
    }

    private void loadYouTubeVideo(YouTubePlayerView playerView, String videoId) {
        playerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(videoId, 0);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release YouTubePlayerViews to prevent memory leaks
        if (youTubePlayerNGU != null) {
            youTubePlayerNGU.release();
        }
        if (youTubePlayerdino != null) {
            youTubePlayerdino.release();
        }
        if (youTubePlayerUnholy != null) {
            youTubePlayerUnholy.release();
        }
    }
    private void startLesson(int lessonId) {
        if (getActivity() == null) return;

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        java.util.List<com.example.elearning.Exercise> exercises = dbHelper.getExercisesForLesson(lessonId);
        if (exercises == null || exercises.isEmpty()) {
            Toast.makeText(getContext(), "Không có bài tập cho bài học này.", android.widget.Toast.LENGTH_SHORT).show();
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
        }else {
            android.widget.Toast.makeText(getContext(), "Không tìm thấy container bài học.", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

}