package com.xperia.kitkat.icons.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.AlarmClock;
import android.widget.RemoteViews;

import com.xperia.kitkat.icons.R;

import java.util.Calendar;

/**
 * Live analog clock widget. Android 12+ draws the seconds hand with AnalogClock itself.
 * On older Android a ViewFlipper of 60 rotated hands ticks every second inside the launcher,
 * and a 1-minute alarm re-syncs it to the real second.
 */
public class XperiaClockProvider extends AppWidgetProvider {
    private static final String ACTION_SYNC = "com.xperia.kitkat.icons.CLOCK_SYNC";

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) update(ctx, mgr, id);
        schedule(ctx);
    }

    @Override
    public void onEnabled(Context ctx) { schedule(ctx); }

    @Override
    public void onDisabled(Context ctx) {
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am != null) am.cancel(syncIntent(ctx));
    }

    @Override
    public void onReceive(Context ctx, Intent intent) {
        super.onReceive(ctx, intent);
        if (ACTION_SYNC.equals(intent.getAction()) && Build.VERSION.SDK_INT < 31) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
            int[] ids = mgr.getAppWidgetIds(new ComponentName(ctx, XperiaClockProvider.class));
            for (int id : ids) {
                RemoteViews rv = new RemoteViews(ctx.getPackageName(), R.layout.widget_clock);
                rv.setInt(R.id.xclock_flip, "setDisplayedChild", Calendar.getInstance().get(Calendar.SECOND));
                mgr.partiallyUpdateAppWidget(id, rv);
            }
        }
    }

    private static PendingIntent syncIntent(Context ctx) {
        Intent i = new Intent(ctx, XperiaClockProvider.class).setAction(ACTION_SYNC);
        return PendingIntent.getBroadcast(ctx, 8, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private static void schedule(Context ctx) {
        if (Build.VERSION.SDK_INT >= 31) return;
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am != null) am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 15000, 60000, syncIntent(ctx));
    }

    static void update(Context ctx, AppWidgetManager mgr, int id) {
        RemoteViews rv = new RemoteViews(ctx.getPackageName(), R.layout.widget_clock);
        if (Build.VERSION.SDK_INT < 31)
            rv.setInt(R.id.xclock_flip, "setDisplayedChild", Calendar.getInstance().get(Calendar.SECOND));
        Intent i = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
        PackageManager pm = ctx.getPackageManager();
        ResolveInfo ri = pm.resolveActivity(i, 0);
        if (ri == null) {
            i = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                    .setPackage("com.sec.android.app.clockpackage");
        }
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        try {
            rv.setOnClickPendingIntent(R.id.xclock_root, PendingIntent.getActivity(ctx, 7, i, flags));
        } catch (Exception ignored) { }
        mgr.updateAppWidget(id, rv);
    }
}
