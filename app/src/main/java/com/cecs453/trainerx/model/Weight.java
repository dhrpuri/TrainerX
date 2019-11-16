package com.cecs453.trainerx.model;

public class Weight {
    private String weight;
    private String weightType;
    private String weightUnit;

   public Weight(Weight w){
        this.weight = w.weight;
        this.weightType = w.weightType;
        this.weightUnit= w.weightUnit;
    }
    public  Weight(){
        this.weight = "";
        this.weightType = "";
        this.weightUnit="";
    }

    public  Weight(String value, String unit,String type){
       weight = value;
       weightType = type;
       weightUnit = unit;

    }
    public String getWeight() {
        return weight;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public String getWeightType() {
        return weightType;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setWeightType(String weightType) {
        this.weightType = weightType;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public void resetWeight(){
        weightUnit="";
        weight="";
        weightType="";
    }

    public boolean equals(Weight w){
        return this.weight.equals(w.weight) && this.weightType.equals(w.weightType) && this.weightUnit.equals(w.weightUnit);
    }

    public String print(){
       return weight+" "+weightUnit+" "+weightType;
    }
}
