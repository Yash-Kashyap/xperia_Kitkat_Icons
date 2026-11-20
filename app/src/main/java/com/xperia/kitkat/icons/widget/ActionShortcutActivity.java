package com.xperia.kitkat.icons.widget;

import android.app.Activity;
import android.os.Bundle;

/** Invisible activity that a home-screen / dock shortcut launches to run its action. */
public class ActionShortcutActivity extends Activity {
    public static final String EXTRA_ACTION = "xa_action";
    public static final String EXTRA_APP = "xa_app";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        String action = getIntent().getStringExtra(EXTRA_ACTION);
        String app = getIntent().getStringExtra(EXTRA_APP);
        if (action != null) WidgetActions.run(getApplicationContext(), action, app);
        finish();
        overridePendingTransition(0, 0);
    }
}
