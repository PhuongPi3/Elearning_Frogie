package com.example.elearning.ui.person;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.elearning.DatabaseHelper;
import com.example.elearning.R;
import com.google.android.material.textfield.TextInputEditText;

public class UsernameFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_username, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText editUsername = view.findViewById(R.id.edit_text_username);
        Button btnChange = view.findViewById(R.id.btn_change);

        btnChange.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            if (TextUtils.isEmpty(newUsername)) {
                Toast.makeText(getContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String oldUsername = prefs.getString("logged_in_username", null);
            if (oldUsername == null) return;

            DatabaseHelper db = new DatabaseHelper(requireContext());
            // Kiểm tra tên mới đã tồn tại chưa
            if (!newUsername.equals(oldUsername) && db.checkUsernameExists(newUsername)) {
                Toast.makeText(getContext(), "Tên người dùng đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }
            // Nếu không trùng thì mới cập nhật
            db.updateUsername(oldUsername, newUsername);
            prefs.edit().putString("logged_in_username", newUsername).apply();

            Toast.makeText(getContext(), "Đổi tên thành công", Toast.LENGTH_SHORT).show();
            requireParentFragment().getChildFragmentManager().popBackStack();
        });
    }
}
