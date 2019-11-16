package com.cecs453.trainerx;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.MyOptionsPickerView;
import com.cecs453.trainerx.model.Distance;
import com.cecs453.trainerx.model.Time;
import com.cecs453.trainerx.model.Weight;
import com.cecs453.trainerx.model.Workout;
import com.cecs453.trainerx.model.WorkoutSel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


//TODO: when back button is clicked ... set docId3 to null

public class WorkoutTemplateFragment extends Fragment implements View.OnClickListener {
    WorkoutSel w;
    Button btnSaveExercise;
    Button btnRepEdit, btnWeightEdit, btnTimeEdit, btnSpeedEdit, btnDistanceEdit;
    Button btnRepRemove, btnWeightRemove, btnTimeRemove, btnSpeedRemove, btnDistanceRemove;
    TextView repEditText, weightEditText, timeEditText, speedEditText, distanceEditText;
    EditText commentEditText;
    private Button buttonNewExericse;
    View main_view;

    RadioGroup radioGroup;
    Button back, btnAddSet, btnRemoveSet;
    TextView exerciseName;
    MyOptionsPickerView repPicker, weightsPicker, timePicker, speedPicker, distancePicker;
    int SetBtnCount = 0;
    private FirebaseFirestore db;
    private String TAG = "WorkoutTemplateFragment";
    private String docID;
    private String st;
    private  String  exercise;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, WorkoutSel> WorkoutSelHM = new HashMap<>();
    private ArrayList<WorkoutSel> WorkoutSelList = new ArrayList<>();
    private Intent returnIntent;
    private Bundle returnBundle;
 

    public WorkoutTemplateFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        returnIntent=new Intent();
        View view = inflater.inflate(R.layout.fragment_workout_select, container, false);
        main_view = inflater.inflate(R.layout.activity_workout_select, container, false);
        buttonNewExericse = main_view.findViewById(R.id.buttonNewExericse);
        //buttonNewExericse.setOnClickListener(this);
        buttonNewExericse.setVisibility(View.VISIBLE);

        Bundle args = getArguments();
        docID = DocId.getInstance().getId();



        //ToDO: get exercise type as well
        exercise= args.getString("excName");
        TextView exerciseNameTextView = view.findViewById(R.id.exerciseName);
        exerciseNameTextView.setText(exercise);

        initView(view);
        createFirstSet();
        repPickerShow();
        weightPickerShow();
        timePickerShow();
        speedPickerShow();
        distancePickerShow();
//        createNewUpload();

