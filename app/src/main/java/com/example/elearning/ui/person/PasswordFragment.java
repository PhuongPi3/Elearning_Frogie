package com.example.elearning.ui.person;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.elearning.DatabaseHelper;
import com.example.elearning.R;
import com.google.android.material.textfield.TextInputEditText;

public class PasswordFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText currentPassword = view.findViewById(R.id.edit_text_current_password);
        TextInputEditText newPassword = view.findViewById(R.id.edit_text_new_password);
        TextInputEditText confirmPassword = view.findViewById(R.id.edit_text_confirm_new_password);
        Button btnComplete = view.findViewById(R.id.btn_complete);

        btnComplete.setOnClickListener(v -> {
            String oldPass = currentPassword.getText().toString().trim();
            String newPass = newPassword.getText().toString().trim();
            String confirmPass = confirmPassword.getText().toString().trim();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(getContext(), "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String username = prefs.getString("logged_in_username", null);
            if (username == null) return;

            DatabaseHelper db = new DatabaseHelper(requireContext());
            if (!db.checkPassword(username, oldPass)) {
                Toast.makeText(getContext(), "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                return;
            }

            db.updatePassword(username, newPass);
            Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

            requireParentFragment().getChildFragmentManager().popBackStack();
        });
    }
}
