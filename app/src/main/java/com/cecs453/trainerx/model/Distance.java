package com.cecs453.trainerx.model;

public class Distance {

    private String distance;
    private String distanceType;

    public  Distance(Distance d){
        this.distance = d.distance;
        this.distanceType = d.distanceType;
    }

    public Distance(String value,String unit){
        distance = value;
        distanceType = unit;
    }
    public  Distance(){
        this.distance ="";
        this.distanceType="";
    }

    public String getDistance() {
        return distance;
    }

    public String getDistanceType() {
        return distanceType;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDistanceType(String distanceType) {
        this.distanceType = distanceType;
    }

    public void resetDistance(){
        distanceType="";
        distance="";
    }

    public boolean equals(Distance d){
        return this.distance.equalsIgnoreCase(d.distance) && this.distanceType.equalsIgnoreCase(d.distanceType);
    }

    public String print(){
        return distance+" "+distanceType;
    }

}
