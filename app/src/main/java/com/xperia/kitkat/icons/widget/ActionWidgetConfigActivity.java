package com.xperia.kitkat.icons.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.xperia.kitkat.icons.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Shown when the widget is added (and when it is reconfigured): choose an action and an icon. */
public class ActionWidgetConfigActivity extends Activity {

    private static final int REQ_PICK_IMAGE = 41;

    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private boolean shortcutMode;
    private String fileKey;
    private SharedPreferences prefs;
    private String action = WidgetActions.APP_DRAWER;
    private String iconMode = "flat";
    private String appPkg;
    private boolean hasCustom;
    private ImageView preview;
    private TextView appLabel;
    private RadioGroup iconGroup;

    private int dp(int v) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setResult(RESULT_CANCELED);
        Intent in = getIntent();
        shortcutMode = Intent.ACTION_CREATE_SHORTCUT.equals(in.getAction());
        if (in.getExtras() != null)
            widgetId = in.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (!shortcutMode && widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) { finish(); return; }
        prefs = getSharedPreferences(ActionWidgetProvider.PREFS, MODE_PRIVATE);
        fileKey = shortcutMode ? "shortcut" : String.valueOf(widgetId);
        if (!shortcutMode) {
            action = prefs.getString("action_" + widgetId, WidgetActions.APP_DRAWER);
            iconMode = prefs.getString("icon_" + widgetId, "flat");
            appPkg = prefs.getString("app_" + widgetId, null);
        } else {
            new File(getFilesDir(), "widget_icon_shortcut.png").delete();
        }
        hasCustom = new File(getFilesDir(), "widget_icon_" + fileKey + ".png").exists();
        if ("custom".equals(iconMode) && !hasCustom) iconMode = "flat";
        build();
    }

    private TextView header(String t) {
        TextView v = new TextView(this);
        v.setText(t);
        v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        v.setTextColor(Color.parseColor("#2a6fb5"));
        v.setAllCaps(true);
        v.setPadding(0, dp(18), 0, dp(4));
        return v;
    }

    private void build() {
        ScrollView sv = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(16), dp(20), dp(20));
        sv.addView(root);

        TextView title = new TextView(this);
        title.setText(R.string.widget_config_title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        root.addView(title);

        preview = new ImageView(this);
        LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams(dp(84), dp(84));
        pl.gravity = Gravity.CENTER_HORIZONTAL;
        pl.topMargin = dp(12);
        preview.setLayoutParams(pl);
        preview.setBackgroundColor(Color.parseColor("#44667788"));
        root.addView(preview);

        root.addView(header(getString(R.string.widget_action_header)));
        RadioGroup ag = new RadioGroup(this);
        for (int i = 0; i < WidgetActions.IDS.length; i++) {
            RadioButton rb = new RadioButton(this);
            String id = WidgetActions.IDS[i];
            String label = WidgetActions.LABELS[i];
            if (WidgetActions.needsAccessibility(id)) label += "  •";
            rb.setText(label);
            rb.setId(1000 + i);
            rb.setChecked(id.equals(action));
            ag.addView(rb);
        }
        ag.setOnCheckedChangeListener((g, checkedId) -> {
            action = WidgetActions.IDS[checkedId - 1000];
            appLabel.setVisibility(WidgetActions.OPEN_APP.equals(action) ? View.VISIBLE : View.GONE);
            if (WidgetActions.OPEN_APP.equals(action)) pickApp();
            refreshPreview();
        });
        root.addView(ag);

        appLabel = new TextView(this);
        appLabel.setPadding(dp(32), 0, 0, dp(4));
        appLabel.setOnClickListener(v -> pickApp());
        root.addView(appLabel);
        updateAppLabel();

        TextView note = new TextView(this);
        note.setText(R.string.widget_accessibility_note);
        note.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        root.addView(note);

        root.addView(header(getString(R.string.widget_icon_header)));
        iconGroup = new RadioGroup(this);
        String[] names = {getString(R.string.widget_icon_flat), getString(R.string.widget_icon_orb), getString(R.string.widget_icon_custom)};
        String[] modes = {"flat", "orb", "custom"};
        for (int i = 0; i < 3; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(names[i]);
            rb.setId(2000 + i);
            rb.setChecked(modes[i].equals(iconMode));
            iconGroup.addView(rb);
        }
        iconGroup.setOnCheckedChangeListener((g, checkedId) -> {
            String m = modes[checkedId - 2000];
            if ("custom".equals(m) && !hasCustom) {
                pickImage();
                return;
            }
            iconMode = m;
            refreshPreview();
        });
        root.addView(iconGroup);

        Button choose = new Button(this);
        choose.setText(R.string.widget_choose_image);
        choose.setOnClickListener(v -> pickImage());
        root.addView(choose);

        Button save = new Button(this);
        save.setText(R.string.widget_save);
        LinearLayout.LayoutParams sl = new LinearLayout.LayoutParams(-1, -2);
        sl.topMargin = dp(18);
        save.setLayoutParams(sl);
        save.setOnClickListener(v -> save());
        root.addView(save);

        setContentView(sv);
        appLabel.setVisibility(WidgetActions.OPEN_APP.equals(action) ? View.VISIBLE : View.GONE);
        refreshPreview();
    }

    private void updateAppLabel() {
        String name = getString(R.string.widget_no_app);
        if (appPkg != null) {
            try {
                name = getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(appPkg, 0)).toString();
            } catch (Exception ignored) {}
        }
        appLabel.setText(getString(R.string.widget_app_chosen, name));
    }

    private void refreshPreview() {
        if ("custom".equals(iconMode) && hasCustom) {
            Bitmap bm = BitmapFactory.decodeFile(new File(getFilesDir(), "widget_icon_" + fileKey + ".png").getAbsolutePath());
            preview.setImageBitmap(bm);
            return;
        }
        String prefix = "orb".equals(iconMode) ? "widget_orb_" : "widget_flat_";
        int res = ActionWidgetProvider.drawableFor(this, prefix + action);
        if (res == 0) res = ActionWidgetProvider.drawableFor(this, "widget_flat_app_drawer");
        preview.setImageResource(res);
    }

    private void pickApp() {
        final PackageManager pm = getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = new ArrayList<>(pm.queryIntentActivities(main, 0));
        Collections.sort(apps, (a, c) -> a.loadLabel(pm).toString().compareToIgnoreCase(c.loadLabel(pm).toString()));
        final String[] labels = new String[apps.size()];
        for (int i = 0; i < labels.length; i++) labels[i] = apps.get(i).loadLabel(pm).toString();
        new AlertDialog.Builder(this).setTitle(R.string.widget_pick_app)
                .setItems(labels, (d, which) -> {
                    appPkg = apps.get(which).activityInfo.packageName;
                    updateAppLabel();
                }).show();
    }

    private void pickImage() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*").addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(i, getString(R.string.widget_choose_image)), REQ_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req != REQ_PICK_IMAGE) return;
        if (res != RESULT_OK || data == null || data.getData() == null) {
            iconGroup.check(2000 + ("orb".equals(iconMode) ? 1 : 0));
            return;
        }
        try {
            Uri u = data.getData();
            Bitmap src;
            try (InputStream in = getContentResolver().openInputStream(u)) { src = BitmapFactory.decodeStream(in); }
            int max = 256;
            float s = Math.min(1f, max / (float) Math.max(src.getWidth(), src.getHeight()));
            Bitmap out = Bitmap.createScaledBitmap(src, Math.max(1, Math.round(src.getWidth() * s)), Math.max(1, Math.round(src.getHeight() * s)), true);
            try (FileOutputStream fo = new FileOutputStream(new File(getFilesDir(), "widget_icon_" + fileKey + ".png"))) {
                out.compress(Bitmap.CompressFormat.PNG, 100, fo);
            }
            hasCustom = true;
            iconMode = "custom";
            iconGroup.check(2002);
            refreshPreview();
        } catch (Exception e) {
            Toast.makeText(this, R.string.widget_action_failed, Toast.LENGTH_SHORT).show();
            iconGroup.check(2000);
        }
    }

    private void save() {
        if (WidgetActions.OPEN_APP.equals(action) && appPkg == null) {
            Toast.makeText(this, R.string.widget_pick_app, Toast.LENGTH_SHORT).show();
            return;
        }
        if (shortcutMode) {
            setResult(RESULT_OK, buildShortcutResult());
        } else {
            prefs.edit().putString("action_" + widgetId, action).putString("icon_" + widgetId, iconMode)
                    .putString("app_" + widgetId, appPkg).apply();
            ActionWidgetProvider.update(this, AppWidgetManager.getInstance(this), widgetId);
            setResult(RESULT_OK, new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId));
        }
        if (WidgetActions.needsAccessibility(action) && !WidgetActions.isServiceEnabled(this)) {
            new AlertDialog.Builder(this).setTitle(R.string.widget_enable_title)
                    .setMessage(R.string.widget_enable_message)
                    .setPositiveButton(R.string.widget_open_settings, (d, w) -> { WidgetActions.openAccessibilitySettings(this); finish(); })
                    .setNegativeButton(R.string.widget_later, (d, w) -> finish())
                    .setOnCancelListener(d -> finish()).show();
        } else {
            finish();
        }
    }

    private Bitmap currentIconBitmap() {
        Bitmap bm = null;
        if ("custom".equals(iconMode) && hasCustom)
            bm = BitmapFactory.decodeFile(new File(getFilesDir(), "widget_icon_" + fileKey + ".png").getAbsolutePath());
        if (bm == null) {
            String prefix = "orb".equals(iconMode) ? "widget_orb_" : "widget_flat_";
            int res = ActionWidgetProvider.drawableFor(this, prefix + action);
            if (res == 0) res = ActionWidgetProvider.drawableFor(this, "widget_flat_app_drawer");
            bm = BitmapFactory.decodeResource(getResources(), res);
        }
        int s = 192;
        Bitmap out = Bitmap.createBitmap(s, s, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas c = new android.graphics.Canvas(out);
        float k = Math.min((float) s / bm.getWidth(), (float) s / bm.getHeight());
        float w = bm.getWidth() * k, h = bm.getHeight() * k;
        c.drawBitmap(bm, null, new android.graphics.RectF((s - w) / 2, (s - h) / 2, (s + w) / 2, (s + h) / 2),
                new android.graphics.Paint(android.graphics.Paint.FILTER_BITMAP_FLAG));
        return out;
    }

    private Intent buildShortcutResult() {
        Intent target = new Intent(this, ActionShortcutActivity.class).setAction(Intent.ACTION_MAIN)
                .putExtra(ActionShortcutActivity.EXTRA_ACTION, action)
                .putExtra(ActionShortcutActivity.EXTRA_APP, appPkg);
        Intent r = new Intent();
        r.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);
        r.putExtra(Intent.EXTRA_SHORTCUT_NAME, WidgetActions.label(action));
        r.putExtra(Intent.EXTRA_SHORTCUT_ICON, currentIconBitmap());
        return r;
    }
}
