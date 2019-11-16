package com.cecs453.trainerx.model;

public class Time {
    private String timeH;
    private String timeM;
    private String timeS;

    public String getTimeH(){ return timeH; }
    public String getTimeM(){return  timeM;}
    public String getTimeS(){return  timeS;}

    public  void  setTimeH(String s){timeH=s;}
    public  void  setTimeM(String s){timeM=s;}
    public  void  setTimeS(String s){timeS=s;}

    public boolean equals(Time t){
        return this.timeH.equals(t.timeH) && this.timeM.equals(t.timeM) && this.timeS.equals(t.timeS);
    }
    //Copy constructor
    public Time(Time t){
        this.timeH = t.timeH;
        this.timeM = t.timeM;
        this.timeS = t.timeS;
    }

    public Time(String h,String m,String s){
        timeS=s;
        timeM=m;
        timeH=h;
    }
    public Time(){
        this.timeH = "";
        this.timeM = "";
        this.timeS = "";
    }
    public  void resetTime(){
        timeS="";
        timeM="";
        timeH="";
    }

    public String print(){
        return timeH+" "+timeM+" "+timeS;
    }

}
