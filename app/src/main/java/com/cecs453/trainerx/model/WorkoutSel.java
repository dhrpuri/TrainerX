package com.cecs453.trainerx.model;

import java.util.HashMap;
import java.util.Map;


public class WorkoutSel {
    private String setNumber;
    private String srepetitions;
    private String sspeed;
    private String scomments;

    private Weight sweight;
    private Time stime;
    private Distance sdistance;

    private String timeOn;
    private String weightOn;
    private String repsOn;
    private String speedOn;
    private String distanceOn;



    public WorkoutSel(String setNumber, String srepetitions, Weight sweight, Time stime, String sspeed, Distance sdistance,String scomments) {
        this.setNumber = setNumber;
        this.srepetitions = srepetitions;
        this.sweight = new Weight(sweight);
        this.stime = new Time(stime);
        this.sspeed = sspeed;
        this.sdistance = new Distance(sdistance);
        this.scomments = scomments;
        this.timeOn =null;
        this.weightOn=null;
        this.repsOn=null;
        this.speedOn =null;
        this.distanceOn=null;
        this.repsOn=null;
    }

    public Map<String,String> returnWorkoutSel (){
        String setString = "-set-"+setNumber;
        Map <String,String> h = new HashMap<>();
        h.put("Comments"+setString,scomments);
        h.put("timeOn"+setString,timeOn);
        h.put("weightOn"+setString,weightOn);
        h.put("speedOn"+setString,speedOn);
        h.put("repsOn"+setString,repsOn);
        h.put("distanceOn"+setString,distanceOn);

        if(distanceOn!=null){
            h.put("distance"+setString,sdistance.getDistance());
            h.put("distanceType"+setString,sdistance.getDistanceType());
        }

        if(speedOn!=null){
        h.put("Speed"+setString,sspeed);
        }
        if(repsOn!=null) {
            h.put("Repetitions"+setString, srepetitions);
        }
        if(weightOn!=null){
             h.put("weight"+setString,sweight.getWeight());
             h.put("weightUnit"+setString,sweight.getWeightUnit());
             h.put("weightType"+setString,sweight.getWeightType());
        }

        if(timeOn!=null){
            h.put("timeH"+setString,stime.getTimeH());
            h.put("timeM"+setString,stime.getTimeM());
            h.put("timeS"+setString,stime.getTimeS());
        }


        return  h;
    }

    public String getDistanceOn() { return distanceOn; }
    public String getRepsOn() { return repsOn; }
    public String getSpeedOn() { return speedOn; }
    public String getTimeOn() { return timeOn; }
    public String getWeightOn() { return weightOn; }

    public void setRepsOn(String repsOn) { this.repsOn = repsOn; }
    public void setSpeedOn(String speedOn) { this.speedOn = speedOn; }
    public void setDistanceOn(String distanceOn) { this.distanceOn = distanceOn; }
    public  String getScomments(){ return scomments; }
    public String getSetNumber() { return setNumber; }
    public void setSetNumber(String setNumber) { this.setNumber = setNumber; }
    public String getSrepetitions() { return srepetitions; }
    public Weight getSweight() {
        return sweight; }
    public Time getStime() { return stime; }
    public String getSspeed() { return sspeed; }
    public Distance getSdistance() { return sdistance; }


    public void setTimeOn(String s){
        this.timeOn =s;

    }
    public void setWeightOn(String s){
        this.weightOn=s;
    }

    public  void setScomments(String str){scomments=str;}
    public void setSdistance(Distance sdistance) {
        this.sdistance = sdistance;
        if(sdistance.equals(new Distance())){
            setDistanceOn(null);
        }else {
            setDistanceOn("true");
        }
    }

    public void setSspeed(String sspeed) {

        this.sspeed = sspeed;
        if(sspeed.equalsIgnoreCase("")){
            setSpeedOn(null);
        }else{
            setSpeedOn("true");
        }
    }

    public void setSrepetitions(String srepetitions) {
        this.srepetitions = srepetitions;
        if(srepetitions.equalsIgnoreCase("")){
            setRepsOn(null);
        }else{
            setRepsOn("true");
        }
    }

    public void setStime(Time stime) {
        this.stime = stime;
        if(stime.equals(new Time())){
            setTimeOn(null);
        }else {
            setTimeOn("true");
        }
    }

    public void setSweight(Weight sweight) {
        setWeightOn("true");
        if(sweight.equals(new Weight())){
            setWeightOn(null);
        }
        else {
            this.sweight = sweight;
        }
    }

}