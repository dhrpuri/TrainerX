package com.cecs453.trainerx;

public class DocId {
    private static DocId d = new DocId();
    private String CustomerId;
    private String workoutId=null;
    private String exerciseID=null;
    private String trainer;
    private String notes;
     public String getNotes(){return notes;}
     public  void setNotes(String txt) {
         if (txt == null) {
             notes = "";
         } else {
             notes = txt;
         }
     }

    public String getId() {
        return CustomerId;
    }
    public  void setId(String s){
        CustomerId=s;
    }
    public  static  DocId getInstance(){
        return d;
    }

    public String getTrainer(){return trainer;}
    public void  setTrainer(String TrainerID){trainer=TrainerID;}

    public String getWorkoutId() {
        return workoutId;
    }
    public String getId3() {
        return exerciseID;
    }

    public  void setWorkoutId(String s){
        workoutId=s;
    }
    public  void setId3(String s){
        exerciseID=s;
    }

}
