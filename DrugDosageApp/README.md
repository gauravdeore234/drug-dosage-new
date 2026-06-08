# Drug Dosage Calculator — Android App

## What it does
- **Drug dropdown**: All 14 drug entries from the Doctor Input sheet (Midazolam, Propofol, Fentanyl, Dexmedetomidine, Atracurium, Vecuronium, Dopamine × 3 modes, Dobutamine, Noradrenaline × 2, Vasopressin, NTG)
- **Weight input**: Positive number up to 2 decimal places (kg)
- **Output**: Preparation string + dosage as `(min – max) ml/hr`, calculated live
- **Edit screen**: Add / edit / delete drugs with full parameter control

## Calculation logic (from your Excel)
```
Output (ml/hr) = doseMin_per_kg_per_hr × weight_kg × (1 / concentration_per_ml)
```
- For weight-independent drugs (Vasopressin, NTG): weight is not multiplied
- All µg/min doses are pre-converted to mg/hr equivalents to keep one formula

## How to build in Android Studio

1. Open **Android Studio** → **File → Open** → select the `DrugDosageApp` folder
2. Wait for Gradle sync to finish
3. Connect an Android device (API 24+) or start an emulator
4. Click **Run ▶**

Or build APK via terminal:
```bash
cd DrugDosageApp
./gradlew assembleDebug
# APK at: app/build/outputs/apk/debug/app-debug.apk
```

## Editing drugs at runtime
- Tap the **✏️ pencil icon** in the top-right toolbar on the main screen
- Tap any drug to edit its name, preparation string, dose range, and concentration
- Long-press a drug to delete it
- Tap **+** to add a new drug
- Changes are saved locally and persist across app restarts

### Fields explained
| Field | Example | Notes |
|---|---|---|
| Drug Name | Midazolam | Shown in dropdown |
| Preparation String | 25 mg Midazolam in 25 ml NS (0.5 mg/ml) | Shown as output |
| Dose Min | 0.2 | In unit/kg/hr (or unit/hr for fixed) |
| Dose Max | 0.3 | In unit/kg/hr (or unit/hr for fixed) |
| Concentration | 0.5 | unit/ml of syringe solution |
| Weight-dependent | ✓ | Uncheck for Vasopressin, NTG |

## Dependencies
- AndroidX AppCompat 1.7.0
- Material Components 1.12.0
- CardView 1.0.0
- Gson 2.10.1 (for persisting drug list as JSON)
- Min SDK: 24 (Android 7.0)
