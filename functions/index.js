const functions = require("firebase-functions");
const admin = require("firebase-admin");
const path = require("path");
const os = require("os");
const fs = require("fs").promises;

admin.initializeApp();

const bucket = admin.storage().bucket();

// HTTP endpoint to serve kill-switch status from Firestore
exports.appStatus = functions.https.onRequest(async (req, res) => {
  try {
    const snap = await admin.firestore()
      .collection("app_config")
      .doc("status")
      .get();

    const data = snap.exists ? snap.data() : { disabled: false, message: "" };

    res.set("Cache-Control", "no-cache, no-store, must-revalidate");
    res.json({
      disabled: !!data.disabled,
      message: data.message || "App temporarily disabled."
    });
  } catch (e) {
    res.status(500).json({ disabled: false, message: "" });
  }
});

// Firestore trigger to sync kill-switch status to Storage bucket
// This ensures a public JSON file stays in sync with Firestore
// The file can be accessed at: https://storage.googleapis.com/{bucket}/app-status.json
exports.syncKillSwitchToStorage = functions.firestore
    .document("app_config/status")
    .onWrite(async (change, context) => {
      try {
        // Get the current data (after write)
        const data = change.after.exists ? change.after.data() : {
          disabled: false,
          message: "",
        };

        // Normalize the data
        const statusData = {
          disabled: !!data.disabled,
          message: data.message || "App temporarily disabled.",
        };

        console.log("Syncing kill-switch status to Storage:", statusData);

        // Create temporary file with the JSON content
        const tempFilePath = path.join(os.tmpdir(), "app-status.json");
        await fs.writeFile(
            tempFilePath,
            JSON.stringify(statusData, null, 2),
            "utf8"
        );

        // Upload to the storage bucket as a public file
        const file = bucket.file("app-status.json");
        await bucket.upload(tempFilePath, {
          destination: "app-status.json",
          metadata: {
            contentType: "application/json",
            cacheControl: "no-cache, no-store, must-revalidate",
          },
        });

        // Make the file publicly readable
        await file.makePublic();

        // Clean up temp file
        await fs.unlink(tempFilePath);

        console.log("Successfully synced kill-switch status to Storage");
        console.log("Access at: https://storage.googleapis.com/" +
                    bucket.name + "/app-status.json");
        return null;
      } catch (error) {
        console.error("Error syncing kill-switch status:", error);
        // Don't throw - we don't want to fail the Firestore write
        return null;
      }
    });