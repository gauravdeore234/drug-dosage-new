package com.drugdosage;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DrugRepository {

    private static final String PREFS = "drug_prefs";
    private static final String KEY_DRUGS = "drugs_json";
    private static final Gson gson = new Gson();

    public static List<DrugData> getDrugs(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_DRUGS, null);
        if (json != null) {
            Type type = new TypeToken<List<DrugData>>(){}.getType();
            List<DrugData> list = gson.fromJson(json, type);
            if (list != null && !list.isEmpty()) return list;
        }
        return getDefaults();
    }

    public static void saveDrugs(Context ctx, List<DrugData> drugs) {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_DRUGS, gson.toJson(drugs)).apply();
    }

    public static List<DrugData> getDefaults() {
        List<DrugData> list = new ArrayList<>();

        // Formula: output_ml_hr = doseMin(unit/kg/hr) * weight(kg) * syringeMlPerUnit(ml/unit)
        // syringeMlPerUnit = 1 / concentration(unit/ml)

        // Midazolam: 0.2-0.3 mg/kg/hr, 25mg in 25ml NS = 0.5 mg/ml → 1/0.5 = 2 ml/mg
        list.add(new DrugData("Midazolam",
                "25 mg Midazolam in 25 ml NS (0.5 mg/ml)",
                0.2, 0.3, 2.0, true));

        // Propofol: 3-7 mg/kg/hr, 50ml Propofol (10mg/ml) → 1/10 = 0.1 ml/mg
        list.add(new DrugData("Propofol",
                "50 ml Propofol (10 mg/ml)",
                3.0, 7.0, 0.1, true));

        // Fentanyl: 0.5-1 µg/kg/hr, 200µg in 50ml NS = 4 µg/ml → 0.25 ml/µg
        list.add(new DrugData("Fentanyl",
                "2 ampules (200 µg) in 50 ml NS (4 µg/ml)",
                0.5, 1.0, 0.25, true));

        // Dexmedetomidine: 0.2-0.7 µg/kg/hr, 200µg in 50ml NS = 4 µg/ml → 0.25 ml/µg
        list.add(new DrugData("Dexmedetomidine",
                "1 ampule (200 µg) in 50 ml NS (4 µg/ml)",
                0.2, 0.7, 0.25, true));

        // Atracurium: 0.2-0.3 mg/kg/hr, 125mg in 50ml NS = 2.5 mg/ml → 0.4 ml/mg
        list.add(new DrugData("Atracurium",
                "5 ampules (125 mg) in 50 ml NS (2.5 mg/ml)",
                0.2, 0.3, 0.4, true));

        // Vecuronium: 0.05-0.07 mg/kg/hr, 28mg in 50ml NS = 0.56 mg/ml → 1.7857 ml/mg
        list.add(new DrugData("Vecuronium",
                "7 vials (28 mg) in 50 ml NS (0.56 mg/ml)",
                0.05, 0.07, 1.0/0.56, true));

        // Dopamine D1&D2: 2-5 µg/kg/min → converted to mg/kg/hr: *60/1000
        // 400mg in 50ml D5 = 8 mg/ml → 0.125 ml/mg
        list.add(new DrugData("Dopamine (D1 & D2)",
                "2 ampules (400 mg) in 50 ml D5 (8 mg/ml)",
                2*60.0/1000, 5*60.0/1000, 0.125, true));

        list.add(new DrugData("Dopamine (β)",
                "2 ampules (400 mg) in 50 ml D5 (8 mg/ml)",
                5*60.0/1000, 10*60.0/1000, 0.125, true));

        list.add(new DrugData("Dopamine (α + β)",
                "2 ampules (400 mg) in 50 ml D5 (8 mg/ml)",
                10*60.0/1000, 20*60.0/1000, 0.125, true));

        // Dobutamine: 5-20 µg/kg/min → mg/kg/hr, 500mg in 50ml D5 = 10mg/ml → 0.1 ml/mg
        list.add(new DrugData("Dobutamine",
                "2 ampules (500 mg) in 50 ml D5 (10 mg/ml)",
                5*60.0/1000, 20*60.0/1000, 0.1, true));

        // Noradrenaline bitartarate: 0.01-0.5 µg/kg/min → mg/kg/hr, conc 0.08 mg/ml → 12.5 ml/mg
        list.add(new DrugData("Noradrenaline (bitartarate)",
                "4 ampules noradrenaline bitartarate (2 mg/ml) in 50 ml D5 (0.08 mg/ml)",
                0.01*60.0/1000, 0.5*60.0/1000, 1.0/0.08, true));

        // Noradrenaline HCl: 0.5-1 µg/kg/min → mg/kg/hr, conc 0.08 mg/ml → 12.5 ml/mg
        list.add(new DrugData("Noradrenaline (HCl)",
                "2 ampules noradrenaline HCl (1 mg/ml) in 50 ml D5 (0.08 mg/ml)",
                0.5*60.0/1000, 1*60.0/1000, 1.0/0.08, true));

        // Vasopressin: 0.02-0.05 units/min → hr: *60, 40 units in 50ml D5 = 0.8 units/ml → 1.25 ml/unit
        // Weight independent
        list.add(new DrugData("Vasopressin",
                "2 ampules (40 units) in 50 ml D5 (0.8 units/ml)",
                0.02*60, 0.05*60, 1.0/0.8, false));

        // NTG: 0.005-0.02 mg/min → hr: *60, 25mg in 50ml NS = 0.5 mg/ml → 2 ml/mg
        // Weight independent
        list.add(new DrugData("Nitroglycerine (NTG)",
                "1 ampule (25 mg) in 50 ml NS (0.5 mg/ml)",
                0.005*60, 0.02*60, 2.0, false));

        return list;
    }
}
