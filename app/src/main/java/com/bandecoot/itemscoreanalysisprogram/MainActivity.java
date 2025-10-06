package com.bandecoot.itemscoreanalysisprogram;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.HapticFeedbackConstants;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.res.ColorStateList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ISA_VISION";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;

    // UI components
    private MaterialToolbar topAppBar;
    private LinearLayout mainLayout, answerKeyLayout, testHistoryLayout, scanSessionLayout;
    private EditText studentNameInput, sectionNameInput, examNameInput;
    private EditText questionNumberInput, removeQuestionInput;
    private MaterialAutoCompleteTextView answerDropdown;
    private Button startScanButton, setupButton, viewHistoryButton;
    private Button saveAnswerButton, removeAnswerButton, clearButton, backButton;
    private Button historyBackButton, tryAgainButton, captureResultButton, cancelScanButton;
    private Button confirmParsedButton, importPhotosButton;
    private TextView currentKeyTextView, sessionScoreTextView, parsedLabel;
    private MaterialCardView resultsCard;
    private LinearLayout testHistoryList, parsedAnswersContainer;
    private TextureView cameraPreviewTextureView;
    private View shutterView;
    
    // Slot management
    private MaterialAutoCompleteTextView slotSelector;
    private Button btnSlotNew, btnSlotRename, btnSlotDelete, btnSlotImport, btnSlotExport;
    // Slot management
    private static final String PREFS_SLOTS = "answer_key_slots";
    private static final String PREF_CURRENT_SLOT = "current_slot";
    private static final String PREF_LAST_SECTION = "last_section";
    private static final String PREF_LAST_EXAM = "last_exam";
    private String currentSlotId = "default";
    private final HashMap<String, SlotData> slots = new HashMap<>();
    
    // Slot data structure
    private static class SlotData {
        String name;
        HashMap<Integer, String> answers;
        
        SlotData(String name) {
            this.name = name;
            this.answers = new HashMap<>();
        }
    }

    // Camera components
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private ImageReader imageReader; // preview YUV
    private ImageReader jpegReader;  // still JPEG reader
    private Size jpegSize;
    private final AtomicBoolean waitingForJpeg = new AtomicBoolean(false);

    // State
    private boolean scanSessionActive = false;

    // Data
    private SharedPreferences answerKeyPreferences, historyPreferences, appPreferences;
    private final HashMap<Integer, String> currentAnswerKey = new HashMap<>();
    private volatile HashMap<Integer, String> lastDetectedAnswers = new HashMap<>();
    // Parsed answers editors + last confirmed score
    private final HashMap<Integer, View> parsedEditors = new HashMap<>();
    private Integer lastConfirmedScore = null;
    private Integer lastConfirmedTotal = null;

    // Document picker launchers
    private ActivityResultLauncher<String[]> importSlotLauncher;
    private ActivityResultLauncher<String> exportSlotLauncher;
    
    // Multi-image import launcher (Feature #2)
    private ActivityResultLauncher<String> importPhotosLauncher;
    
    // Crop launcher (Feature #2.1)
    private ActivityResultLauncher<android.content.Intent> cropLauncher;
    private android.net.Uri lastCapturedImageUri;

    // General settings
    private static final int CAMERA_WIDTH = 1280;
    private static final int CAMERA_HEIGHT = 720;

    // CSV Export
    private ActivityResultLauncher<String> createCsvLauncher;

    // HTTP client for Google Vision calls
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
    // Slot management methods
    private void initializeSlots() {
        answerKeyPreferences = getSharedPreferences("AnswerKeyPrefs", MODE_PRIVATE);
        appPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        
        // Load existing slots or create default
        loadSlots();
        
        // Migration: if old answer key exists, migrate to default slot
        migrateOldAnswerKey();
    }

    private void loadSlots() {
        try {
            String slotsJson = answerKeyPreferences.getString(PREFS_SLOTS, "");
            currentSlotId = answerKeyPreferences.getString(PREF_CURRENT_SLOT, "default");
            
            if (!slotsJson.isEmpty()) {
                JSONObject slotsObj = new JSONObject(slotsJson);
                
                // Load current slot
                if (slotsObj.has("current_slot")) {
                    currentSlotId = slotsObj.getString("current_slot");
                }
                
                // Load slots
                if (slotsObj.has("slots")) {
                    JSONObject slotsData = slotsObj.getJSONObject("slots");
                    Iterator<String> keys = slotsData.keys();
                    while (keys.hasNext()) {
                        String slotId = keys.next();
                        JSONObject slotObj = slotsData.getJSONObject(slotId);
                        SlotData slot = new SlotData(slotObj.getString("name"));
                        
                        if (slotObj.has("answers")) {
                            JSONObject answers = slotObj.getJSONObject("answers");
                            Iterator<String> qKeys = answers.keys();
                            while (qKeys.hasNext()) {
                                String qStr = qKeys.next();
                                int q = Integer.parseInt(qStr);
                                slot.answers.put(q, answers.getString(qStr));
                            }
                        }
                        slots.put(slotId, slot);
                    }
                }
            }
            
            // Ensure default slot exists
            if (slots.isEmpty()) {
                slots.put("default", new SlotData(getString(R.string.slot_default_name)));
                currentSlotId = "default";
            }
            
            loadCurrentSlot();
            
        } catch (JSONException e) {
            Log.e(TAG, "Error loading slots", e);
            // Create default slot on error
            slots.clear();
            slots.put("default", new SlotData(getString(R.string.slot_default_name)));
            currentSlotId = "default";
            loadCurrentSlot();
        }
    }

    private void migrateOldAnswerKey() {
        // Check if old answer key exists (pre-v1.1)
        if (answerKeyPreferences.contains("Q1") || answerKeyPreferences.contains("Q2")) {
            SlotData defaultSlot = slots.get("default");
            if (defaultSlot != null && defaultSlot.answers.isEmpty()) {
                // Migrate old answers to default slot
                for (int i = 1; i <= 200; i++) {
                    String answer = answerKeyPreferences.getString("Q" + i, null);
                    if (answer != null && !answer.trim().isEmpty()) {
                        defaultSlot.answers.put(i, answer);
                    }
                }
                saveSlots();
                
                // Clear old format
                SharedPreferences.Editor editor = answerKeyPreferences.edit();
                for (int i = 1; i <= 200; i++) {
                    editor.remove("Q" + i);
                }
                editor.apply();
                
                Log.d(TAG, "Migrated " + defaultSlot.answers.size() + " answers to default slot");
            }
        }
    }

    private void saveSlots() {
        try {
            JSONObject rootObj = new JSONObject();
            rootObj.put("current_slot", currentSlotId);
            
            JSONObject slotsObj = new JSONObject();
            for (String slotId : slots.keySet()) {
                SlotData slot = slots.get(slotId);
                if (slot != null) {
                    JSONObject slotObj = new JSONObject();
                    slotObj.put("name", slot.name);
                    
                    JSONObject answersObj = new JSONObject();
                    for (Integer q : slot.answers.keySet()) {
                        answersObj.put(q.toString(), slot.answers.get(q));
                    }
                    slotObj.put("answers", answersObj);
                    
                    slotsObj.put(slotId, slotObj);
                }
            }
            rootObj.put("slots", slotsObj);
            
            answerKeyPreferences.edit()
                    .putString(PREFS_SLOTS, rootObj.toString())
                    .putString(PREF_CURRENT_SLOT, currentSlotId)
                    .apply();
                    
        } catch (JSONException e) {
            Log.e(TAG, "Error saving slots", e);
        }
    }

    private void loadCurrentSlot() {
        currentAnswerKey.clear();
        SlotData currentSlot = slots.get(currentSlotId);
        if (currentSlot != null) {
            currentAnswerKey.putAll(currentSlot.answers);
        }
        updateAnswerKeyDisplay();
    }

    private void switchToSlot(String slotId) {
        // Save current answers to current slot
        SlotData currentSlot = slots.get(currentSlotId);
        if (currentSlot != null) {
            currentSlot.answers.clear();
            currentSlot.answers.putAll(currentAnswerKey);
        }
        
        // Switch to new slot
        currentSlotId = slotId;
        loadCurrentSlot();
        saveSlots();
        updateSlotSelector();
    }

    private void initializeDocumentLaunchers() {
        importSlotLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                result -> {
                    if (result != null) {
                        importSlotFromUri(result);
                    }
                });
        
        exportSlotLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/json"),
                result -> {
                    if (result != null) {
                        exportSlotToUri(result);
                    }
                });
    }

    private void setupSlotListeners() {
        if (slotSelector != null) {
            slotSelector.setOnItemClickListener((parent, view, position, id) -> {
                String selectedName = (String) parent.getItemAtPosition(position);
                // Find slot ID by name
                for (String slotId : slots.keySet()) {
                    SlotData slot = slots.get(slotId);
                    if (slot != null && slot.name.equals(selectedName)) {
                        switchToSlot(slotId);
                        break;
                    }
                }
            });
        }

        if (btnSlotNew != null) {
            btnSlotNew.setOnClickListener(v -> showNewSlotDialog());
        }
        
        if (btnSlotRename != null) {
            btnSlotRename.setOnClickListener(v -> showRenameSlotDialog());
        }
        
        if (btnSlotDelete != null) {
            btnSlotDelete.setOnClickListener(v -> showDeleteSlotDialog());
        }
        
        if (btnSlotImport != null) {
            btnSlotImport.setOnClickListener(v -> importSlotLauncher.launch(new String[]{"application/json", "*/*"}));
        }
        
        if (btnSlotExport != null) {
            btnSlotExport.setOnClickListener(v -> {
                SlotData currentSlot = slots.get(currentSlotId);
                String filename = "slot_" + (currentSlot != null ? currentSlot.name : "export") + ".json";
                exportSlotLauncher.launch(filename);
            });
        }
    }

    private void loadPersistedInputs() {
        if (appPreferences != null) {
            String lastSection = appPreferences.getString(PREF_LAST_SECTION, "");
            String lastExam = appPreferences.getString(PREF_LAST_EXAM, "");
            
            if (sectionNameInput != null && !lastSection.isEmpty()) {
                sectionNameInput.setText(lastSection);
            }
            if (examNameInput != null && !lastExam.isEmpty()) {
                examNameInput.setText(lastExam);
            }
        }
    }

    private void saveInputValues() {
        if (appPreferences != null && sectionNameInput != null && examNameInput != null) {
            appPreferences.edit()
                    .putString(PREF_LAST_SECTION, sectionNameInput.getText().toString().trim())
                    .putString(PREF_LAST_EXAM, examNameInput.getText().toString().trim())
                    .apply();
        }
    }

    // Replace the entire updateAnswerKeyDisplay() with this corrected version:
    // Replace the whole method with this:
    // Replace the whole method with this:
    private void updateAnswerKeyDisplay() {
        if (currentKeyTextView == null) return;

        StringBuilder sb = new StringBuilder(getString(R.string.current_key_prefix));
        if (currentAnswerKey.isEmpty()) {
            sb.append(" (Empty)");
        } else {
            sb.append("\n");
            List<Integer> sortedKeys = new ArrayList<>(currentAnswerKey.keySet());
            Collections.sort(sortedKeys);
            for (Integer q : sortedKeys) {
                sb.append(q).append(": ").append(currentAnswerKey.get(q)).append("  ");
                if (sb.length() > 200) {
                    sb.append("...");
                    break;
                }
            }
        }
        currentKeyTextView.setText(sb.toString());
    }


    private void showNewSlotDialog() {
        EditText input = new EditText(this);
        input.setHint(getString(R.string.slot_name_hint));
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.slot_name_dialog_title))
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        createNewSlot(name);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRenameSlotDialog() {
        SlotData currentSlot = slots.get(currentSlotId);
        if (currentSlot == null) return;

        EditText input = new EditText(this);
        input.setText(currentSlot.name);
        input.setHint(getString(R.string.slot_name_hint));
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.slot_name_dialog_title))
                .setView(input)
                .setPositiveButton("Rename", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        currentSlot.name = name;
                        saveSlots();
                        updateSlotSelector();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteSlotDialog() {
        if (slots.size() <= 1) {
            Toast.makeText(this, getString(R.string.slot_delete_last_error), Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Slot")
                .setMessage(getString(R.string.slot_delete_confirm))
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteCurrentSlot();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void createNewSlot(String name) {
        String newSlotId = "slot_" + System.currentTimeMillis();
        SlotData newSlot = new SlotData(name);
        slots.put(newSlotId, newSlot);
        switchToSlot(newSlotId);
        saveSlots();
        updateSlotSelector();
    }

    private void deleteCurrentSlot() {
        if (slots.size() <= 1) return;

        slots.remove(currentSlotId);
        
        // Switch to first available slot
        String newSlotId = slots.keySet().iterator().next();
        currentSlotId = newSlotId;
        loadCurrentSlot();
        saveSlots();
        updateSlotSelector();
    }

    private void importSlotFromUri(android.net.Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            if (inputStream == null) return;

            StringBuilder json = new StringBuilder();
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                json.append(new String(buffer, 0, read));
            }

            importSlotData(json.toString());
            Toast.makeText(this, getString(R.string.slot_import_success), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Error importing slot", e);
            String message = String.format(getString(R.string.slot_import_error), e.getMessage());
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void importSlotData(String jsonString) throws JSONException {
        JSONObject json = new JSONObject(jsonString);
        
        // Try to parse as our slot format
        if (json.has("slots") && json.has("current_slot")) {
            // Full slot export format
            JSONObject slotsObj = json.getJSONObject("slots");
            String importSlotId = json.getString("current_slot");
            
            if (slotsObj.has(importSlotId)) {
                JSONObject slotObj = slotsObj.getJSONObject(importSlotId);
                String name = slotObj.optString("name", "Imported Slot");
                
                SlotData newSlot = new SlotData(name);
                if (slotObj.has("answers")) {
                    JSONObject answers = slotObj.getJSONObject("answers");
                    Iterator<String> keys = answers.keys();
                    while (keys.hasNext()) {
                        String qStr = keys.next();
                        int q = Integer.parseInt(qStr);
                        newSlot.answers.put(q, answers.getString(qStr));
                    }
                }
                
                String newSlotId = "imported_" + System.currentTimeMillis();
                slots.put(newSlotId, newSlot);
                switchToSlot(newSlotId);
            }
        } else {
            // Try to parse as simple {"1":"A", "2":"B"} format
            String name = "Imported " + System.currentTimeMillis();
            SlotData newSlot = new SlotData(name);
            
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String qStr = keys.next();
                try {
                    int q = Integer.parseInt(qStr);
                    newSlot.answers.put(q, json.getString(qStr));
                } catch (NumberFormatException ignored) {
                    // Skip non-numeric keys
                }
            }
            
            String newSlotId = "imported_" + System.currentTimeMillis();
            slots.put(newSlotId, newSlot);
            switchToSlot(newSlotId);
        }
        
        saveSlots();
        updateSlotSelector();
    }

    private void exportSlotToUri(android.net.Uri uri) {
        try {
            SlotData currentSlot = slots.get(currentSlotId);
            if (currentSlot == null) return;

            // Create export JSON in our format
            JSONObject rootObj = new JSONObject();
            rootObj.put("current_slot", currentSlotId);
            
            JSONObject slotsObj = new JSONObject();
            JSONObject slotObj = new JSONObject();
            slotObj.put("name", currentSlot.name);
            
            JSONObject answersObj = new JSONObject();
            for (Integer q : currentSlot.answers.keySet()) {
                answersObj.put(q.toString(), currentSlot.answers.get(q));
            }
            slotObj.put("answers", answersObj);
            
            slotsObj.put(currentSlotId, slotObj);
            rootObj.put("slots", slotsObj);

            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                if (outputStream != null) {
                    outputStream.write(rootObj.toString(2).getBytes());
                    outputStream.flush();
                    Toast.makeText(this, getString(R.string.slot_export_success), Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error exporting slot", e);
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void showTutorialDialog() {
        android.widget.ScrollView scroll = new android.widget.ScrollView(this);
        TextView tv = new TextView(this);
        tv.setPadding(dp(16), dp(16), dp(16), dp(16));
        tv.setTextColor(Color.BLACK);
        tv.setText(getString(R.string.tutorial_text));
        scroll.addView(tv);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.tutorial_title))
                .setView(scroll)
                .setPositiveButton("OK", null)
                .show();
    }
    private void updateSlotSelector() {
        if (slotSelector != null) {
            List<String> slotNames = new ArrayList<>();
            for (String slotId : slots.keySet()) {
                SlotData slot = slots.get(slotId);
                if (slot != null) {
                    slotNames.add(slot.name);
                }
            }
            
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                    android.R.layout.simple_list_item_1, slotNames);
            slotSelector.setAdapter(adapter);
            
            SlotData currentSlot = slots.get(currentSlotId);
            if (currentSlot != null) {
                slotSelector.setText(currentSlot.name, false);
            }
        }
    }

    private void updateAnswerInputUi() {
        if (answerDropdown == null) return;
        
        // Universal input - no adapter, accepts both letters and text
        answerDropdown.setAdapter(null);
        answerDropdown.setText("");
        answerDropdown.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        answerDropdown.setHint(getString(R.string.hint_answer));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize prefs EARLY
        historyPreferences = getSharedPreferences("TestHistoryPrefs", MODE_PRIVATE);
        
        // Initialize slot system
        initializeSlots();

        // Initialize document launchers
        initializeDocumentLaunchers();

        // Bind UI
        topAppBar = findViewById(R.id.topAppBar);
        mainLayout = findViewById(R.id.main_layout);
        studentNameInput = findViewById(R.id.editText_student_name);
        sectionNameInput = findViewById(R.id.editText_section_name);
        examNameInput = findViewById(R.id.editText_exam_name);
        startScanButton = findViewById(R.id.button_scan);
        setupButton = findViewById(R.id.button_setup_answers);
        viewHistoryButton = findViewById(R.id.button_view_history);

        // Answer key
        answerKeyLayout = findViewById(R.id.answer_key_layout);
        questionNumberInput = findViewById(R.id.editText_question_number);
        answerDropdown = findViewById(R.id.auto_answer);
        saveAnswerButton = findViewById(R.id.button_save_answer);
        removeQuestionInput = findViewById(R.id.editText_remove_question);
        removeAnswerButton = findViewById(R.id.button_remove_answer);
        clearButton = findViewById(R.id.button_clear_answers);
        backButton = findViewById(R.id.button_back);
        currentKeyTextView = findViewById(R.id.textView_current_key);
        
        // Slot management UI
        slotSelector = findViewById(R.id.slot_selector);
        btnSlotNew = findViewById(R.id.btn_slot_new);
        btnSlotRename = findViewById(R.id.btn_slot_rename);
        btnSlotDelete = findViewById(R.id.btn_slot_delete);
        btnSlotImport = findViewById(R.id.btn_slot_import);
        btnSlotExport = findViewById(R.id.btn_slot_export);
        
        // Initialize slot UI
        updateSlotSelector();
        setupSlotListeners();
        
        // Ensure input reflects universal mode on start
        updateAnswerInputUi();
        
        // History
        testHistoryLayout = findViewById(R.id.test_history_layout);
        testHistoryList = findViewById(R.id.test_history_list);
        historyBackButton = findViewById(R.id.button_history_back);
        historyBackButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            toggleView("main");
        });

        // Scan session  
        scanSessionLayout = findViewById(R.id.scan_session_layout);
        cameraPreviewTextureView = findViewById(R.id.textureView_camera_preview);
        sessionScoreTextView = findViewById(R.id.textView_session_score);
        resultsCard = findViewById(R.id.results_card);
        tryAgainButton = findViewById(R.id.button_try_again);
        captureResultButton = findViewById(R.id.button_capture_result);
        importPhotosButton = findViewById(R.id.button_import_photos);
        cancelScanButton = findViewById(R.id.button_cancel_scan);
        shutterView = findViewById(R.id.shutterView);

        // Bind parsed answers verification views
        parsedLabel = findViewById(R.id.textView_parsed_label);
        parsedAnswersContainer = findViewById(R.id.parsed_answers_container);
        confirmParsedButton = findViewById(R.id.button_confirm_parsed);
        Button saveResultInlineButton = findViewById(R.id.button_save_result);

        // Load answer key and display
        updateAnswerKeyDisplay();

        // Load persisted section and exam values
        loadPersistedInputs();

        // CSV Export launcher
        createCsvLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("text/csv"),
                this::onCsvDocumentCreated
        );
        
        // Multi-image import launcher (Feature #2)
        importPhotosLauncher = registerForActivityResult(
                new ActivityResultContracts.GetMultipleContents(),
                this::onPhotosImported
        );
        
        // Crop launcher (Feature #2.1)
        cropLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::onCropResult
        );

        // Toolbar menu
        if (topAppBar != null) {
            topAppBar.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_setup) {
                    toggleView("setup");
                    return true;
                } else if (id == R.id.menu_history) {
                    toggleView("history");
                    displayTestHistory();
                    return true;
                } else if (id == R.id.menu_export_csv) {
                    exportHistoryCsv();
                    return true;
                } else if (id == R.id.menu_tutorial) {
                    showTutorialDialog();
                    return true;
                } else if (id == R.id.menu_credits) {
                    showCreditsDialog();
                    return true;
                }
                return false;
            });
        }


        // Buttons (main)
        startScanButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            requestAndStartScanSession();
        });
        setupButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            toggleView("setup");
        });
        viewHistoryButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            toggleView("history");
            displayTestHistory();
        });

        // Answer key buttons
        saveAnswerButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            saveAnswer();
        });
        removeAnswerButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            removeAnswer();
        });
        clearButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            clearAnswerKey();
        });
        backButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            toggleView("main");
        });

        // Scan session buttons

        tryAgainButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            if (resultsCard != null) resultsCard.setVisibility(View.GONE);
            if (captureResultButton != null) {
                captureResultButton.setVisibility(View.VISIBLE);
                captureResultButton.setText("Scan");
            }
            // Reset placeholder and clear editors display
            if (sessionScoreTextView != null) {
                sessionScoreTextView.setText(getString(R.string.live_score_placeholder));
            }
            if (parsedAnswersContainer != null) parsedAnswersContainer.removeAllViews();
            parsedEditors.clear();
            lastDetectedAnswers = new HashMap<>();
        });

        // Bottom button always captures a new photo
        captureResultButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            triggerStillCapture();
        });
        
        // Import photos button (Feature #2)
        if (importPhotosButton != null) {
            importPhotosButton.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                importPhotosLauncher.launch("image/*");
            });
        }

        cancelScanButton.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            stopScanSession();
            toggleView("main");
        });

        // Disable scan if Vision key missing
        if (BuildConfig.GCLOUD_VISION_API_KEY == null || BuildConfig.GCLOUD_VISION_API_KEY.trim().isEmpty()) {
            startScanButton.setEnabled(false);
            Toast.makeText(this, "Vision API key missing (add GCLOUD_VISION_API_KEY to local.properties).", Toast.LENGTH_LONG).show();
        }

        // Confirm parsed edits -> compute score
        if (confirmParsedButton != null) {
            confirmParsedButton.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                gatherEditedAnswers();
                computeAndDisplayScoreAfterConfirm();
            });
        }
        if (saveResultInlineButton != null) {
            saveResultInlineButton.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                captureAndRecord();
            });
        }

        // Default
        toggleView("main");
    }

    // ---------------------------
    // Camera + capture pipeline
    // ---------------------------

    private void requestAndStartScanSession() {
        if (sectionNameInput.getText().toString().trim().isEmpty()
                || examNameInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter name, subgroup 1 and 2 first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startScanSession();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void startScanSession() {
        scanSessionActive = true;
        toggleView("scan");
        sessionScoreTextView.setText(getString(R.string.live_score_placeholder));
        if (resultsCard != null) resultsCard.setVisibility(View.GONE);
        if (captureResultButton != null) {
            captureResultButton.setVisibility(View.VISIBLE);
            captureResultButton.setText("Scan");
        }
        startBackgroundThread();
        // ... rest unchanged


        if (imageReader != null) imageReader.close();
        imageReader = ImageReader.newInstance(CAMERA_WIDTH, CAMERA_HEIGHT, ImageFormat.YUV_420_888, 2);
        imageReader.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler);

        cameraPreviewTextureView.setSurfaceTextureListener(textureListener);
        if (cameraPreviewTextureView.isAvailable())
            openCameraPreview(cameraPreviewTextureView.getWidth(), cameraPreviewTextureView.getHeight());
    }

    private void stopScanSession() {
        scanSessionActive = false;
        closeCamera();
        stopBackgroundThread();
        cameraPreviewTextureView.setSurfaceTextureListener(null);
    }

    private final TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            if (!scanSessionActive) return;
            openCameraPreview(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private void openCameraPreview(int viewWidth, int viewHeight) {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = manager.getCameraIdList()[0];

            // Determine largest JPEG size for stills
            try {
                CameraCharacteristics chars = manager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map != null) {
                    Size[] jpegSizes = map.getOutputSizes(ImageFormat.JPEG);
                    if (jpegSizes != null && jpegSizes.length > 0) {
                        Size largest = jpegSizes[0];
                        for (Size s : jpegSizes) {
                            if ((long) s.getWidth() * s.getHeight() > (long) largest.getWidth() * largest.getHeight())
                                largest = s;
                        }
                        jpegSize = largest;
                    }
                }
            } catch (Throwable ignored) {
                jpegSize = new Size(1920, 1080);
            }
            if (jpegSize == null) jpegSize = new Size(1920, 1080);

            SurfaceTexture texture = cameraPreviewTextureView.getSurfaceTexture();
            if (texture == null) return;
            texture.setDefaultBufferSize(CAMERA_WIDTH, CAMERA_HEIGHT);
            Surface previewSurface = new Surface(texture);
            Surface readerSurface = imageReader.getSurface();

            // prepare jpegReader
            if (jpegReader != null) {
                jpegReader.close();
                jpegReader = null;
            }
            jpegReader = ImageReader.newInstance(jpegSize.getWidth(), jpegSize.getHeight(), ImageFormat.JPEG, 2);
            jpegReader.setOnImageAvailableListener(onJpegAvailableListener, backgroundHandler);
            Surface jpegSurface = jpegReader.getSurface();

            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    try {
                        captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        captureRequestBuilder.addTarget(previewSurface);
                        captureRequestBuilder.addTarget(readerSurface);

                        try {
                            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        } catch (Throwable ignored) {
                        }
                        try {
                            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, new Range<>(30, 30));
                        } catch (Throwable ignored) {
                        }

                        cameraDevice.createCaptureSession(
                                Arrays.asList(previewSurface, readerSurface, jpegSurface),
                                new CameraCaptureSession.StateCallback() {
                                    @Override
                                    public void onConfigured(@NonNull CameraCaptureSession session) {
                                        if (cameraDevice == null) return;
                                        cameraCaptureSession = session;
                                        try {
                                            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
                                        } catch (CameraAccessException e) {
                                            Log.e(TAG, "setRepeatingRequest", e);
                                        }
                                    }

                                    @Override
                                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to create camera session", Toast.LENGTH_SHORT).show());
                                    }
                                }, backgroundHandler);
                    } catch (CameraAccessException e) {
                        Log.e(TAG, "openCameraPreview create session", e);
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                    cameraDevice = null;
                }
            }, backgroundHandler);

        } catch (CameraAccessException e) {
            Log.e(TAG, "openCameraPreview", e);
        } catch (SecurityException se) {
            Log.e(TAG, "Camera permission", se);
        }
    }

    private final ImageReader.OnImageAvailableListener onImageAvailableListener = reader -> {
        // preview only: drop frame
        Image img = reader.acquireLatestImage();
        if (img != null) img.close();
    };

    // Trigger still capture (user taps Try/Take Photo)
    private void triggerStillCapture() {
        if (cameraDevice == null || cameraCaptureSession == null || jpegReader == null) {
            runOnUiThread(() -> Toast.makeText(this, "Camera not ready", Toast.LENGTH_SHORT).show());
            return;
        }
        if (!waitingForJpeg.compareAndSet(false, true)) return;

        // runOnUiThread(() -> sessionOcrTextView.setText("Capturing… hold steady"));
        shutterFlash();

        try {
            final CaptureRequest.Builder stillBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            stillBuilder.addTarget(jpegReader.getSurface());
            try {
                stillBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            } catch (Throwable ignored) {
            }
            try {
                stillBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            } catch (Throwable ignored) {
            }
            try {
                stillBuilder.set(CaptureRequest.JPEG_ORIENTATION, 90);
            } catch (Throwable ignored) {
            }

            cameraCaptureSession.stopRepeating();
            cameraCaptureSession.capture(stillBuilder.build(), new CameraCaptureSession.CaptureCallback() {
            }, backgroundHandler);
            // Resume preview
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
        } catch (Throwable e) {
            waitingForJpeg.set(false);
            Log.e(TAG, "triggerStillCapture failed", e);
            runOnUiThread(() -> Toast.makeText(this, "Capture failed", Toast.LENGTH_SHORT).show());
        }
    }

    private final ImageReader.OnImageAvailableListener onJpegAvailableListener = reader -> {
        Image img = reader.acquireLatestImage();
        if (img == null) return;
        try {
            Image.Plane plane = img.getPlanes()[0];
            ByteBuffer buffer = plane.getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (bmp == null) {
                runOnUiThread(() -> Toast.makeText(this, "Invalid photo data", Toast.LENGTH_SHORT).show());
                return;
            }

            // Feature #2.1: Save to file and launch crop
            try {
                // Save bitmap to temporary file
                String fileName = "capture_" + System.currentTimeMillis() + ".jpg";
                java.io.File tempFile = new java.io.File(getCacheDir(), fileName);
                java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile);
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
                
                lastCapturedImageUri = android.net.Uri.fromFile(tempFile);
                
                // Launch crop activity on UI thread
                runOnUiThread(() -> {
                    startCropActivity(lastCapturedImageUri);
                });
                
                bmp.recycle();
                
            } catch (Exception e) {
                Log.e(TAG, "Error saving capture for crop", e);
                // Fallback: process without crop
                byte[] processedJpeg = resizeAndCompress(bmp, 1600);
                bmp.recycle();
                processImageWithOcr(processedJpeg);
            }
            
        } catch (Throwable t) {
            Log.e(TAG, "onJpegAvailableListener error", t);
            runOnUiThread(() -> Toast.makeText(this, "Analyze failed", Toast.LENGTH_SHORT).show());
        } finally {
            try { img.close(); } catch (Throwable ignored) {}
            waitingForJpeg.set(false);
        }
    };
    
    /**
     * Process image with OCR (extracted from previous inline code).
     */
    private void processImageWithOcr(byte[] processedJpeg) {
        backgroundHandler.post(() -> {
            try {
                String recognizedText = callVisionApiAndRecognize(processedJpeg);
                if (recognizedText == null) recognizedText = "";
                HashMap<Integer, String> parsed = parseAnswersFromText(recognizedText);
                lastDetectedAnswers = (parsed != null) ? parsed : new HashMap<>();

                runOnUiThread(() -> {
                    populateParsedAnswersEditable();
                    sessionScoreTextView.setText(getString(R.string.live_score_placeholder));
                });
            } catch (Exception e) {
                Log.e(TAG, "OCR processing error", e);
                runOnUiThread(() -> Toast.makeText(this, "OCR failed", Toast.LENGTH_SHORT).show());
            }
        });
    }

    // Pure-Java resize and compress to JPEG, max dimension
    private byte[] resizeAndCompress(Bitmap src, int maxDim) {
        int w = src.getWidth(), h = src.getHeight();
        float scale = Math.min(1f, maxDim / (float) Math.max(w, h));
        int nw = Math.round(w * scale), nh = Math.round(h * scale);
        Bitmap scaled = Bitmap.createScaledBitmap(src, nw, nh, true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 92, out);
        scaled.recycle();
        return out.toByteArray();
    }

    // Vision API call (OkHttp, synchronous)
    private String callVisionApiAndRecognize(byte[] jpegBytes) {
        String apiKey = BuildConfig.GCLOUD_VISION_API_KEY;
        if (apiKey == null || apiKey.trim().isEmpty()) return null;
        String base64Image = Base64.encodeToString(jpegBytes, Base64.NO_WRAP);
        String url = "https://vision.googleapis.com/v1/images:annotate?key=" + apiKey;
        JSONObject req = new JSONObject();
        try {
            JSONArray requests = new JSONArray();
            JSONObject image = new JSONObject();
            image.put("content", base64Image);
            JSONObject feature = new JSONObject();
            feature.put("type", "DOCUMENT_TEXT_DETECTION");
            feature.put("maxResults", 1);
            JSONObject request = new JSONObject();
            request.put("image", image);
            request.put("features", new JSONArray().put(feature));
            requests.put(request);
            req.put("requests", requests);
        } catch (JSONException e) {
            Log.e(TAG, "Vision req JSON", e);
            return null;
        }
        RequestBody body = RequestBody.create(req.toString(), MediaType.parse("application/json"));
        Request httpReq = new Request.Builder().url(url).post(body).build();
        try (Response resp = httpClient.newCall(httpReq).execute()) {
            if (!resp.isSuccessful()) {
                Log.e(TAG, "Vision API error: " + resp.code());
                return null;
            }
            String respStr = resp.body().string();
            JSONObject respJson = new JSONObject(respStr);
            JSONArray responses = respJson.optJSONArray("responses");
            if (responses == null || responses.length() == 0) return null;
            JSONObject first = responses.getJSONObject(0);
            if (first.has("fullTextAnnotation")) {
                JSONObject fullText = first.getJSONObject("fullTextAnnotation");
                return fullText.optString("text", "");
            } else if (first.has("textAnnotations")) {
                JSONArray textAnn = first.getJSONArray("textAnnotations");
                if (textAnn.length() > 0) {
                    JSONObject top = textAnn.getJSONObject(0);
                    return top.optString("description", "");
                }
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Vision API call", e);
            runOnUiThread(() -> Toast.makeText(this, "Vision API error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            return null;
        }
    }

    // Enhanced parser using the new Parser class with roman numeral support
    private HashMap<Integer, String> parseAnswersFromText(String text) {
        if (text == null) return new HashMap<>();
        
        // Use the enhanced parser from Parser.java
        LinkedHashMap<Integer, String> parsed = Parser.parseOcrTextToAnswers(text);
        
        // Filter to only answer key questions (Feature #1: Parsing Must Follow Answer Key Only)
        LinkedHashMap<Integer, String> filtered = Parser.filterToAnswerKey(parsed, currentAnswerKey);
        
        // Convert LinkedHashMap to HashMap for compatibility
        HashMap<Integer, String> result = new HashMap<>();
        result.putAll(filtered);
        
        return result;
    }

    private int safeParseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return -1; }
    }

    private void closeCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
        if (jpegReader != null) {
            jpegReader.close();
            jpegReader = null;
        }
        cameraCaptureSession = null;
    }

    private void saveAnswer() {
        String qStr = questionNumberInput != null ? questionNumberInput.getText().toString().trim() : "";
        String ans = answerDropdown != null ? answerDropdown.getText().toString().trim() : "";
        if (qStr.isEmpty() || ans.isEmpty()) {
            Toast.makeText(this, "Enter question number and answer.", Toast.LENGTH_SHORT).show();
            return;
        }
        int q;
        try { q = Integer.parseInt(qStr); } catch (Exception e) {
            Toast.makeText(this, "Invalid question number.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Universal input validation
        String value = validateUniversalAnswer(ans);
        if (value == null) {
            Toast.makeText(this, "Answer must be A-Z letter or text up to 40 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentAnswerKey.put(q, value);
        
        // Save to current slot
        SlotData currentSlot = slots.get(currentSlotId);
        if (currentSlot != null) {
            currentSlot.answers.put(q, value);
        }
        
        saveSlots();
        updateAnswerKeyDisplay();
        
        // Clear inputs for next entry
        questionNumberInput.setText("");
        answerDropdown.setText("");
        
        // Save input values for persistence
        saveInputValues();
        
        Toast.makeText(this, "Saved.", Toast.LENGTH_SHORT).show();
    }

    private String validateUniversalAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = answer.trim();
        
        // If exactly one letter A-Z, accept and uppercase it
        if (trimmed.length() == 1) {
            char c = trimmed.toUpperCase().charAt(0);
            if (c >= 'A' && c <= 'Z') {
                return String.valueOf(c);
            }
        }
        
        // Otherwise accept as text up to 40 chars, trim punctuation at end
        String cleaned = trimmed.replaceAll("[.,;!?]+$", "");
        if (cleaned.length() > 40) {
            cleaned = cleaned.substring(0, 40).trim();
        }
        
        return cleaned.isEmpty() ? null : cleaned;
    }

    private void removeAnswer() {
        if (removeQuestionInput == null) return;
        String qStr = removeQuestionInput.getText().toString().trim();
        if (qStr.isEmpty()) {
            Toast.makeText(this, "Enter question number to remove.", Toast.LENGTH_SHORT).show();
            return;
        }
        int q;
        try {
            q = Integer.parseInt(qStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid question number.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentAnswerKey.remove(q) != null) {
            // Remove from current slot
            SlotData currentSlot = slots.get(currentSlotId);
            if (currentSlot != null) {
                currentSlot.answers.remove(q);
            }
            
            saveSlots();
            updateAnswerKeyDisplay();
            Toast.makeText(this, "Removed question " + q, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Question " + q + " not in key.", Toast.LENGTH_SHORT).show();
        }
        removeQuestionInput.setText("");
    }

    private void clearAnswerKey() {
        if (currentAnswerKey.isEmpty()) {
            Toast.makeText(this, "Answer key already empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Clear All Answers")
                .setMessage("Remove ALL answers from the current slot?")
                .setPositiveButton("Clear", (d, w) -> {
                    currentAnswerKey.clear();
                    
                    // Clear current slot
                    SlotData currentSlot = slots.get(currentSlotId);
                    if (currentSlot != null) {
                        currentSlot.answers.clear();
                    }
                    
                    saveSlots();
                    updateAnswerKeyDisplay();
                    if (questionNumberInput != null) questionNumberInput.setText("");
                    if (answerDropdown != null) answerDropdown.setText("");
                    if (removeQuestionInput != null) removeQuestionInput.setText("");
                    Toast.makeText(this, "Answer key cleared.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------------------
    // Organized History (collapsible with rename/delete)
    // ---------------------------

    private void displayTestHistory() {
        testHistoryList.removeAllViews();
        JSONArray history = getHistoryArray();
        if (history.length() == 0) {
            TextView tv = makeBlackBoldText("No history yet.", 16);
            tv.setPadding(dp(12), dp(12), dp(12), dp(12));
            testHistoryList.addView(tv);
            return;
        }

        LinkedHashMap<String, LinkedHashMap<String, List<JSONObject>>> grouped = new LinkedHashMap<>();

        for (int i = 0; i < history.length(); i++) {
            JSONObject rec = history.optJSONObject(i);
            if (rec == null) continue;
            String exam = rec.optString("exam", "Untitled Quiz");
            String section = rec.optString("section", "Unsectioned");

            LinkedHashMap<String, List<JSONObject>> sections = grouped.get(exam);
            if (sections == null) {
                sections = new LinkedHashMap<>();
                grouped.put(exam, sections);
            }
            List<JSONObject> list = sections.get(section);
            if (list == null) {
                list = new ArrayList<>();
                sections.put(section, list);
            }
            list.add(rec);
        }

        for (java.util.Map.Entry<String, LinkedHashMap<String, List<JSONObject>>> examEntry : grouped.entrySet()) {
            String exam = examEntry.getKey();
            LinkedHashMap<String, List<JSONObject>> sections = examEntry.getValue();

            LinearLayout examContainer = new LinearLayout(this);
            examContainer.setOrientation(LinearLayout.VERTICAL);
            examContainer.setPadding(dp(8), dp(8), dp(8), dp(8));

            LinearLayout examHeaderRow = new LinearLayout(this);
            examHeaderRow.setOrientation(LinearLayout.HORIZONTAL);
            examHeaderRow.setPadding(dp(12), dp(12), dp(8), dp(8));
            examHeaderRow.setBackgroundColor(0xFFEFEFEF);

            TextView examTitle = makeBlackBoldText("Quiz: " + exam + " (" + countRecords(sections) + ") ▶", 18);
            LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            examTitle.setLayoutParams(titleLp);

            ImageButton btnExamRename = makeIconButton(android.R.drawable.ic_menu_edit, "Rename quiz");
            btnExamRename.setOnClickListener(v -> promptRenameExam(exam));

            ImageButton btnExamDelete = makeIconButton(android.R.drawable.ic_menu_delete, "Delete quiz");
            btnExamDelete.setOnClickListener(v -> confirmDeleteExam(exam));

            examHeaderRow.addView(examTitle);
            examHeaderRow.addView(btnExamRename);
            examHeaderRow.addView(btnExamDelete);

            LinearLayout sectionsContainer = new LinearLayout(this);
            sectionsContainer.setOrientation(LinearLayout.VERTICAL);
            sectionsContainer.setVisibility(View.GONE);

            examHeaderRow.setOnClickListener(v -> toggleCollapse(sectionsContainer, examTitle));

            examContainer.addView(examHeaderRow);

            for (java.util.Map.Entry<String, List<JSONObject>> sectionEntry : sections.entrySet()) {
                String section = sectionEntry.getKey();
                List<JSONObject> records = sectionEntry.getValue();

                LinearLayout sectionContainer = new LinearLayout(this);
                sectionContainer.setOrientation(LinearLayout.VERTICAL);
                sectionContainer.setPadding(dp(8), dp(4), dp(8), dp(8));

                LinearLayout sectionHeaderRow = new LinearLayout(this);
                sectionHeaderRow.setOrientation(LinearLayout.HORIZONTAL);
                sectionHeaderRow.setPadding(dp(16), dp(10), dp(12), dp(6));
                sectionHeaderRow.setBackgroundColor(0xFFF6F6F6);

                TextView sectionTitle = makeBlackBoldText("Section: " + section + " (" + records.size() + ") ▶", 16);
                sectionTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                ImageButton btnSectionRename = makeIconButton(android.R.drawable.ic_menu_edit, "Rename section");
                btnSectionRename.setOnClickListener(v -> promptRenameSection(exam, section));

                ImageButton btnSectionDelete = makeIconButton(android.R.drawable.ic_menu_delete, "Delete section");
                btnSectionDelete.setOnClickListener(v -> confirmDeleteSection(exam, section));

                sectionHeaderRow.addView(sectionTitle);
                sectionHeaderRow.addView(btnSectionRename);
                sectionHeaderRow.addView(btnSectionDelete);

                TextView mpsLine = calculateAndCreateMpsDisplay(records);

                LinearLayout sectionWrapper = new LinearLayout(this);
                sectionWrapper.setOrientation(LinearLayout.VERTICAL);
                sectionWrapper.addView(sectionHeaderRow);
                sectionWrapper.addView(mpsLine);

                LinearLayout recordsContainer = new LinearLayout(this);
                recordsContainer.setOrientation(LinearLayout.VERTICAL);
                recordsContainer.setVisibility(View.GONE);

                sectionWrapper.setOnClickListener(v -> toggleCollapse(recordsContainer, sectionTitle));

                // Records (newest first)
                for (int i = records.size() - 1; i >= 0; i--) {
                    JSONObject rec = records.get(i);
                    String ts = rec.optString("ts", "");
                    String student = rec.optString("student", "Unknown");
                    int score = rec.optInt("score", 0);
                    int total = rec.optInt("total", 0);
                    double pct = rec.optDouble("percent", 0.0);

                    LinearLayout recordRow = new LinearLayout(this);
                    recordRow.setOrientation(LinearLayout.HORIZONTAL);
                    recordRow.setPadding(dp(24), dp(6), dp(12), dp(6));

                    String line = String.format(Locale.US, "[%s] %s — %d/%d (%.1f%%)", ts, student, score, total, pct);
                    TextView recordTv = makeBlackBoldText(line, 14);
                    recordTv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                    ImageButton btnRecordDelete = makeIconButton(android.R.drawable.ic_menu_delete, "Delete record");
                    btnRecordDelete.setOnClickListener(v -> confirmDeleteRecord(ts));

                    recordRow.addView(recordTv);
                    recordRow.addView(btnRecordDelete);
                    recordsContainer.addView(recordRow);
                }

                // Add section UI once (NOT twice)
                sectionContainer.addView(sectionWrapper);
                sectionContainer.addView(recordsContainer);
                sectionsContainer.addView(sectionContainer);
            }

            examContainer.addView(sectionsContainer);

            View divider = new View(this);
            LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1));
            dlp.setMargins(0, dp(8), 0, dp(8));
            divider.setLayoutParams(dlp);
            divider.setBackgroundColor(0xFFCCCCCC);

            testHistoryList.addView(examContainer);
            testHistoryList.addView(divider);
        }
    }

    private TextView calculateAndCreateMpsDisplay(List<JSONObject> records) {
        if (records == null || records.isEmpty()) {
            TextView mpsView = new TextView(this);
            mpsView.setText("");
            return mpsView;
        }

        // Calculate MPS (Mean Percentage Score)
        double totalPercentage = 0.0;
        int count = 0;
        
        for (JSONObject record : records) {
            double percent = record.optDouble("percent", -1.0);
            if (percent >= 0.0) {  // Only count valid percentages
                totalPercentage += percent;
                count++;
            }
        }
        
        TextView mpsView = new TextView(this);
        mpsView.setPadding(dp(16), dp(4), dp(16), dp(8));
        mpsView.setTextSize(12);
        mpsView.setTextColor(0xFF666666); // Gray text
        
        if (count > 0) {
            double mps = totalPercentage / count;
            String mpsText = String.format(Locale.US, getString(R.string.mps_format), mps, count);
            mpsView.setText(mpsText);
        } else {
            mpsView.setText("MPS: No valid scores");
        }
        
        return mpsView;
    }

    private int countRecords(LinkedHashMap<String, List<JSONObject>> sections) {
        int c = 0;
        for (List<JSONObject> l : sections.values()) c += l.size();
        return c;
    }

    private TextView makeBlackBoldText(String text, int sp) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(sp);
        tv.setTextColor(Color.BLACK);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        return tv;
    }

    private ImageButton makeIconButton(int androidIconRes, String contentDesc) {
        ImageButton b = new ImageButton(this);
        b.setImageResource(androidIconRes);
        b.setContentDescription(contentDesc);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(32), dp(32));
        lp.setMargins(dp(6), 0, 0, 0);
        b.setLayoutParams(lp);

        b.setBackgroundColor(Color.TRANSPARENT);
        b.setPadding(dp(4), dp(4), dp(4), dp(4));

        // Tint icon to black for readability
        b.setImageTintList(ColorStateList.valueOf(Color.BLACK));
        return b;
    }

    private void toggleCollapse(View container, TextView headerTitle) {
        boolean expand = container.getVisibility() != View.VISIBLE;
        container.setVisibility(expand ? View.VISIBLE : View.GONE);
        String t = headerTitle.getText().toString();
        String base = t.replace(" ▼", "").replace(" ▶", "");
        headerTitle.setText(base + (expand ? " ▼" : " ▶"));
    }

    private JSONArray getHistoryArray() {
        String raw = historyPreferences.getString("history", "[]");
        try {
            return new JSONArray(raw);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private void saveHistoryArray(JSONArray arr) {
        historyPreferences.edit().putString("history", arr.toString()).apply();
    }

    private void promptRenameExam(String oldExam) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setText(oldExam);
        new AlertDialog.Builder(this)
                .setTitle("Rename Quiz")
                .setView(input)
                .setPositiveButton("Save", (d, w) -> {
                    String newName = input.getText().toString().trim();
                    if (newName.isEmpty() || newName.equals(oldExam)) return;
                    JSONArray history = getHistoryArray();
                    JSONArray out = new JSONArray();
                    for (int i = 0; i < history.length(); i++) {
                        JSONObject rec = history.optJSONObject(i);
                        if (rec == null) continue;
                        if (oldExam.equals(rec.optString("exam", ""))) {
                            try {
                                rec.put("exam", newName);
                            } catch (JSONException ignored) {
                            }
                        }
                        out.put(rec);
                    }
                    saveHistoryArray(out);
                    displayTestHistory();
                    Toast.makeText(this, "Quiz renamed.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void promptRenameSection(String exam, String oldSection) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setText(oldSection);
        new AlertDialog.Builder(this)
                .setTitle("Rename Section")
                .setView(input)
                .setPositiveButton("Save", (d, w) -> {
                    String newName = input.getText().toString().trim();
                    if (newName.isEmpty() || newName.equals(oldSection)) return;
                    JSONArray history = getHistoryArray();
                    JSONArray out = new JSONArray();
                    for (int i = 0; i < history.length(); i++) {
                        JSONObject rec = history.optJSONObject(i);
                        if (rec == null) continue;
                        if (exam.equals(rec.optString("exam", "")) &&
                                oldSection.equals(rec.optString("section", ""))) {
                            try {
                                rec.put("section", newName);
                            } catch (JSONException ignored) {
                            }
                        }
                        out.put(rec);
                    }
                    saveHistoryArray(out);
                    displayTestHistory();
                    Toast.makeText(this, "Section renamed.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDeleteExam(String exam) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Quiz")
                .setMessage("Delete all records under \"" + exam + "\"?")
                .setPositiveButton("Delete", (d, w) -> {
                    JSONArray history = getHistoryArray();
                    JSONArray out = new JSONArray();
                    for (int i = 0; i < history.length(); i++) {
                        JSONObject rec = history.optJSONObject(i);
                        if (rec == null) continue;
                        if (!exam.equals(rec.optString("exam", ""))) out.put(rec);
                    }
                    saveHistoryArray(out);
                    displayTestHistory();
                    Toast.makeText(this, "Quiz deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDeleteSection(String exam, String section) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Section")
                .setMessage("Delete all \"" + section + "\" records under \"" + exam + "\"?")
                .setPositiveButton("Delete", (d, w) -> {
                    JSONArray history = getHistoryArray();
                    JSONArray out = new JSONArray();
                    for (int i = 0; i < history.length(); i++) {
                        JSONObject rec = history.optJSONObject(i);
                        if (rec == null) continue;
                        if (!(exam.equals(rec.optString("exam", "")) &&
                                section.equals(rec.optString("section", "")))) {
                            out.put(rec);
                        }
                    }
                    saveHistoryArray(out);
                    displayTestHistory();
                    Toast.makeText(this, "Section deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDeleteRecord(String ts) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Delete this record?")
                .setPositiveButton("Delete", (d, w) -> {
                    JSONArray history = getHistoryArray();
                    JSONArray out = new JSONArray();
                    for (int i = 0; i < history.length(); i++) {
                        JSONObject rec = history.optJSONObject(i);
                        if (rec == null) continue;
                        if (!ts.equals(rec.optString("ts", ""))) {
                            out.put(rec);
                        }
                    }
                    saveHistoryArray(out);
                    displayTestHistory();
                    Toast.makeText(this, "Record deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------------------------
    // CSV export (unchanged)
    // ---------------------------

    private void exportHistoryCsv() {
        createCsvLauncher.launch("isa_history.csv");
    }

    private void onCsvDocumentCreated(android.net.Uri uri) {
        if (uri == null) {
            Toast.makeText(this, "Export canceled.", Toast.LENGTH_SHORT).show();
            return;
        }
        backgroundHandler.post(() -> {
            String raw = historyPreferences.getString("history", "[]");
            JSONArray history;
            try {
                history = new JSONArray(raw);
            } catch (Exception e) {
                history = new JSONArray();
            }

            // Put exam & section first so spreadsheets can sort easily; add answer_mode column
            StringBuilder sb = new StringBuilder();
            sb.append("exam,section,student,timestamp,score,total,percent,answer_mode,answers\n");
            String modeLabel = "Universal"; // v1.1 uses universal input

            for (int i = 0; i < history.length(); i++) {
                try {
                    JSONObject rec = history.getJSONObject(i);
                    String ts = rec.optString("ts", "").replace(",", " ");
                    String student = rec.optString("student", "").replace(",", " ");
                    String section = rec.optString("section", "").replace(",", " ");
                    String exam = rec.optString("exam", "").replace(",", " ");
                    int score = rec.optInt("score", 0);
                    int total = rec.optInt("total", 0);
                    double pct = rec.optDouble("percent", 0.0);
                    JSONObject answers = rec.optJSONObject("answers");
                    String answersStr = answers != null ? answers.toString().replace(",", ";") : "{}";

                    sb.append(exam).append(",")
                            .append(section).append(",")
                            .append(student).append(",")
                            .append(ts).append(",")
                            .append(score).append(",")
                            .append(total).append(",")
                            .append(String.format(Locale.US, "%.1f", pct)).append(",")
                            .append(modeLabel).append(",")
                            .append("\"").append(answersStr.replace("\"", "'")).append("\"")
                            .append("\n");
                } catch (Exception ignored) {
                }
            }

            try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                if (os != null) {
                    os.write(sb.toString().getBytes());
                    os.flush();
                }
                runOnUiThread(() -> Toast.makeText(this, "CSV exported.", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                Log.e(TAG, "CSV export", e);
                runOnUiThread(() -> Toast.makeText(this, "CSV export failed.", Toast.LENGTH_LONG).show());
            }
        });
    }

    // ---------------------------
    // Misc (unchanged)
    // ---------------------------

    private void showCreditsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Credits")
                .setMessage("ItemScoreAnalysis Program\nGoogle Vision OCR Powered\nVersion 1.1\n\nResearchers:\nEspaño, Elijah Ria D.\nLolos, Kneel Charles B.\nMahusay, Queen Rheyceljoy F.\nMedel, Myra J.\nReyes, John Jharen R.\nSahagun, Jayson G.\nTagle, Steve Aldrei D.\n\nProgrammed By: Jayson G. Sahagun")
                .setPositiveButton("OK", null)
                .show();
    }

    private void startBackgroundThread() {
        if (backgroundThread != null) return;
        backgroundThread = new HandlerThread("CameraBg");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (backgroundThread == null) return;
        try {
            backgroundThread.quitSafely();
            backgroundThread.join();
        } catch (InterruptedException e) {
            Log.w(TAG, "stopBackgroundThread", e);
        } finally {
            backgroundThread = null;
            backgroundHandler = null;
        }
    }

    private void shutterFlash() {
        if (shutterView == null) return;
        shutterView.bringToFront();
        shutterView.setAlpha(0f);
        shutterView.setVisibility(View.VISIBLE);
        shutterView.animate().alpha(0.9f).setDuration(60).withEndAction(() ->
                shutterView.animate().alpha(0f).setDuration(140).withEndAction(() ->
                        shutterView.setVisibility(View.GONE)).start()
        ).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startScanSession();
            else Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        if (scanSessionActive) stopScanSession();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScanSession();
    }

    private void toggleView(String viewName) {
        // Fade out all views first
        fadeOut(mainLayout);
        fadeOut(answerKeyLayout);
        fadeOut(testHistoryLayout);
        fadeOut(scanSessionLayout);
        
        // Then fade in the target view after a short delay
        new Handler().postDelayed(() -> {
            switch (viewName) {
                case "main":
                    fadeIn(mainLayout);
                    break;
                case "setup":
                    fadeIn(answerKeyLayout);
                    break;
                case "history":
                    fadeIn(testHistoryLayout);
                    break;
                case "scan":
                    fadeIn(scanSessionLayout);
                    break;
            }
        }, 150); // 150ms delay for smoother transition
    }

    private void fadeOut(View v) {
        if (v != null && v.getVisibility() == View.VISIBLE) {
            v.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> v.setVisibility(View.GONE))
                .start();
        } else if (v != null) {
            v.setVisibility(View.GONE);
        }
    }
    
    private void fadeIn(View v) {
        if (v != null) {
            v.setVisibility(View.VISIBLE);
            v.setAlpha(0f);
            v.animate()
                .alpha(1f)
                .setDuration(300)
                .start();
        }
    }

    private void setGone(View v) {
        if (v != null) v.setVisibility(View.GONE);
    }

    private void setVisible(View v) {
        if (v != null) v.setVisibility(View.VISIBLE);
    }

    private int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return Math.round(v * d);
    }

    // ---------------------------
    // Minimal stubs to satisfy existing calls (no behavior changes)
    // ---------------------------
    private void populateParsedAnswersEditable() {
        if (parsedAnswersContainer == null || parsedLabel == null || confirmParsedButton == null)
            return;

        parsedEditors.clear();
        parsedAnswersContainer.removeAllViews();

        if (lastDetectedAnswers == null || lastDetectedAnswers.isEmpty()) {
            parsedLabel.setText("No answers parsed. You can add answers in the key setup screen.");
            parsedLabel.setVisibility(View.VISIBLE);
            confirmParsedButton.setVisibility(View.GONE);
            if (resultsCard != null) resultsCard.setVisibility(View.VISIBLE);
            if (captureResultButton != null) captureResultButton.setVisibility(View.GONE);
            return;
        }

        parsedLabel.setText("Editable Parsed Answers:");
        parsedLabel.setVisibility(View.VISIBLE);
        confirmParsedButton.setVisibility(View.VISIBLE);

        if (resultsCard != null) resultsCard.setVisibility(View.VISIBLE);
        if (captureResultButton != null) captureResultButton.setVisibility(View.GONE);

        List<Integer> qList = new ArrayList<>(lastDetectedAnswers.keySet());
        Collections.sort(qList);
        
        // Feature #1.1: Two-Column Grid Layout
        // Create a container with 2-column layout using nested LinearLayouts
        LinearLayout gridContainer = new LinearLayout(this);
        gridContainer.setOrientation(LinearLayout.VERTICAL);
        gridContainer.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT));
        
        LinearLayout currentRow = null;
        int columnCount = 0;
        
        for (int i = 0; i < qList.size(); i++) {
            int q = qList.get(i);
            
            // Create new row every 2 items
            if (columnCount == 0) {
                currentRow = new LinearLayout(this);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                currentRow.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 
                    ViewGroup.LayoutParams.WRAP_CONTENT));
                currentRow.setPadding(0, 0, 0, dp(8)); // 8dp spacing between rows
                gridContainer.addView(currentRow);
            }
            
            // Create card for each cell
            MaterialCardView cell = new MaterialCardView(this);
            LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            cellParams.setMargins(columnCount == 0 ? 0 : dp(8), 0, 0, 0); // 8dp spacing between columns
            cell.setLayoutParams(cellParams);
            cell.setCardElevation(dp(2));
            cell.setRadius(dp(8));
            cell.setContentPadding(dp(8), dp(6), dp(8), dp(6));
            
            // Inner layout for Q# and EditText
            LinearLayout cellContent = new LinearLayout(this);
            cellContent.setOrientation(LinearLayout.HORIZONTAL);
            cellContent.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.WRAP_CONTENT));
            
            TextView tv = new TextView(this);
            tv.setText(String.format(Locale.US, "#%d:", q));
            tv.setTextSize(14);
            tv.setTextColor(Color.BLACK);
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            tv.setPadding(0, 0, dp(6), 0);
            cellContent.addView(tv);

            // Universal input - always use EditText for both letters and words
            EditText et = new EditText(this);
            et.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            et.setTextSize(14);
            et.setPadding(dp(4), 0, dp(4), 0);
            et.setMinHeight(dp(36));
            String val = lastDetectedAnswers.get(q);
            et.setText(val != null ? val : "");
            et.setTextColor(Color.BLACK);
            et.setTypeface(et.getTypeface(), Typeface.BOLD);
            cellContent.addView(et);
            parsedEditors.put(q, et);
            
            cell.addView(cellContent);
            currentRow.addView(cell);
            
            columnCount++;
            if (columnCount >= 2) {
                columnCount = 0;
            }
        }
        
        // If last row has only 1 item, add spacer for balance
        if (columnCount == 1 && currentRow != null) {
            View spacer = new View(this);
            LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            spacerParams.setMargins(dp(8), 0, 0, 0);
            spacer.setLayoutParams(spacerParams);
            currentRow.addView(spacer);
        }
        
        parsedAnswersContainer.addView(gridContainer);
    }

    private void gatherEditedAnswers() {
        if (parsedEditors.isEmpty()) return;

        HashMap<Integer, String> edited = new HashMap<>();
        List<Integer> sorted = new ArrayList<>(parsedEditors.keySet());
        Collections.sort(sorted);

        for (int q : sorted) {
            View v = parsedEditors.get(q);
            String val = "";
            if (v instanceof EditText) {
                val = ((EditText) v).getText().toString().trim();
            }
            
            // Apply universal validation to the edited answer
            String validated = validateUniversalAnswer(val);
            if (validated != null && !validated.isEmpty()) {
                edited.put(q, validated);
            }
        }
        lastDetectedAnswers = edited;
    }

    private void computeAndDisplayScoreAfterConfirm() {
        if (sessionScoreTextView == null) return;

        if (currentAnswerKey == null || currentAnswerKey.isEmpty()) {
            Toast.makeText(this, "Set up an answer key first.", Toast.LENGTH_LONG).show();
            return;
        }
        if (lastDetectedAnswers == null || lastDetectedAnswers.isEmpty()) {
            Toast.makeText(this, "No parsed answers to score.", Toast.LENGTH_SHORT).show();
            return;
        }

        int total = currentAnswerKey.size();
        int correct = 0;
        int answered = 0;

        for (HashMap.Entry<Integer, String> e : currentAnswerKey.entrySet()) {
            int q = e.getKey();
            String expected = e.getValue();
            String got = lastDetectedAnswers.get(q);
            if (got != null) {
                answered++;
                if (expected.equalsIgnoreCase(got)) correct++;
            }
        }

        double pct = total > 0 ? (100.0 * correct / total) : 0.0;
        String summary = String.format(Locale.US, "Score: %d/%d (%.1f%%) • Answered: %d • Missed: %d",
                correct, total, pct, answered, Math.max(0, total - answered));

        sessionScoreTextView.setText(summary);

        // keep last computed numbers if you later want to save
        lastConfirmedScore = correct;
        lastConfirmedTotal = total;

        Toast.makeText(this, "Scored. Tap Save to add to history.", Toast.LENGTH_SHORT).show();
    }

    // Add missing method to fix compile error and enable saving to history.
    private void captureAndRecord() {
        if (lastDetectedAnswers == null || lastDetectedAnswers.isEmpty()) {
            triggerStillCapture();
            return;
        }
        if (lastConfirmedScore == null || lastConfirmedTotal == null) {
            gatherEditedAnswers();
            computeAndDisplayScoreAfterConfirm();
            if (lastConfirmedScore == null || lastConfirmedTotal == null) {
                Toast.makeText(this, "Score not computed. Try again.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        try {
            org.json.JSONObject rec = new org.json.JSONObject();
            String ts = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US)
                    .format(new java.util.Date());
            String student = studentNameInput != null ? studentNameInput.getText().toString().trim() : "";
            String section = sectionNameInput != null ? sectionNameInput.getText().toString().trim() : "";
            String exam = examNameInput != null ? examNameInput.getText().toString().trim() : "";
            int score = lastConfirmedScore != null ? lastConfirmedScore : 0;
            int total = lastConfirmedTotal != null ? lastConfirmedTotal : 0;
            double percent = total > 0 ? (100.0 * score / total) : 0.0;

            // Answers as JSON object
            org.json.JSONObject answersObj = new org.json.JSONObject();
            if (lastDetectedAnswers != null) {
                for (Map.Entry<Integer, String> e : lastDetectedAnswers.entrySet()) {
                    answersObj.put(String.valueOf(e.getKey()), e.getValue());
                }
            }

            rec.put("ts", ts);
            rec.put("student", student);
            rec.put("section", section.isEmpty() ? "Unsectioned" : section);
            rec.put("exam", exam.isEmpty() ? "Untitled Quiz" : exam);
            rec.put("score", score);
            rec.put("total", total);
            rec.put("percent", percent);
            rec.put("answers", answersObj);

            org.json.JSONArray history = getHistoryArray();
            history.put(rec);
            saveHistoryArray(history);

            sessionScoreTextView.setText(String.format(java.util.Locale.US, "Saved: %d/%d (%.1f%%)", score, total, percent));
            if (captureResultButton != null) captureResultButton.setText("Take Photo");

            Toast.makeText(this, "Result saved to history.", Toast.LENGTH_SHORT).show();

            lastConfirmedScore = null;
            lastConfirmedTotal = null;
            lastDetectedAnswers = new HashMap<>();
            parsedEditors.clear();
            if (parsedAnswersContainer != null) parsedAnswersContainer.removeAllViews();
            if (parsedLabel != null) parsedLabel.setText("Saved. Tap 'Scan Again' to take another.");
        } catch (Exception e) {
            Log.e(TAG, "Saving record failed", e);
            Toast.makeText(this, "Failed to save record.", Toast.LENGTH_LONG).show();
        }
    }
    
    // ---------------------------
    // Multi-image import and crop handlers (Feature #2 & #2.1)
    // ---------------------------
    
    /**
     * Handle multiple images imported from gallery.
     * Process each image sequentially, parse answers, and merge results.
     * Deterministic merge: first non-blank value wins.
     */
    private void onPhotosImported(java.util.List<android.net.Uri> uris) {
        if (uris == null || uris.isEmpty()) {
            return;
        }
        
        Log.d(TAG, "Importing " + uris.size() + " photo(s)");
        
        // Show progress dialog
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setTitle("Processing Images");
        progressDialog.setMessage(String.format(Locale.US, "Processing %d image(s)…", uris.size()));
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        // Process images on background thread
        new Thread(() -> {
            HashMap<Integer, String> mergedAnswers = new HashMap<>();
            int processedCount = 0;
            
            for (android.net.Uri uri : uris) {
                try {
                    // Load bitmap from URI
                    java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (inputStream != null) inputStream.close();
                    
                    if (bitmap == null) {
                        Log.e(TAG, "Failed to decode image from URI: " + uri);
                        continue;
                    }
                    
                    // Resize and compress
                    byte[] processedJpeg = ImageUtil.resizeAndCompress(bitmap, 1600);
                    bitmap.recycle();
                    
                    // OCR
                    String recognizedText = callVisionApiAndRecognize(processedJpeg);
                    if (recognizedText == null) recognizedText = "";
                    
                    // Parse answers
                    HashMap<Integer, String> parsed = parseAnswersFromText(recognizedText);
                    
                    // Merge: first non-blank value wins
                    for (Map.Entry<Integer, String> entry : parsed.entrySet()) {
                        if (!mergedAnswers.containsKey(entry.getKey()) || 
                            mergedAnswers.get(entry.getKey()).isEmpty()) {
                            mergedAnswers.put(entry.getKey(), entry.getValue());
                        }
                    }
                    
                    processedCount++;
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error processing image: " + uri, e);
                }
            }
            
            final int finalProcessed = processedCount;
            final HashMap<Integer, String> finalMerged = mergedAnswers;
            
            // Update UI on main thread
            runOnUiThread(() -> {
                progressDialog.dismiss();
                
                lastDetectedAnswers = finalMerged;
                populateParsedAnswersEditable();
                
                // Show status
                int filled = 0;
                int total = currentAnswerKey.size();
                for (Integer q : currentAnswerKey.keySet()) {
                    if (finalMerged.containsKey(q) && !finalMerged.get(q).isEmpty()) {
                        filled++;
                    }
                }
                
                String statusMsg = String.format(Locale.US, 
                    "Processed %d image(s) • Filled %d / %d answers", 
                    finalProcessed, filled, total);
                parsedLabel.setText(statusMsg);
                
                sessionScoreTextView.setText(getString(R.string.live_score_placeholder));
                
                Toast.makeText(this, statusMsg, Toast.LENGTH_LONG).show();
            });
            
        }).start();
    }
    
    /**
     * Handle crop result from uCrop.
     */
    private void onCropResult(androidx.activity.result.ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            android.net.Uri croppedUri = com.yalantis.ucrop.UCrop.getOutput(result.getData());
            if (croppedUri != null) {
                // Process cropped image
                processCroppedImage(croppedUri);
            }
        } else if (result.getResultCode() == com.yalantis.ucrop.UCrop.RESULT_ERROR) {
            Throwable cropError = com.yalantis.ucrop.UCrop.getError(result.getData());
            Log.e(TAG, "Crop error", cropError);
            Toast.makeText(this, "Crop failed", Toast.LENGTH_SHORT).show();
        }
        // User canceled crop: do nothing, return to camera
    }
    
    /**
     * Process the cropped image - run OCR and parse answers.
     */
    private void processCroppedImage(android.net.Uri croppedUri) {
        new Thread(() -> {
            try {
                java.io.InputStream inputStream = getContentResolver().openInputStream(croppedUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (inputStream != null) inputStream.close();
                
                if (bitmap == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Failed to load cropped image", Toast.LENGTH_SHORT).show());
                    return;
                }
                
                // Resize and compress
                byte[] processedJpeg = ImageUtil.resizeAndCompress(bitmap, 1600);
                bitmap.recycle();
                
                // OCR
                String recognizedText = callVisionApiAndRecognize(processedJpeg);
                if (recognizedText == null) recognizedText = "";
                
                // Parse answers
                HashMap<Integer, String> parsed = parseAnswersFromText(recognizedText);
                lastDetectedAnswers = parsed;
                
                // Update UI
                runOnUiThread(() -> {
                    populateParsedAnswersEditable();
                    sessionScoreTextView.setText(getString(R.string.live_score_placeholder));
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error processing cropped image", e);
                runOnUiThread(() -> Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
    
    /**
     * Start crop activity for the captured image.
     */
    private void startCropActivity(android.net.Uri sourceUri) {
        try {
            // Create destination URI for cropped image
            String destFileName = "cropped_" + System.currentTimeMillis() + ".jpg";
            android.net.Uri destUri = android.net.Uri.fromFile(new java.io.File(getCacheDir(), destFileName));
            
            // Configure uCrop
            com.yalantis.ucrop.UCrop.Options options = new com.yalantis.ucrop.UCrop.Options();
            options.setFreeStyleCropEnabled(true);
            options.setShowCropGrid(true);
            options.setShowCropFrame(true);
            
            android.content.Intent cropIntent = com.yalantis.ucrop.UCrop.of(sourceUri, destUri)
                    .withOptions(options)
                    .getIntent(this);
            
            cropLauncher.launch(cropIntent);
            
        } catch (Exception e) {
            Log.e(TAG, "Error starting crop activity", e);
            Toast.makeText(this, "Crop not available", Toast.LENGTH_SHORT).show();
        }
    }
}