        return view;
    }


    // created when a workout is clicked for the first time:
    //Purpose : get the document id of the workout and init parameters
    private void createNewUpload(){
        Map<String, Object> data = new HashMap<>();

        data.put("name",exercise);
        data.put("type","exerciseType");

        String docId2=DocId.getInstance().getWorkoutId();
        String docId3=DocId.getInstance().getId3();
        if(docId3!=null) {
            db.collection("customers").document(docID).collection("workouts").document(docId2).collection("exercises").add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            DocId.getInstance().setId3(documentReference.getId());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }else{
            populateSets();
        }

    }

     private void populateSets(){
        //TODO: Read from DB and initialize it

    }
    void initView(View view) {
        radioGroup = view.findViewById(R.id.setLayout);

        btnSaveExercise = view.findViewById(R.id.saveExercise);
        btnSaveExercise.setOnClickListener(this);
        btnAddSet = view.findViewById(R.id.btnAddSet);
        btnAddSet.setOnClickListener(this);
        btnRemoveSet = view.findViewById(R.id.btnRemoveSet);
        btnRemoveSet.setOnClickListener(this);

        btnRepEdit = view.findViewById(R.id.btnRepEdit);
        btnWeightEdit = view.findViewById(R.id.btnWeightEdit);
        btnTimeEdit = view.findViewById(R.id.btnTimeEdit);
        btnSpeedEdit = view.findViewById(R.id.btnSpeedEdit);
        btnDistanceEdit = view.findViewById(R.id.btnDistanceEdit);

        btnRepRemove = view.findViewById(R.id.btnRepRemove);
        btnWeightRemove = view.findViewById(R.id.btnWeightRemove);
        btnTimeRemove = view.findViewById(R.id.btnTimeRemove);
        btnSpeedRemove = view.findViewById(R.id.btnSpeedRemove);
        btnDistanceRemove = view.findViewById(R.id.btnDistanceRemove);

        repEditText = view.findViewById(R.id.repEditText);
        weightEditText = view.findViewById(R.id.weightEditText);
        timeEditText = view.findViewById(R.id.timeEditText);
        speedEditText = view.findViewById(R.id.speedEditText);
        distanceEditText = view.findViewById(R.id.distanceEditText);

        commentEditText = view.findViewById(R.id.commentEditText);
        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) { }

            @Override
            public void afterTextChanged(Editable s)
            {
                WorkoutSel wSel = WorkoutSelHM.get(radioGroup.getCheckedRadioButtonId());
                wSel.setScomments(commentEditText.getText().toString());
            }
        });

        btnRepEdit.setOnClickListener(this);
        btnWeightEdit.setOnClickListener(this);
        btnTimeEdit.setOnClickListener(this);
        btnSpeedEdit.setOnClickListener(this);
        btnDistanceEdit.setOnClickListener(this);

        btnRepRemove.setOnClickListener(this);
        btnWeightRemove.setOnClickListener(this);
        btnTimeRemove.setOnClickListener(this);
        btnSpeedRemove.setOnClickListener(this);
        btnDistanceRemove.setOnClickListener(this);
    }

    private void setCurrentWorkoutParams() {
        WorkoutSel wSel = WorkoutSelHM.get(radioGroup.getCheckedRadioButtonId());
        if (wSel != null) {
            repEditText.setText(wSel.getSrepetitions());
            weightEditText.setText(wSel.getSweight().print());
            timeEditText.setText(wSel.getStime().print());
            speedEditText.setText(wSel.getSspeed());
            distanceEditText.setText(wSel.getSdistance().print());
            commentEditText.setText(wSel.getScomments());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.saveExercise:
                writeData();

                Toast.makeText(getActivity(), "Excercise saved.", Toast.LENGTH_SHORT).show();
                //add to the left fragment.

                break;
            case R.id.btnAddSet:
                createNewSet();
                break;
            case R.id.btnRemoveSet:
                removeSet();
                break;
            case R.id.btnRepEdit:
                repPicker.show();
                break;
            case R.id.btnWeightEdit:
                weightsPicker.show();
                break;
            case R.id.btnTimeEdit:
                timePicker.show();
                break;
            case R.id.btnSpeedEdit:
                speedPicker.show();
                break;
            case R.id.btnDistanceEdit:
                distancePicker.show();
                break;
            case R.id.btnRepRemove:
                repEditText.setText("");
                WorkoutSelHM.get(radioGroup.getCheckedRadioButtonId()).setSrepetitions("");
                break;
            case R.id.btnWeightRemove:
                weightEditText.setText("");
                WorkoutSelHM.get(radioGroup.getCheckedRadioButtonId()).setSweight(new Weight());
                break;
            case R.id.btnTimeRemove:
                timeEditText.setText("");
                WorkoutSelHM.get(radioGroup.getCheckedRadioButtonId()).setStime(new Time());
                break;
            case R.id.btnSpeedRemove:
                speedEditText.setText("");
                WorkoutSelHM.get(radioGroup.getCheckedRadioButtonId()).setSspeed("");
                break;
            case R.id.btnDistanceRemove:
                distanceEditText.setText("");
                WorkoutSelHM.get(radioGroup.getCheckedRadioButtonId()).setSdistance(new Distance());
                break;
        }
    }

    private void writeData() {
        returnBundle=new Bundle();
        Map<String, Object> data = new HashMap<>();
        Map<String, String> setDeets = new HashMap<>();
        for (Integer key : WorkoutSelHM.keySet()) {
            Map<String, String> hm = WorkoutSelHM.get(key).returnWorkoutSel(); // get workout details from set i
            for (String s : hm.keySet()) {
               setDeets.put(s, hm.get(s));
            }
            Log.e("WrktTmplteFrg", key.toString() + "  " + WorkoutSelHM.get(key).toString() + "DOCID:  " + DocId.getInstance().getId());
        }
        data.put("data", setDeets);
        data.put("name",exercise);
        data.put("type","exerciseType");


        String docId2=DocId.getInstance().getWorkoutId();
        String docId3=DocId.getInstance().getId3();
        returnBundle.putString("customersId",docID);
        returnBundle.putString("workoutsId",docId2);
        returnBundle.putString("ecerciseId",docId3);
        if(docId3!=null){
            db.collection("customers").document(docID).collection("workouts").document(docId2).collection("exercises").document(docId3).update(data);
        }else {
            Log.d("aaaaa", "dddddd");
             db.collection("customers").document(docID).collection("workouts").document(docId2).collection("exercises").
                     add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

                         @Override
                         public void onComplete(@NonNull Task<DocumentReference> task) {
                             if (task.isSuccessful()) {
//                                 DocumentReference document = task.getResult();
//                                 DocId.getInstance().setId3(document.getId());
//                                 returnBundle.putString("ecerciseId",document.getId());
//                                 returnIntent.putExtra("bundle",returnBundle);
//                                 WorkoutSelectActivity wrkSel=(WorkoutSelectActivity) getActivity();
//                                 wrkSel.onReturnFromFragment(returnIntent);


                                 Intent intent = new Intent(getActivity(), WorkoutSelectActivity.class);
                                 Bundle args = new Bundle();
                                 args.putString("DocId", ((WorkoutSelectActivity)getActivity()).docID);
                                 args.putSerializable("calendarClickedDay", ((WorkoutSelectActivity)getActivity()).calendarClickedDay);
                                 args.putSerializable("workoutOnClickedDay",((WorkoutSelectActivity)getActivity()).workoutOnClickedDay);
                                 intent.putExtras(args);
                                 startActivity(intent);

                             }
                        }
                     });
        }


        //getActivity().finish();

    }



    public void createNewSet() {
        RadioButton newSet = new RadioButton(getActivity());
        SetBtnCount = SetBtnCount + 1;
        newSet.setTextColor(ContextCompat.getColor(getActivity(), R.color.material_text_color_white_text));
        newSet.setId(SetBtnCount);
        String st = "SET" + SetBtnCount;
        newSet.setText(st);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMarginStart(Utils.dpToPx(getContext(), 16f));
        newSet.setLayoutParams(layoutParams);
        radioGroup.addView(newSet);
        w = new WorkoutSel(Integer.valueOf(SetBtnCount).toString(), " " + repEditText.getText(), new Weight(), new Time(), "" + speedEditText.getText(), new Distance(),""+commentEditText.getText());
        WorkoutSelHM.put(newSet.getId(), w);
        WorkoutSelList.add(w);
        writeData();
    }

    public void createFirstSet() {
        RadioButton newSet = new RadioButton(getActivity());
        newSet.setTextColor(ContextCompat.getColor(getActivity(), R.color.material_text_color_white_text));
        SetBtnCount = SetBtnCount + 1;
        newSet.setId(SetBtnCount);
        String st = "SET" + SetBtnCount;
        newSet.setText(st);
        newSet.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        radioGroup.addView(newSet);
        newSet.setChecked(true);
        w = new WorkoutSel(Integer.valueOf(SetBtnCount).toString(), "" + repEditText.getText(), new Weight(),  new Time(), "" + speedEditText.getText(),  new Distance(),""+commentEditText.getText());
        WorkoutSelHM.put(newSet.getId(), w);
        WorkoutSelList.add(w);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setCurrentWorkoutParams();
                Log.d("Current radio selected", "" + checkedId);
            }
        });
    }

    public void removeSet() {
        if (SetBtnCount > 1) {
            boolean isChecked = (SetBtnCount == radioGroup.getCheckedRadioButtonId());
            WorkoutSelHM.remove(radioGroup.getCheckedRadioButtonId());
            radioGroup.removeViewAt(radioGroup.getChildCount() - 1);
            SetBtnCount--;
            if (isChecked) {
                RadioButton r1 = (RadioButton) radioGroup.getChildAt(SetBtnCount - 1);
                r1.setChecked(true);
                writeData();
            }
        } else {
            Toast.makeText(getActivity(), "Cannot remove first set!", Toast.LENGTH_SHORT).show();
        }
    }

    public void repPickerShow() {
        repPicker = new MyOptionsPickerView(getActivity());
        final ArrayList<Integer> items = new ArrayList<>();
        for (int i = 1; i <= 200; i++) {
            items.add(i);
        }
        repPicker.setPicker(items);
        repPicker.setCyclic(false);
        repPicker.setTitle("REPETITIONS");
        repPicker.setSelectOptions(0);
        repPicker.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                st = "" + items.get(options1);
                WorkoutSelHM.get(radioGroup.getCheckedRadioButtonId()).setSrepetitions(st);
                Toast.makeText(getActivity(), st, Toast.LENGTH_SHORT).show();
                repEditText.setText(st);
            }
        });
    }

    public void weightPickerShow() {
        weightsPicker = new MyOptionsPickerView(getActivity());
        final ArrayList<String> threeItemsOptions1 = new ArrayList<>();
        threeItemsOptions1.add("Dumbbell");
        threeItemsOptions1.add("Barbell");
        threeItemsOptions1.add("Kettlebell");
        threeItemsOptions1.add("Medicine Ball");
        threeItemsOptions1.add("Plate");
        threeItemsOptions1.add("EZ Bar");
        threeItemsOptions1.add("Bodyweight");
        threeItemsOptions1.add("None");

        final ArrayList<Integer> threeItemsOptions2 = new ArrayList<>();
        for (int i = 1; i <= 200; i++) {
            threeItemsOptions2.add(i);
        }

        final ArrayList<String> threeItemsOptions3 = new ArrayList<>();
        threeItemsOptions3.add("kg");
        threeItemsOptions3.add("lb");
        threeItemsOptions3.add("stones");


        //value , unit , type

        weightsPicker.setPicker(threeItemsOptions1, threeItemsOptions2, threeItemsOptions3, false);
        weightsPicker.setTitle("WEIGHTS");
        weightsPicker.setCyclic(false, false, false);
        weightsPicker.setSelectOptions(0, 0, 0);
        weightsPicker.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String st = "" + threeItemsOptions1.get(options1) + " " + threeItemsOptions2.get(option2) + " " + threeItemsOptions3.get(options3);
                weightEditText.setText(st);
                WorkoutSelHM.get(radioGroup.getCheckedRadioButtonId()).setSweight(new Weight(threeItemsOptions2.get(option2).toString(),threeItemsOptions3.get(options3),threeItemsOptions1.get(options1)));
                Toast.makeText(getActivity(), "" + threeItemsOptions1.get(options1) + " " + threeItemsOptions2.get(option2) + " " + threeItemsOptions3.get(options3), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void timePickerShow() {
        timePicker = new MyOptionsPickerView(getActivity());
        final ArrayList<Integer> threeItemsOptions1 = new ArrayList<>();
        for (int i = 0; i <= 12; i++) {
            threeItemsOptions1.add(i);
        }

        final ArrayList<Integer> threeItemsOptions2 = new ArrayList<>();
        for (int i = 0; i <= 59; i++) {
            threeItemsOptions2.add(i);
        }

        final ArrayList<Integer> threeItemsOptions3 = new ArrayList<>();
        for (int i = 0; i <= 59; i++) {
            threeItemsOptions3.add(i);
        }

        timePicker.setPicker(threeItemsOptions1, threeItemsOptions2, threeItemsOptions3, false);
        timePicker.setTitle("HH\t\t\t\t\t::\t\t\t\t\tMM\t\t\t\t\t::\t\t\t\t\tSS");
        timePicker.setCyclic(false, false, false);
        timePicker.setSelectOptions(0, 0, 0);
        timePicker.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                st = "" + threeItemsOptions1.get(options1) + ":" + threeItemsOptions2.get(option2) + ":" + threeItemsOptions3.get(options3);
                timeEditText.setText(st);
                WorkoutSelHM.get(radioGroup.getCheckedRadioButtonId()).setStime(new Time(threeItemsOptions1.get(options1).toString(),threeItemsOptions2.get(option2).toString(),threeItemsOptions3.get(options3).toString()));
                Toast.makeText(getActivity(), "" + threeItemsOptions1.get(options1) + ":" + threeItemsOptions2.get(option2) + ":" + threeItemsOptions3.get(options3), Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void speedPickerShow() {
        speedPicker = new MyOptionsPickerView(getActivity());
        final ArrayList<Integer> twoItemsOptions1 = new ArrayList<>();
        for (int i = 1; i <= 200; i++) {
            twoItemsOptions1.add(i);
        }
        final ArrayList<String> twoItemsOptions2 = new ArrayList<>();
        twoItemsOptions2.add("mph");
        twoItemsOptions2.add("kph");

        speedPicker.setPicker(twoItemsOptions1, twoItemsOptions2, false);
        speedPicker.setTitle("SPEED");
        speedPicker.setCyclic(false, false, false);
        speedPicker.setSelectOptions(0, 0);
        speedPicker.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                st = "" + twoItemsOptions1.get(options1) + " " + twoItemsOptions2.get(option2);
                speedEditText.setText(st);
                WorkoutSelHM.get(radioGroup.getCheckedRadioButtonId()).setSspeed(st);
                Toast.makeText(getActivity(), "" + twoItemsOptions1.get(options1) + " " + twoItemsOptions2.get(option2), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void distancePickerShow() {
        distancePicker = new MyOptionsPickerView(getActivity());
        final ArrayList<Integer> twoItemsOptions1 = new ArrayList<>();
        for (int i = 1; i <= 200; i++) {
            twoItemsOptions1.add(i);
        }
        final ArrayList<String> twoItemsOptions2 = new ArrayList<>();
        twoItemsOptions2.add("Miles");
        twoItemsOptions2.add("Feet");
        twoItemsOptions2.add("Meters");
        twoItemsOptions2.add("Laps");

        distancePicker.setPicker(twoItemsOptions1, twoItemsOptions2, false);
        distancePicker.setTitle("DISTANCE");
        distancePicker.setCyclic(false, false, false);
        distancePicker.setSelectOptions(0, 0);
        distancePicker.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                st = "" + twoItemsOptions1.get(options1) + " " + twoItemsOptions2.get(option2);
                distanceEditText.setText(st);
                WorkoutSelHM.get(radioGroup.getCheckedRadioButtonId()).setSdistance(new Distance(twoItemsOptions1.get(options1).toString(),twoItemsOptions2.get(option2)));
                Toast.makeText(getActivity(), "" + twoItemsOptions1.get(options1) + " " + twoItemsOptions2.get(option2), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
