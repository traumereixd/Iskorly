const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

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