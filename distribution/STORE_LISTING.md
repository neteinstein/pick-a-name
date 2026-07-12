# Play Store Listing Checklist

Reference for every field Google Play Console asks for when creating the store listing for
**Allowed Names in Portugal** (`org.neteinstein.pickaname`). **Default/main listing language:
Portuguese (Portugal) — `pt-PT`**, with English (`en-US`) as a secondary translation, matching
the app's own supported locales (`values/` and `values-pt/`).

Text fields are checked into this repo (see below); graphics and Console-only settings are not
representable as repo files and are called out explicitly as outstanding manual steps.

## 1. Create app

- **App name**: `Nomes Permitidos em Portugal` (see `title.txt` below)
- **Default language**: Portuguese (Portugal) — `pt-PT`
- **App or game**: App
- **Free or paid**: Free

## 2. Main store listing — text (✅ in this repo)

Source of truth: `distribution/metadata/android/<locale>/`. Character counts below include no
trailing newline and are within Play Console's limits.

| Field | Limit | `pt-PT` (default) | `en-US` |
|---|---|---|---|
| Title | 30 | `title.txt` — 28 chars | `title.txt` — 25 chars |
| Short description | 80 | `short_description.txt` — 74 chars | `short_description.txt` — 69 chars |
| Full description | 4000 | `full_description.txt` — 1731 chars | `full_description.txt` — 1502 chars |

Copy each file's contents verbatim into the matching Play Console field for that language (or
wire up `fastlane supply` / `triple-t/gradle-play-publisher` to publish them directly).

## 3. Main store listing — graphics (⚠️ not included — needs design/device work)

Not producible as source text; each needs an actual export/screenshot pass:

- **App icon** — 512×512, 32-bit PNG, no alpha. The in-app adaptive icon
  (`app/src/main/res/drawable/ic_launcher_{background,foreground,monochrome}.xml`) only has
  raster exports up to 192×192 (`mipmap-xxxhdpi`); generate a fresh 512×512 export from the
  vector layers (Android Studio → Image Asset Studio, or an SVG/vector export tool).
- **Feature graphic** — 1024×500, JPG or 24-bit PNG. No source asset exists yet; needs original
  design.
- **Phone screenshots** — at least 2 (up to 8), JPEG or 24-bit PNG, no alpha, each dimension
  between 320–3840px, max aspect ratio 2:1. Capture from a running device/emulator (splash,
  name list with filters open, settings).
- **Tablet / Chromebook / Wear / TV screenshots, promo video** — optional; skip unless
  targeting those form factors.

## 4. Store settings (Console-only — recommended values)

| Field | Recommended value |
|---|---|
| App category | **Books & Reference** (closest fit for a lookup/reference tool); *Lifestyle* is a reasonable alternative |
| Tags | Pick up to 5 from Play Console's own list, e.g. Reference, Government, Parenting |
| Contact email (required) | `neteinstein@gmail.com` |
| Website (optional) | `https://github.com/neteinstein/portuguese-allowed-names` |
| Phone (optional) | none |
| External marketing | Optional opt-in; no strong recommendation either way |

## 5. App content declarations (Console-only — recommended answers)

These match the app's actual behavior as documented in
[`PRIVACY_POLICY.md`](../PRIVACY_POLICY.md) (no accounts, no ads, no analytics, no data
collection):

| Declaration | Recommended answer |
|---|---|
| Privacy Policy URL | `https://github.com/neteinstein/portuguese-allowed-names/blob/master/PRIVACY_POLICY.md` |
| App access | "All functionality is available without special access" (no login) |
| Ads | "No, my app does not contain ads" |
| Content rating questionnaire — category | "Reference, News, or Educational" |
| Content rating questionnaire — content | Answer "No" to all violence/sexual/gambling/controlled-substance/user-generated-content questions → expect Everyone / PEGI 3 / USK 0 (confirm by completing the questionnaire in Console) |
| Target audience | Primary audience 18+ (app is a utility for adults/parents); answer "No" to "designed to appeal primarily to children" |
| News app | No |
| COVID-19 contact tracing/status app | Not applicable |
| Government app | No |
| Financial features | No |
| Data safety — collects/shares user data? | **No data collected** |
| Data safety — has a privacy policy? | Yes — URL above |

## 6. Release

Signed AAB build + GitHub release is already automated (`.github/workflows/release.yml`). The
`upload-google-play` step exists but is intentionally disabled (`if: false`) until:

1. `PLAY_STORE_JSON_KEY` secret is configured (see `.github/CI_CD_SETUP.md`), and
2. The app package (`org.neteinstein.pickaname`) has been created and had at least one manual
   AAB/APK upload in Play Console — the Play Developer API used by that action can only update
   an app that already exists there, it cannot create one from scratch.
