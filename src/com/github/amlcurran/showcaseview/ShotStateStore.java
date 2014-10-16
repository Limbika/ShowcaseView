package com.github.amlcurran.showcaseview;

import android.content.Context;
import android.content.SharedPreferences;

public class ShotStateStore {

    private static final String PREFS_SHOWCASE_INTERNAL = ShotStateStore.class.getName();
    private static final String KEY_SHOT = "shot";
    private static final String KEY_FINISHED = "finished";
    private static final int INVALID_SHOT_ID = -1;
    
    long shotId = INVALID_SHOT_ID;

    private final Context context;

    public ShotStateStore(Context context) {
        this.context = context;
    }

    boolean hasShot() {
        return isSingleShot() && 
        	   isFinished() && 
        	   context
                .getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE)
                .getBoolean(KEY_SHOT + shotId, false);
    }

    boolean isSingleShot() {
        return shotId != INVALID_SHOT_ID;
    }

    void storeShot() {
        if (isSingleShot()) {
            SharedPreferences internal = context.getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE);
            internal.edit().putBoolean(KEY_SHOT + shotId, true).apply();
        }
    }

    void setSingleShot(long shotId) {
        this.shotId = shotId;
    }
    
    public void cleanSharedPrefs() {
    	SharedPreferences internal = context.getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE);
    	internal.edit().clear().commit();
    }
    
    public void setFinished() {
    	SharedPreferences internal = context.getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE);
    	internal.edit().putBoolean(KEY_FINISHED, true);
    }
    
    private boolean isFinished() {
    	return context.getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE).getBoolean(KEY_FINISHED, false);
    }

}