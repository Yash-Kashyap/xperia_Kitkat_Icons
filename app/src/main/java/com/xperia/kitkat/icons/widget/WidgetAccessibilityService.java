package com.xperia.kitkat.icons.widget;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityEvent;

/**
 * Minimal accessibility service. It does not read the screen: it only performs the
 * global actions (notifications, recents, lock, ...) and one swipe gesture that opens
 * the launcher's app drawer when the user taps the "Xperia action" widget.
 */
public class WidgetAccessibilityService extends AccessibilityService {

    static volatile WidgetAccessibilityService instance;

    @Override protected void onServiceConnected() { instance = this; }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) { /* intentionally unused */ }

    @Override public void onInterrupt() {}

    @Override public boolean onUnbind(android.content.Intent intent) {
        instance = null;
        return super.onUnbind(intent);
    }

    @Override public void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    /** Swipe up from the middle of the home screen, which opens the app drawer on One UI and most launchers. */
    void openAppDrawer() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float x = dm.widthPixels * 0.5f;
        Path path = new Path();
        path.moveTo(x, dm.heightPixels * 0.50f);
        path.lineTo(x, dm.heightPixels * 0.12f);
        GestureDescription.Builder b = new GestureDescription.Builder();
        b.addStroke(new GestureDescription.StrokeDescription(path, 0, 160));
        dispatchGesture(b.build(), null, null);
    }
}
