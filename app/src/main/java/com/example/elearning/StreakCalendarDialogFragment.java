package com.example.elearning; // Adjust to your package name

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity; // Import Gravity
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Import Button
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import android.content.Context;
import android.content.SharedPreferences;

public class StreakCalendarDialogFragment extends DialogFragment {

    private TextView tvMonthYear;
    private GridLayout calendarGrid;
    private Calendar currentCalendar;
    private ImageView btnCloseCalendar;

    // Đây là Set lưu trữ các ngày đã học (streak days).
    // Trong ứng dụng thực tế, bạn sẽ tải dữ liệu này từ cơ sở dữ liệu hoặc API.
    private Set<Integer> streakDaysInCurrentMonth = new HashSet<>();

    public StreakCalendarDialogFragment() {
        // Required empty public constructor
    }

    public static StreakCalendarDialogFragment newInstance() {
        return new StreakCalendarDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogTheme);
        currentCalendar = Calendar.getInstance();
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("logged_in_username", null);

        if (username != null) {
            DatabaseHelper db = new DatabaseHelper(getContext());
            int userId = db.getUserIdByUsername(username); // bạn cần thêm hàm này nếu chưa có
            streakDaysInCurrentMonth = db.getStreakDaysInMonth(userId,
                    currentCalendar.get(Calendar.YEAR),
                    currentCalendar.get(Calendar.MONTH));
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fire, container, false); // Sử dụng layout_streak_calendar

        tvMonthYear = view.findViewById(R.id.tv_month_year);
        calendarGrid = view.findViewById(R.id.calendar_grid);
        ImageView btnPrevMonth = view.findViewById(R.id.btn_prev_month);
        ImageView btnNextMonth = view.findViewById(R.id.btn_next_month);
        btnCloseCalendar = view.findViewById(R.id.btn_close_calendar);
        Button btnStartLesson = view.findViewById(R.id.btn_start_lesson);
        TextView tvStreakCount = view.findViewById(R.id.tv_streak_count);

        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("logged_in_username", null);

        if (username != null) {
            DatabaseHelper db = new DatabaseHelper(getContext());
            int userId = db.getUserIdByUsername(username);
            int streak = db.getCurrentStreak(userId);
            tvStreakCount.setText(String.valueOf(streak));
        }


        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            // **QUAN TRỌNG:** Trong ứng dụng thực tế, bạn sẽ tải dữ liệu streak
            // cho tháng mới này từ nguồn dữ liệu của bạn.
            streakDaysInCurrentMonth.clear(); // Xóa dữ liệu cũ cho mục đích demo
            updateCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            // **QUAN TRỌNG:** Trong ứng dụng thực tế, bạn sẽ tải dữ liệu streak
            // cho tháng mới này từ nguồn dữ liệu của bạn.
            streakDaysInCurrentMonth.clear(); // Xóa dữ liệu cũ cho mục đích demo


            updateCalendar();
        });

        btnCloseCalendar.setOnClickListener(v -> dismiss());

        btnStartLesson.setOnClickListener(v -> {
            // Thêm ngày hiện tại vào danh sách streak nếu người dùng nhấn nút "BẮT ĐẦU BÀI HỌC"
            // và hoàn thành bài học (logic này nằm ngoài phạm vi đoạn code này).
            // Để demo:
            Calendar today = Calendar.getInstance();
            if (currentCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    currentCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)) {
                streakDaysInCurrentMonth.add(today.get(Calendar.DAY_OF_MONTH));
                updateCalendar(); // Cập nhật lại lịch để highlight ngày mới
            }
            // Logic để bắt đầu bài học thực tế của bạn sẽ ở đây
            // Toast.makeText(getContext(), "Bắt đầu bài học!", Toast.LENGTH_SHORT).show();
            // dismiss();
        });

        updateCalendar();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().setCancelable(true);
        }
    }

    private void updateCalendar() {
        String monthName = currentCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        if (monthName != null && monthName.length() > 0) {
            monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();
        } else {
            monthName = "";
        }
        tvMonthYear.setText(String.format(Locale.getDefault(), "%s %d", monthName, currentCalendar.get(Calendar.YEAR)));

        calendarGrid.removeAllViews();

        Calendar tempCalendar = (Calendar) currentCalendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK);
        // Điều chỉnh để Thứ Hai là ngày đầu tuần (0 cho Thứ Hai) dựa trên Calendar.MONDAY = 2
        // Nếu firstDayOfWeek là Chủ Nhật (1), startDayOffset sẽ là 6 để đặt nó ở cuối hàng tuần đầu tiên.
        // Nếu firstDayOfWeek là Thứ Hai (2), startDayOffset sẽ là 0.
        int startDayOffset = (firstDayOfWeek == Calendar.SUNDAY) ? 6 : firstDayOfWeek - Calendar.MONDAY;


        for (int i = 0; i < startDayOffset; i++) {
            TextView emptyDay = new TextView(getContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            emptyDay.setLayoutParams(params);
            calendarGrid.addView(emptyDay);
        }

        int maxDaysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar today = Calendar.getInstance();


        for (int day = 1; day <= maxDaysInMonth; day++) {
            TextView dayView = new TextView(getContext());
            dayView.setText(String.valueOf(day));
            dayView.setGravity(Gravity.CENTER);
            dayView.setPadding(0, 16, 0, 16);
            int paddingVertical = (int) getResources().getDimension(R.dimen.calendar_day_vertical_padding);
            dayView.setPadding(0, paddingVertical, 0, paddingVertical);

            boolean isToday = (currentCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    currentCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    day == today.get(Calendar.DAY_OF_MONTH));

            boolean isStreakDay = streakDaysInCurrentMonth.contains(day);


            if (isStreakDay) {
                dayView.setBackgroundResource(R.drawable.calendar_streak_day_background); // Màu cam
                dayView.setTextColor(Color.BLACK); // Chữ đen
            } else if (isToday) {
                // Nếu là ngày hôm nay và chưa học (không phải streak day), sử dụng viền xám
                dayView.setBackgroundResource(R.drawable.calendar_today_unlearned_background); // Viền xám
                dayView.setTextColor(Color.WHITE); // Chữ trắng
            } else {
                // Các ngày khác: không nền, chữ trắng
                dayView.setTextColor(Color.WHITE);
                dayView.setBackgroundResource(0);
            }

            dayView.setTextSize(16f);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(4, 4, 4, 4);
            dayView.setLayoutParams(params);
            calendarGrid.addView(dayView);
        }
    }


}