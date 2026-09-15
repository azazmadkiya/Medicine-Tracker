import re

with open(".github/workflows/build-apk.yml", "r") as f:
    content = f.read()

# Update name
content = content.replace("name: Build and Release Debug APK", "name: Build and Release APK & AAB")

# Update Build command
content = content.replace(
    "gradle :app:assembleDebug --stacktrace --no-daemon",
    "gradle :app:assembleDebug :app:bundleRelease --stacktrace --no-daemon"
)

# Update Verification
old_verify = """      - name: Verify the APK
        run: |
          APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
          if [ ! -f "$APK_PATH" ]; then
            echo "Error: APK not found at $APK_PATH."
            echo "Listing contents of app/build/outputs/apk (if exists):"
            find app/build/outputs/apk -type f 2>/dev/null || echo "Directory does not exist."
            exit 1
          fi
          if [ ! -s "$APK_PATH" ]; then
            echo "Error: APK file is empty."
            exit 1
          fi
          if [[ "$APK_PATH" != *.apk ]]; then
            echo "Error: Not an .apk file."
            exit 1
          fi
          echo "Verified APK at $APK_PATH\""""

new_verify = """      - name: Verify the APK and AAB
        run: |
          APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
          AAB_PATH="app/build/outputs/bundle/release/app-release.aab"
          
          if [ ! -f "$APK_PATH" ]; then
            echo "Error: APK not found at $APK_PATH."
            exit 1
          fi
          if [ ! -f "$AAB_PATH" ]; then
            echo "Error: AAB not found at $AAB_PATH."
            exit 1
          fi
          
          echo "Verified APK at $APK_PATH"
          echo "Verified AAB at $AAB_PATH\""""

content = content.replace(old_verify, new_verify)

# Upload Artifacts
old_upload = """      - name: Upload debug APK artifact
        uses: actions/upload-artifact@v4
        with:
          name: app-debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
          if-no-files-found: error"""
new_upload = """      - name: Upload debug APK artifact
        uses: actions/upload-artifact@v4
        with:
          name: app-debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
          if-no-files-found: error

      - name: Upload release AAB artifact
        uses: actions/upload-artifact@v4
        with:
          name: app-release-aab
          path: app/build/outputs/bundle/release/app-release.aab
          if-no-files-found: error"""

content = content.replace(old_upload, new_upload)

# Prepare Asset
old_prepare = """      - name: Prepare APK release asset
        id: prepare_asset
        run: |
          TMP_DIR=$(mktemp -d)
          REPO_NAME_SANITIZED=$(basename ${{ github.repository }} | tr -cd '[:alnum:]_-')
          ASSET_NAME="${REPO_NAME_SANITIZED}-debug-build-${{ github.run_number }}.apk"
          ASSET_PATH="${TMP_DIR}/${ASSET_NAME}"
          cp app/build/outputs/apk/debug/app-debug.apk "$ASSET_PATH"
          echo "asset_path=$ASSET_PATH" >> $GITHUB_OUTPUT
          echo "asset_name=$ASSET_NAME" >> $GITHUB_OUTPUT"""
          
new_prepare = """      - name: Prepare Release Assets
        id: prepare_asset
        run: |
          TMP_DIR=$(mktemp -d)
          REPO_NAME_SANITIZED=$(basename ${{ github.repository }} | tr -cd '[:alnum:]_-')
          
          APK_NAME="${REPO_NAME_SANITIZED}-debug-build-${{ github.run_number }}.apk"
          APK_PATH="${TMP_DIR}/${APK_NAME}"
          cp app/build/outputs/apk/debug/app-debug.apk "$APK_PATH"
          
          AAB_NAME="${REPO_NAME_SANITIZED}-release-build-${{ github.run_number }}.aab"
          AAB_PATH="${TMP_DIR}/${AAB_NAME}"
          cp app/build/outputs/bundle/release/app-release.aab "$AAB_PATH"
          
          echo "apk_path=$APK_PATH" >> $GITHUB_OUTPUT
          echo "apk_name=$APK_NAME" >> $GITHUB_OUTPUT
          echo "aab_path=$AAB_PATH" >> $GITHUB_OUTPUT
          echo "aab_name=$AAB_NAME" >> $GITHUB_OUTPUT"""

content = content.replace(old_prepare, new_prepare)

# Release Creation
old_release = """      - name: Publish APK to GitHub Releases
        id: release
        continue-on-error: true
        env:
          GH_TOKEN: ${{ github.token }}
        run: |
          gh --version
          ASSET_PATH="${{ steps.prepare_asset.outputs.asset_path }}"
          if [ ! -f "$ASSET_PATH" ]; then
             echo "Error: Release asset not found at $ASSET_PATH"
             exit 1
          fi
          if [ ! -s "$ASSET_PATH" ]; then
             echo "Error: Release asset is empty."
             exit 1
          fi
          if [[ "$ASSET_PATH" != *.apk ]]; then
            echo "Error: Release asset is not an .apk file."
            exit 1
          fi
          
          TAG="debug-apk-build-${{ github.run_number }}-${{ github.run_attempt }}"
          TITLE="Debug APK Build #${{ github.run_number }}"
          
          NOTES=$(cat << EOF
          - This is an automatically generated debug APK.
          - The APK was built by GitHub Actions.
          - GitHub Actions Run Number: ${{ github.run_number }}
          - GitHub Actions Run Attempt: ${{ github.run_attempt }}
          - Source Commit SHA: ${{ github.sha }}
          - The APK is intended for testing.
          - The APK is not a Play Store production release.
          - The APK is signed only with a temporary debug keystore.
          EOF
          )
          
          gh release create "$TAG" "$ASSET_PATH" \\
            --title "$TITLE" \\
            --notes "$NOTES" \\
            --target "${{ github.sha }}" \\
            --latest"""
            
