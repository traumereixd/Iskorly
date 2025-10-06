\# API Key Security



This app calls Google Cloud Vision from the device. Treat API keys as sensitive.

Best practice is to proxy calls through your own backend. If you keep using

the key in the client, lock it down tightly.



\## 1) Rotate the Vision API key



1\. Go to Google Cloud Console → APIs \& Services → Credentials.

2\. Create a new API key.

3\. Restrict the new key to:

&nbsp;  - API restrictions: “Restrict key” → enable only “Cloud Vision API”.

&nbsp;  - Application restrictions: “Android apps” → add:

&nbsp;    - Package: com.bandecoot.itemscoreanalysisprogram

&nbsp;    - SHA‑1 of your signing certs (debug and release).

&nbsp;      - Debug: `keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android`

&nbsp;      - Release: `keytool -list -v -keystore <your-release.keystore>`

4\. Update `local.properties` (not committed) with:

&nbsp;  ```

&nbsp;  GCLOUD\_VISION\_API\_KEY=YOUR\_NEW\_KEY\_HERE

&nbsp;  ```

5\. Rebuild and test.



\## 2) Remove any leaked keys from Git history



If a key ever landed in Git, rotate it (above), then purge history:



Using `git filter-repo` (recommended):

```bash

pip install git-filter-repo

git filter-repo --path local.properties --invert-paths

git push --force

```



Or using BFG:

```bash

java -jar bfg.jar --delete-files local.properties

git reflog expire --expire=now --all

git gc --prune=now --aggressive

git push --force

```



\## 3) Notes



\- Keys in Android apps can be extracted; prefer server-side calls for production.

\- This project already reads the Vision key from `local.properties` or a Gradle property and injects it into `BuildConfig`. Never hardcode the key in source.

