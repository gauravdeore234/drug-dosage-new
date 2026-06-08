package com.drugdosage;

public class DrugData {
    public String name;
    public String preparation;
    public double doseMin;       // min dose in base unit per kg per hr (or per min for fixed)
    public double doseMax;       // max dose in base unit per kg per hr
    public double syringeMlPerUnit; // ml per unit (e.g. ml/mg or ml/µg)
    public boolean weightDependent; // false for Vasopressin, NTG

    public DrugData(String name, String preparation, double doseMin, double doseMax,
                    double syringeMlPerUnit, boolean weightDependent) {
        this.name = name;
        this.preparation = preparation;
        this.doseMin = doseMin;
        this.doseMax = doseMax;
        this.syringeMlPerUnit = syringeMlPerUnit;
        this.weightDependent = weightDependent;
    }
}
