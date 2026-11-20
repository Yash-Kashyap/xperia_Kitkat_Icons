package com.xperia.kitkat.icons.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.RemoteViews;

import com.xperia.kitkat.icons.R;

import java.io.File;

/** 1x1 home-screen widget that runs one chosen action when tapped. */
public class ActionWidgetProvider extends AppWidgetProvider {

    public static final String PREFS = "xperia_action_widget";
    public static final String ACTION_RUN = "com.xperia.kitkat.icons.widget.RUN";
    public static final String EXTRA_ID = "widget_id";

    @Override
    public void onUpdate(Context context, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) update(context, mgr, id);
    }

    @Override
    public void onDeleted(Context context, int[] ids) {
        SharedPreferences.Editor e = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        for (int id : ids) {
            e.remove("action_" + id).remove("app_" + id).remove("icon_" + id);
            new File(context.getFilesDir(), "widget_icon_" + id + ".png").delete();
        }
        e.apply();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_RUN.equals(intent.getAction())) {
            WidgetActions.run(context, intent.getIntExtra(EXTRA_ID, -1));
            return;
        }
        super.onReceive(context, intent);
    }

    public static int drawableFor(Context c, String name) {
        return c.getResources().getIdentifier(name, "drawable", c.getPackageName());
    }

    public static void update(Context context, AppWidgetManager mgr, int id) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String action = p.getString("action_" + id, WidgetActions.APP_DRAWER);
        String icon = p.getString("icon_" + id, "flat");
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_action);
        boolean done = false;
        if ("custom".equals(icon)) {
            File f = new File(context.getFilesDir(), "widget_icon_" + id + ".png");
            if (f.exists()) {
                Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath());
                if (b != null) { rv.setImageViewBitmap(R.id.widget_icon, b); done = true; }
            }
        }
        if (!done) {
            String prefix = "orb".equals(icon) ? "widget_orb_" : "widget_flat_";
            int res = drawableFor(context, prefix + action);
            if (res == 0) res = drawableFor(context, "widget_flat_app_drawer");
            rv.setImageViewResource(R.id.widget_icon, res);
        }
        Intent i = new Intent(context, ActionWidgetProvider.class).setAction(ACTION_RUN).putExtra(EXTRA_ID, id);
        i.setData(android.net.Uri.parse("xperia-widget://" + id));
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_IMMUTABLE : 0);
        rv.setOnClickPendingIntent(R.id.widget_root, PendingIntent.getBroadcast(context, id, i, flags));
        mgr.updateAppWidget(id, rv);
    }
}
