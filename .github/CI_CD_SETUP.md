# CI/CD Pipeline Setup

This project uses GitHub Actions for continuous integration and continuous deployment.

## Workflows

### 1. PR Checks (`pr-checks.yml`)

Runs on every pull request to `main` or `master` branch:

- **Lint**: Runs Android lint to check code quality
- **Unit Tests**: Executes all unit tests
- **Instrumented Tests**: Runs UI tests on an Android emulator
- **Build**: Builds debug APK

Lint and Unit Tests run as two legs of the same matrix job so they execute in parallel; the
matrix's `fail-fast` behavior means that as soon as one of them fails, the other is cancelled
instead of running to completion, saving CI time on a PR that's already broken. Instrumented
Tests and Build only start once both have passed.

All jobs must pass before a PR can be merged.

### 2. Release (`release.yml`)

Runs when a pull request targeting `main` or `master` is **merged** (`pull_request` with
`types: [closed]`, gated by `github.event.pull_request.merged == true`). Closing a PR without
merging does not trigger a release, and neither does a direct push that bypasses PR review:

- **Test**: Runs lint and unit tests on release variant, against the actual merge commit
- **Build Release**: Creates a signed APK and AAB bundle. The signing secrets below are
  **required** — the job fails fast with a clear error if `KEYSTORE_BASE64` is missing, rather
  than silently producing an unsigned build
- **Verify APK signature**: Runs `apksigner verify` on the built APK so the workflow log always
  shows proof the artifact was actually signed (and with which certificate)
- **Generate Release Notes**: Automatically generates release notes from commits
- **Create Release**: Creates a GitHub release (via `gh release create`) with the version tag,
  attaching both the signed APK and AAB
- **Upload to Play Store**: currently **disabled** (`if: false` on the step) until Play Store
  publishing is ready. Remove the `if: false` line to re-enable it.

## Required Secrets

To enable signing (required for every release) and, later, Play Store deployment, add the
following secrets in GitHub:

### Signing Secrets (required)

1. **KEYSTORE_BASE64**: Base64-encoded release keystore file
   ```bash
   base64 -i release-keystore.jks | pbcopy
   ```

2. **KEYSTORE_PASSWORD**: Password for the keystore

3. **KEY_ALIAS**: Alias of the signing key

4. **KEY_PASSWORD**: Password for the signing key

Without these four secrets, the `build-release` job fails intentionally at the "Decode
Keystore" step instead of publishing an unsigned release.

### Play Store Secrets (not currently used — upload step is disabled)

5. **PLAY_STORE_JSON_KEY**: Service account JSON key for Play Store API
   - Create a service account in Google Play Console
   - Grant "Release Manager" role
   - Create and download JSON key
   - Paste entire JSON content as secret value

## Branch Protection Rules

To enforce quality gates, configure the following branch protection rules for the default
branch (currently `master` in this repo; the workflows also match `main` in case it's renamed
later):

1. Go to Repository Settings → Branches → Add Rule
2. Branch name pattern: `master` (or `main`)
3. Enable:
   - ✅ Require a pull request before merging
   - ✅ Require approvals: 1
   - ✅ Require status checks to pass before merging
   - ✅ Require branches to be up to date before merging
4. Required status checks:
   - `Lint Code`
   - `Unit Tests`
   - `Instrumented Tests`
   - `Build APK`
5. Save changes

## Release Notes

Release notes are automatically generated from commit messages between tags. Follow conventional commits for better changelog:

- `feat:` - New features
- `fix:` - Bug fixes
- `docs:` - Documentation changes
- `refactor:` - Code refactoring
- `test:` - Test additions/changes
- `chore:` - Build/tooling changes

## WhatsNew Directory

For Play Store releases, create `distribution/whatsnew/` directory with localized release notes:

```
distribution/
  whatsnew/
    en-US.txt  # English release notes
    pt-PT.txt  # Portuguese release notes
```

Each file should contain release notes for that language (max 500 characters).

## Local Testing

Test workflows locally using [act](https://github.com/nektos/act):

```bash
# Install act
brew install act

# Run PR checks (matrix job - runs both the lint and unit-tests legs)
act pull_request -j checks

# Run the release workflow. It triggers on a merged pull_request, so act needs an event
# payload with `pull_request.merged: true` (a plain `act pull_request` payload defaults to
# merged: false and the job will be skipped by design):
echo '{"pull_request": {"merged": true, "number": 1, "merge_commit_sha": "HEAD", "base": {"ref": "master"}}}' > /tmp/pr-merged-event.json
act pull_request -j build-release -e /tmp/pr-merged-event.json
```

## Gradle Configuration

The build.gradle.kts should define version:

```kotlin
android {
    defaultConfig {
        versionCode = 2
        versionName = "2.0"
    }
}
```

Version is automatically extracted and used for release tagging.

`app/build.gradle.kts` also declares an empty `signingConfigs.release` and only attaches it to
the `release` build type when the `android.injected.signing.store.file` Gradle property is
present (`release.yml` passes it, along with the store/key password and key alias, on the
command line). This keeps local `./gradlew assembleRelease` runs working unsigned for
day-to-day development while guaranteeing CI produces a properly signed artifact.
