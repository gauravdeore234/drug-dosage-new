package com.drugdosage;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerDrug;
    private EditText etWeight;
    private TextView tvPreparation, tvDosage, tvDosageLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));

        spinnerDrug = findViewById(R.id.spinnerDrug);
        etWeight = findViewById(R.id.etWeight);
        tvPreparation = findViewById(R.id.tvPreparation);
        tvDosage = findViewById(R.id.tvDosage);
        tvDosageLabel = findViewById(R.id.tvDosageLabel);

        setupDrugSpinner();

        spinnerDrug.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                calculateDosage();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etWeight.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { calculateDosage(); }
        });
    }

    private void setupDrugSpinner() {
        List<String> drugNames = new ArrayList<>();
        drugNames.add("Select Drug");
        for (DrugData drug : DrugRepository.getDrugs(this)) {
            drugNames.add(drug.name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, drugNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDrug.setAdapter(adapter);
    }

    private void calculateDosage() {
        int pos = spinnerDrug.getSelectedItemPosition();
        String weightStr = etWeight.getText().toString().trim();

        if (pos == 0 || weightStr.isEmpty()) {
            tvPreparation.setText("—");
            tvDosage.setText("—");
            return;
        }

        double weight;
        try {
            weight = Double.parseDouble(weightStr);
            if (weight <= 0) {
                tvPreparation.setText("—");
                tvDosage.setText("Enter a valid weight");
                return;
            }
        } catch (NumberFormatException e) {
            tvPreparation.setText("—");
            tvDosage.setText("Invalid weight");
            return;
        }

        List<DrugData> drugs = DrugRepository.getDrugs(this);
        DrugData drug = drugs.get(pos - 1);

        double minRate = drug.doseMin * weight * drug.syringeMlPerUnit;
        double maxRate = drug.doseMax * weight * drug.syringeMlPerUnit;

        // For weight-independent drugs (Vasopressin, NTG)
        if (!drug.weightDependent) {
            minRate = drug.doseMin * drug.syringeMlPerUnit;
            maxRate = drug.doseMax * drug.syringeMlPerUnit;
        }

        String minStr = formatRate(minRate);
        String maxStr = formatRate(maxRate);

        tvPreparation.setText(drug.preparation);
        tvDosage.setText(minStr + " – " + maxStr + " ml/hr");
    }

    private String formatRate(double val) {
        if (val == Math.floor(val)) return String.valueOf((int) val);
        // Round to 2 decimal places, remove trailing zeros
        String s = String.format("%.2f", val);
        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            startActivity(new Intent(this, EditDrugsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupDrugSpinner();
        calculateDosage();
    }
}
