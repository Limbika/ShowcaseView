package com.github.amlcurran.showcaseview;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ShotStateStore {

    private static final String PREFS_SHOWCASE_INTERNAL = ShotStateStore.class.getName();
    private static final String KEY_SHOT = "shot";
    private static final String KEY_FINISHED = "finished";
    private static final long INVALID_SHOT_ID = -1;
    
    long shotId = INVALID_SHOT_ID;

    private final Context context;

    public ShotStateStore(Context context) {
        this.context = context;
    }
    
    /**
     * Clean the shared prefrerences.
     */
    public void clean() {
    	SharedPreferences internal = context.getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE);
    	internal.edit().clear().commit();
    }
    
    /**
     * Set all the showcasases as finished.
     */
    public void finished() {
    	SharedPreferences internal = context.getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE);
    	internal.edit().putBoolean(KEY_FINISHED, true).apply();
    }
    
    /**
     * Remove all the showcases as finished flag. 
     */
    public void restore() {
    	SharedPreferences internal = context.getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE);
    	internal.edit().putBoolean(KEY_FINISHED, false).apply();
    }

    boolean hasShot() {
    	return isFinished() || (isSingleShot() && isShown());
    }

    boolean isSingleShot() {
        return shotId != INVALID_SHOT_ID;
    }
    
    boolean isShown() {
    	return context
    		.getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE)
    		.getBoolean(KEY_SHOT + shotId, false);
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
    
    boolean isFinished() {
    	return context.getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE).getBoolean(KEY_FINISHED, false);
    }

}