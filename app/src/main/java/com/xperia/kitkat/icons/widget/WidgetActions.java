package com.xperia.kitkat.icons.widget;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraManager;
import android.provider.Settings;
import android.widget.Toast;

import com.xperia.kitkat.icons.R;

/** All actions the 1x1 "Xperia action" widget can run. */
public final class WidgetActions {

    public static final String APP_DRAWER = "app_drawer";
    public static final String NOTIFICATIONS = "notifications";
    public static final String QUICK_SETTINGS = "quick_settings";
    public static final String RECENTS = "recents";
    public static final String LOCK = "lock";
    public static final String POWER = "power";
    public static final String SCREENSHOT = "screenshot";
    public static final String ASSISTANT = "assistant";
    public static final String FLASHLIGHT = "flashlight";
    public static final String OPEN_APP = "open_app";

    public static final String[] IDS = {APP_DRAWER, NOTIFICATIONS, QUICK_SETTINGS, RECENTS, LOCK,
            POWER, SCREENSHOT, ASSISTANT, FLASHLIGHT, OPEN_APP};
    public static final String[] LABELS = {"App drawer", "Expand notifications", "Expand quick settings",
            "Recent apps", "Screen lock", "Power menu", "Screenshot", "Assistant", "Flashlight", "Open an app"};

    private static boolean torchOn = false;

    private WidgetActions() {}

    public static boolean needsAccessibility(String id) {
        return APP_DRAWER.equals(id) || NOTIFICATIONS.equals(id) || QUICK_SETTINGS.equals(id)
                || RECENTS.equals(id) || LOCK.equals(id) || POWER.equals(id) || SCREENSHOT.equals(id);
    }

    public static String label(String id) {
        for (int i = 0; i < IDS.length; i++) if (IDS[i].equals(id)) return LABELS[i];
        return id;
    }

    public static boolean isServiceEnabled(Context c) {
        if (WidgetAccessibilityService.instance != null) return true;
        try {
            String s = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (s == null) return false;
            String me = new ComponentName(c, WidgetAccessibilityService.class).flattenToString();
            for (String part : s.split(":")) if (part.equalsIgnoreCase(me)) return true;
        } catch (Exception ignored) {}
        return false;
    }

    public static void openAccessibilitySettings(Context c) {
        Intent i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(i);
    }

    public static void run(Context c, int widgetId) {
        SharedPreferences p = c.getSharedPreferences(ActionWidgetProvider.PREFS, Context.MODE_PRIVATE);
        run(c, p.getString("action_" + widgetId, APP_DRAWER), p.getString("app_" + widgetId, null));
    }

    public static void run(Context c, String id, String appPkg) {
        if (needsAccessibility(id)) {
            WidgetAccessibilityService s = WidgetAccessibilityService.instance;
            if (s == null) {
                Toast.makeText(c, R.string.widget_enable_accessibility, Toast.LENGTH_LONG).show();
                openAccessibilitySettings(c);
                return;
            }
            switch (id) {
                case APP_DRAWER: s.openAppDrawer(); break;
                case NOTIFICATIONS: s.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS); break;
                case QUICK_SETTINGS: s.performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS); break;
                case RECENTS: s.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS); break;
                case POWER: s.performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG); break;
                case LOCK:
                    if (android.os.Build.VERSION.SDK_INT >= 28) s.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN);
                    break;
                case SCREENSHOT:
                    if (android.os.Build.VERSION.SDK_INT >= 28) s.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT);
                    break;
                default: break;
            }
            return;
        }
        switch (id) {
            case ASSISTANT:
                try {
                    c.startActivity(new Intent(Intent.ACTION_VOICE_COMMAND).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } catch (Exception e) {
                    try { c.startActivity(new Intent(Intent.ACTION_ASSIST).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)); }
                    catch (Exception e2) { Toast.makeText(c, R.string.widget_action_failed, Toast.LENGTH_SHORT).show(); }
                }
                break;
            case FLASHLIGHT:
                try {
                    CameraManager cm = (CameraManager) c.getSystemService(Context.CAMERA_SERVICE);
                    String cam = cm.getCameraIdList()[0];
                    torchOn = !torchOn;
                    cm.setTorchMode(cam, torchOn);
                } catch (Exception e) {
                    Toast.makeText(c, R.string.widget_action_failed, Toast.LENGTH_SHORT).show();
                }
                break;
            case OPEN_APP:
                String pkg = appPkg;
                Intent li = pkg == null ? null : c.getPackageManager().getLaunchIntentForPackage(pkg);
                if (li != null) c.startActivity(li.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                else Toast.makeText(c, R.string.widget_action_failed, Toast.LENGTH_SHORT).show();
                break;
            default: break;
        }
    }
}
