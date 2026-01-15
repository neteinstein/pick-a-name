# CI/CD Pipeline Setup

This project uses GitHub Actions for continuous integration and continuous deployment.

## Workflows

### 1. PR Checks (`pr-checks.yml`)

Runs on every pull request to `main` or `master` branch:

- **Lint**: Runs Android lint to check code quality
- **Unit Tests**: Executes all unit tests
- **Instrumented Tests**: Runs UI tests on an Android emulator
- **Build**: Builds debug APK

All jobs must pass before a PR can be merged.

### 2. Release (`release.yml`)

Runs on every push to `main` or `master` branch:

- **Test**: Runs lint and unit tests on release variant
- **Build Release**: Creates signed APK and AAB bundles
- **Generate Release Notes**: Automatically generates release notes from commits
- **Create Release**: Creates a GitHub release with version tag
- **Upload to Play Store**: Deploys to Google Play Store (when configured)

## Required Secrets

To enable signing and Play Store deployment, add the following secrets in GitHub:

### Signing Secrets

1. **KEYSTORE_BASE64**: Base64-encoded release keystore file
   ```bash
   base64 -i release-keystore.jks | pbcopy
   ```

2. **KEYSTORE_PASSWORD**: Password for the keystore

3. **KEY_ALIAS**: Alias of the signing key

4. **KEY_PASSWORD**: Password for the signing key

### Play Store Secrets

5. **PLAY_STORE_JSON_KEY**: Service account JSON key for Play Store API
   - Create a service account in Google Play Console
   - Grant "Release Manager" role
   - Create and download JSON key
   - Paste entire JSON content as secret value

## Branch Protection Rules

To enforce quality gates, configure the following branch protection rules for `main`:

1. Go to Repository Settings → Branches → Add Rule
2. Branch name pattern: `main`
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

# Run PR checks
act pull_request -j lint
act pull_request -j unit-tests

# Run release workflow
act push -j build-release
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
