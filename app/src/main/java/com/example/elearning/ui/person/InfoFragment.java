package com.example.elearning.ui.person;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.elearning.MainActivity;
import com.example.elearning.R;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.elearning.DatabaseHelper;


public class InfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView btnChangeName = view.findViewById(R.id.btn_change_name);
        TextView btnChangePassword = view.findViewById(R.id.btn_change_password);
        TextView btnLogout = view.findViewById(R.id.btn_logout);

        EditText editTextName = view.findViewById(R.id.edit_text_name);
        TextView tvEmail = view.findViewById(R.id.edtEmail);

        btnChangeName.setOnClickListener(v -> requireParentFragment().getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_content_container, new UsernameFragment())
                .addToBackStack(null)
                .commit());

        btnChangePassword.setOnClickListener(v -> requireParentFragment().getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_content_container, new PasswordFragment())
                .addToBackStack(null)
                .commit());

        btnLogout.setOnClickListener(v -> showLogoutDialog());



        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("logged_in_username", null);

        if (username != null) {
            DatabaseHelper db = new DatabaseHelper(requireContext());
            SQLiteDatabase readableDb = db.getReadableDatabase();
            Cursor cursor = readableDb.rawQuery("SELECT email FROM users WHERE username = ?", new String[]{username});
            if (cursor.moveToFirst()) {
                String email = cursor.getString(0);
                tvEmail.setText(email);
            }
            cursor.close();
            readableDb.close();

            editTextName.setText(username);
        }


    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_logout_confirmation, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btn_close_dialog).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btn_logout_confirm).setOnClickListener(v -> {
            // Xóa dữ liệu đăng nhập
            SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();

            // Quay về MainActivity ở trạng thái "Chế độ Khách"
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();

            dialog.dismiss();
        });

        dialog.show();
    }
}