new_release = """      - name: Publish APK & AAB to GitHub Releases
        id: release
        continue-on-error: true
        env:
          GH_TOKEN: ${{ github.token }}
        run: |
          gh --version
          APK_PATH="${{ steps.prepare_asset.outputs.apk_path }}"
          AAB_PATH="${{ steps.prepare_asset.outputs.aab_path }}"
          
          if [ ! -f "$APK_PATH" ] || [ ! -f "$AAB_PATH" ]; then
             echo "Error: Release assets not found."
             exit 1
          fi
          
          TAG="app-build-${{ github.run_number }}-${{ github.run_attempt }}"
          TITLE="App Build #${{ github.run_number }} (APK & AAB)"
          
          NOTES=$(cat << EOF
          - Automatically generated release containing an APK (for direct testing) and an AAB (for Google Play).
          - GitHub Actions Run Number: ${{ github.run_number }}
          - GitHub Actions Run Attempt: ${{ github.run_attempt }}
          - Source Commit SHA: ${{ github.sha }}
          - The APK is a debug build. The AAB is an unsigned release build ready to be signed for the Play Store.
          EOF
          )
          
          gh release create "$TAG" "$APK_PATH" "$AAB_PATH" \\
            --title "$TITLE" \\
            --notes "$NOTES" \\
            --target "${{ github.sha }}" \\
            --latest"""

content = content.replace(old_release, new_release)


# Report Result
old_report = """      - name: Report APK download result
        if: always()
        env:
          GH_TOKEN: ${{ github.token }}
        run: |
          if [ "${{ steps.release.outcome }}" == "success" ]; then
            TAG="debug-apk-build-${{ github.run_number }}-${{ github.run_attempt }}"
            echo "Release published successfully!"
            gh release view "$TAG"
            
            RELEASE_URL="https://github.com/${{ github.repository }}/releases/tag/$TAG"
            echo "Release Tag: $TAG"
            echo "Release Title: Debug APK Build #${{ github.run_number }}"
            echo "Direct GitHub Release page URL: $RELEASE_URL"
            echo "APK Asset Filename: ${{ steps.prepare_asset.outputs.asset_name }}"
            
            echo "### ✅ Release Published" >> $GITHUB_STEP_SUMMARY
            echo "The APK can be downloaded from the release Assets section here: [Release Page]($RELEASE_URL)" >> $GITHUB_STEP_SUMMARY
            echo "The normal Actions artifact (\`app-debug-apk\`) is also available as a backup on the workflow run page." >> $GITHUB_STEP_SUMMARY
          else
            echo "⚠️ WARNING: Release publishing failed."
            echo "The APK build succeeded, but the release could not be created."
            echo "The Actions artifact named 'app-debug-apk' remains available."
            
            echo "### ⚠️ Release Publishing Failed" >> $GITHUB_STEP_SUMMARY
            echo "The APK build succeeded, but the release could not be created." >> $GITHUB_STEP_SUMMARY
            echo "The Actions artifact named \`app-debug-apk\` remains available as a backup on the workflow run page." >> $GITHUB_STEP_SUMMARY
          fi"""
          
new_report = """      - name: Report Build & Release result
        if: always()
        env:
          GH_TOKEN: ${{ github.token }}
        run: |
          if [ "${{ steps.release.outcome }}" == "success" ]; then
            TAG="app-build-${{ github.run_number }}-${{ github.run_attempt }}"
            echo "Release published successfully!"
            gh release view "$TAG"
            
            RELEASE_URL="https://github.com/${{ github.repository }}/releases/tag/$TAG"
            echo "Release Tag: $TAG"
            echo "Direct GitHub Release page URL: $RELEASE_URL"
            
            echo "### ✅ Release Published" >> $GITHUB_STEP_SUMMARY
            echo "The APK and AAB can be downloaded from the release Assets section here: [Release Page]($RELEASE_URL)" >> $GITHUB_STEP_SUMMARY
          else
            echo "⚠️ WARNING: Release publishing failed."
            echo "### ⚠️ Release Publishing Failed" >> $GITHUB_STEP_SUMMARY
            echo "The app build succeeded, but the release could not be created." >> $GITHUB_STEP_SUMMARY
            echo "The Actions artifacts (\`app-debug-apk\` and \`app-release-aab\`) remain available as backups on the workflow run page." >> $GITHUB_STEP_SUMMARY
          fi"""

content = content.replace(old_report, new_report)

with open(".github/workflows/build-apk.yml", "w") as f:
    f.write(content)

