package com.example.elearning.ui.person;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.elearning.DatabaseHelper;
import com.example.elearning.LoginActivity;
import com.example.elearning.R;

public class PersonFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final View topBarLayout = view.findViewById(R.id.top_bar_layout);
        final View topBarContentLayout = view.findViewById(R.id.top_bar_content_layout);
        TextView nameTextView = view.findViewById(R.id.User);
        TextView xpTextView = view.findViewById(R.id.kinhnghiem);
        TextView streakTextView = view.findViewById(R.id.chuoi);

        if (topBarLayout != null && topBarContentLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(topBarLayout, (v, insets) -> { // Apply listener to topBarLayout directly
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

                topBarContentLayout.setPadding(
                        topBarContentLayout.getPaddingLeft(),
                        systemBars.top,
                        topBarContentLayout.getPaddingRight(),
                        topBarContentLayout.getPaddingBottom()
                );
                return insets;
            });
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("logged_in_username", "Chế độ Khách");
        nameTextView.setText(username);

        if (!username.equals("Chế độ Khách")) {
            DatabaseHelper db = new DatabaseHelper(requireContext());
            int userId = db.getUserIdByUsername(username);
            int xp = db.getTotalXpForUser(userId);
            int streak = db.getMaxStreak(userId);
            xpTextView.setText(String.valueOf(xp));
            streakTextView.setText(String.valueOf(streak));

        }
        nameTextView.setOnClickListener(v -> {
            String currentUsername = nameTextView.getText().toString();
            if ("Chế độ Khách".equals(currentUsername)) {
                // Nếu đang ở chế độ khách, chuyển sang LoginActivity
                startActivity(new Intent(requireContext(), LoginActivity.class));
            } else {
                // Đã đăng nhập, mở SettingFragment
                new SettingFragment().show(
                        requireActivity().getSupportFragmentManager(),
                        "setting_dialog"
                );
            }
        });




    }
}