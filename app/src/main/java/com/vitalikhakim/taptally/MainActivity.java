package com.vitalikhakim.taptally;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.TextView;

import java.text.NumberFormat;

public class MainActivity extends Activity {

    private static final String PREFS = "taptally";
    private static final String KEY_COUNT = "count";
    private static final String KEY_TOTAL = "total_taps";

    private SharedPreferences prefs;
    private TextView countView;
    private TextView totalView;
    private long count;
    private long totalTaps;
    private final NumberFormat fmt = NumberFormat.getIntegerInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goEdgeToEdge();
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        count = prefs.getLong(KEY_COUNT, 0);
        totalTaps = prefs.getLong(KEY_TOTAL, 0);

        View root = findViewById(R.id.root);
        countView = findViewById(R.id.count);
        totalView = findViewById(R.id.total);

        applyInsets(root);

        findViewById(R.id.tap_area).setOnClickListener(v -> change(+1, v));
        findViewById(R.id.plus).setOnClickListener(v -> change(+1, v));
        findViewById(R.id.minus).setOnClickListener(v -> change(-1, v));
        findViewById(R.id.reset).setOnClickListener(v -> confirmReset());

        render();
    }

    private void change(int delta, View source) {
        if (delta < 0 && count == 0) {
            source.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return;
        }
        count += delta;
        if (delta > 0) totalTaps += delta;
        source.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        save();
        render();
    }

    private void confirmReset() {
        if (count == 0) return;
        new AlertDialog.Builder(this)
                .setTitle(R.string.reset_title)
                .setMessage(R.string.reset_message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.reset, (d, w) -> {
                    count = 0;
                    save();
                    render();
                })
                .show();
    }

    private void render() {
        countView.setText(fmt.format(count));
        totalView.setText(getString(R.string.total_taps, fmt.format(totalTaps)));
    }

    private void save() {
        prefs.edit().putLong(KEY_COUNT, count).putLong(KEY_TOTAL, totalTaps).apply();
    }

    @SuppressWarnings("deprecation")
    private void goEdgeToEdge() {
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            w.setDecorFitsSystemWindows(false);
        } else {
            w.getDecorView().setSystemUiVisibility(
                    w.getDecorView().getSystemUiVisibility()
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        if (Build.VERSION.SDK_INT < 35) {
            w.setStatusBarColor(Color.TRANSPARENT);
            w.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    @SuppressWarnings("deprecation")
    private void applyInsets(View root) {
        final int baseL = root.getPaddingLeft();
        final int baseT = root.getPaddingTop();
        final int baseR = root.getPaddingRight();
        final int baseB = root.getPaddingBottom();
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            int l, t, r, b;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                android.graphics.Insets i = insets.getInsets(
                        WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                l = i.left; t = i.top; r = i.right; b = i.bottom;
            } else {
                l = insets.getSystemWindowInsetLeft();
                t = insets.getSystemWindowInsetTop();
                r = insets.getSystemWindowInsetRight();
                b = insets.getSystemWindowInsetBottom();
            }
            v.setPadding(baseL + l, baseT + t, baseR + r, baseB + b);
            return insets;
        });
        root.requestApplyInsets();
    }
}
