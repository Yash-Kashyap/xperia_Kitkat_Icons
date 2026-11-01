package com.xperia.kitkat.icons.applications;

import androidx.annotation.NonNull;
import candybar.lib.applications.CandyBarApplication;
import com.xperia.kitkat.icons.R;

public class CandyBar extends CandyBarApplication {

    @NonNull
    @Override
    public Class<?> getDrawableClass() {
        return R.drawable.class;
    }

    @NonNull
    @Override
    public Configuration onInit() {
        Configuration configuration = new Configuration();

        configuration.setAutomaticIconsCountEnabled(true);
        configuration.setDashboardThemingEnabled(true);
        configuration.setGenerateAppFilter(true);
        configuration.setGenerateAppMap(false);
        configuration.setGenerateThemeResources(true);
        configuration.setIncludeIconRequestToEmailBody(true);
        configuration.setShadowEnabled(false);
        configuration.setShowTabAllIcons(true);
        configuration.setTabAllIconsTitle("All");

        configuration.setCategoryForTabAllIcons(new String[] {
            "Sony Xperia", "KitKat Stock & Google", "Communication", "Media & Music",
            "System & Tools", "Social & Web", "AI & Productivity",
            "Folders & Navigation", "Alphabet & Numbers", "Alts", "Extras & Retro"
        });

        return configuration;
    }
}
