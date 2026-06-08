package com.drugdosage;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class EditDrugsActivity extends AppCompatActivity {

    private ListView listView;
    private List<DrugData> drugs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drugs);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Drug Table");

        listView = findViewById(R.id.listDrugs);
        drugs = new ArrayList<>(DrugRepository.getDrugs(this));

        refreshList();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            showEditDialog(position);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteDialog(position);
            return true;
        });

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> showEditDialog(-1));
    }

    private void refreshList() {
        List<String> names = new ArrayList<>();
        for (DrugData d : drugs) names.add(d.name);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);
    }

    private void showEditDialog(int position) {
        DrugData existing = position >= 0 ? drugs.get(position) : null;
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_drug, null);

        android.widget.EditText etName = dialogView.findViewById(R.id.etName);
        android.widget.EditText etPrep = dialogView.findViewById(R.id.etPrep);
        android.widget.EditText etDoseMin = dialogView.findViewById(R.id.etDoseMin);
        android.widget.EditText etDoseMax = dialogView.findViewById(R.id.etDoseMax);
        android.widget.EditText etConcentration = dialogView.findViewById(R.id.etConcentration);
        android.widget.CheckBox cbWeightDep = dialogView.findViewById(R.id.cbWeightDependent);

        if (existing != null) {
            etName.setText(existing.name);
            etPrep.setText(existing.preparation);
            etDoseMin.setText(String.valueOf(existing.doseMin));
            etDoseMax.setText(String.valueOf(existing.doseMax));
            etConcentration.setText(String.format("%.4f", 1.0 / existing.syringeMlPerUnit));
            cbWeightDep.setChecked(existing.weightDependent);
        } else {
            cbWeightDep.setChecked(true);
        }

        new AlertDialog.Builder(this)
                .setTitle(existing != null ? "Edit Drug" : "Add Drug")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    try {
                        String name = etName.getText().toString().trim();
                        String prep = etPrep.getText().toString().trim();
                        double doseMin = Double.parseDouble(etDoseMin.getText().toString().trim());
                        double doseMax = Double.parseDouble(etDoseMax.getText().toString().trim());
                        double conc = Double.parseDouble(etConcentration.getText().toString().trim());
                        boolean weightDep = cbWeightDep.isChecked();

                        if (name.isEmpty() || prep.isEmpty() || conc == 0) {
                            android.widget.Toast.makeText(this, "Please fill all fields correctly",
                                    android.widget.Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DrugData drug = new DrugData(name, prep, doseMin, doseMax,
                                1.0 / conc, weightDep);
                        if (existing != null) {
                            drugs.set(position, drug);
                        } else {
                            drugs.add(drug);
                        }
                        DrugRepository.saveDrugs(this, drugs);
                        refreshList();
                    } catch (NumberFormatException e) {
                        android.widget.Toast.makeText(this, "Invalid number entered",
                                android.widget.Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Drug")
                .setMessage("Delete \"" + drugs.get(position).name + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    drugs.remove(position);
                    DrugRepository.saveDrugs(this, drugs);
                    refreshList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
