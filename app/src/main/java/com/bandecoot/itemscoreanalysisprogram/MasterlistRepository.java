package com.bandecoot.itemscoreanalysisprogram;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing Masterlist snapshots tied to Answer Key slots.
 * Snapshots are created when an answer key slot is modified and saved.
 */
public class MasterlistRepository {
    private static final String TAG = "MasterlistRepository";
    private static final String PREFS_NAME = "MasterlistPrefs";
    private static final String KEY_SNAPSHOTS = "masterlist_snapshots";
    
    private final SharedPreferences prefs;
    
    public static class Snapshot {
        public String slotId;
        public String slotName;
        public long savedAtMillis;
        
        public Snapshot(String slotId, String slotName, long savedAtMillis) {
            this.slotId = slotId;
            this.slotName = slotName;
            this.savedAtMillis = savedAtMillis;
        }
        
        public JSONObject toJson() throws JSONException {
            JSONObject obj = new JSONObject();
            obj.put("slotId", slotId);
            obj.put("slotName", slotName);
            obj.put("savedAtMillis", savedAtMillis);
            return obj;
        }
        
        public static Snapshot fromJson(JSONObject obj) {
            String slotId = obj.optString("slotId", "");
            String slotName = obj.optString("slotName", "");
            long savedAtMillis = obj.optLong("savedAtMillis", 0);
            return new Snapshot(slotId, slotName, savedAtMillis);
        }
    }
    
    public MasterlistRepository(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Add a new snapshot when an answer key slot is saved/modified.
     */
    public void addSnapshot(String slotId, String slotName) {
        try {
            List<Snapshot> snapshots = getSnapshots();
            Snapshot newSnapshot = new Snapshot(slotId, slotName, System.currentTimeMillis());
            snapshots.add(0, newSnapshot); // Add to beginning (most recent first)
            saveSnapshots(snapshots);
            Log.d(TAG, "Added snapshot for slot: " + slotName + " (id: " + slotId + ")");
        } catch (Exception e) {
            Log.e(TAG, "Error adding snapshot", e);
        }
    }
    
    /**
     * Get all snapshots, sorted by most recent first.
     */
    public List<Snapshot> getSnapshots() {
        List<Snapshot> snapshots = new ArrayList<>();
        try {
            String json = prefs.getString(KEY_SNAPSHOTS, "[]");
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.optJSONObject(i);
                if (obj != null) {
                    snapshots.add(Snapshot.fromJson(obj));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error loading snapshots", e);
        }
        return snapshots;
    }
    
    /**
     * Get snapshots for a specific slot.
     */
    public List<Snapshot> getSnapshotsForSlot(String slotId) {
        List<Snapshot> all = getSnapshots();
        List<Snapshot> filtered = new ArrayList<>();
        for (Snapshot s : all) {
            if (s.slotId.equals(slotId)) {
                filtered.add(s);
            }
        }
        return filtered;
    }
    
    /**
     * Save snapshots to SharedPreferences.
     */
    private void saveSnapshots(List<Snapshot> snapshots) {
        try {
            JSONArray arr = new JSONArray();
            for (Snapshot s : snapshots) {
                arr.put(s.toJson());
            }
            prefs.edit().putString(KEY_SNAPSHOTS, arr.toString()).apply();
        } catch (JSONException e) {
            Log.e(TAG, "Error saving snapshots", e);
        }
    }
    
    /**
     * Clear all snapshots (for testing/reset).
     */
    public void clearSnapshots() {
        prefs.edit().remove(KEY_SNAPSHOTS).apply();
        Log.d(TAG, "Cleared all snapshots");
    }
}
