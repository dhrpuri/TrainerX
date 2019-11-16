package com.cecs453.trainerx;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cecs453.trainerx.adapters.ViewClientsAdapter;
import com.cecs453.trainerx.model.Client;
import com.cecs453.trainerx.model.Workout;
import com.cecs453.trainerx.model.WorkoutExercise;
import com.cecs453.trainerx.ui.SimpleItem;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.silencedut.taskscheduler.TaskScheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.SelectableAdapter;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

public class ClientViewActivity extends AppCompatActivity implements FlexibleAdapter.OnItemClickListener {
    public static final String TAG = "ClientViewActivity";
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;
    private static List<AbstractFlexibleItem> data;
    CompactCalendarView compactCalendarView;
    private Button createNewWorkout;
    private Button notes;
    private Button addClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private TextView calendarYear;
    private TextView summary;
    private Button showWorkout;
    private LinearLayout summaryGrid;
    private List<Client> clientList = new ArrayList<>();
    private ViewClientsAdapter adapter;
    private int currentActivatedPosition = -1;
    private String fname, lname, Docid;
    private ImageView clientImageView;
    private TextView clientNameTextView;
    private LinearLayout linearLayoutClientView ,showNotes;
    private boolean snapshotListenerAdded = false;
    private List<WorkoutExercise> exerciseslist = new ArrayList<>();
    private List<Workout> workoutList = new ArrayList<>();
    private List<String>  workoutdates = new ArrayList<>();
    private Client client;

    @Override
    public boolean showAssist(Bundle args) {
        return super.showAssist(args);
    }

    private Workout workoutOnClickedDay;
    private String calendarClickedDay;
    private EditText notesTextView;
    private Button saveNotes, cancelNotes;
    private Long calendarClickedDAte;

    @Override
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View Client");
        toolbar.setTitleTextColor(Color.WHITE);

        clientImageView = findViewById(R.id.clientImageView);
        clientNameTextView = findViewById(R.id.clientNameTextView);
        linearLayoutClientView = findViewById(R.id.linearLayoutClientView);
        summary = findViewById(R.id.summary);
        showWorkout = findViewById(R.id.showWorkout);
        summaryGrid = findViewById(R.id.summaryGrid);
        notesTextView = findViewById(R.id.notesTextView);
        saveNotes = findViewById(R.id.saveNotes);
        cancelNotes = findViewById(R.id.cancelNotes);
        showNotes = findViewById(R.id.showNotes);

        createNewWorkout = findViewById(R.id.newWorkout);
        notes = findViewById(R.id.notesButton);
        addClient = findViewById(R.id.buttonAddClient);
        addClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ClientViewActivity.this, AddClientActivity.class),1);
            }
        });

        compactCalendarView = findViewById(R.id.compactcalendar_view);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            readData();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(ClientViewActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addAdapter() {
        adapter = new ViewClientsAdapter(data, this, clientList);
        adapter.setMode(SelectableAdapter.Mode.SINGLE);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new FlexibleItemDecoration(this)
                .withDivider(R.drawable.divider, R.layout.list_clients)
                .withDrawOver(true));

        FastScroller fastScroller = findViewById(R.id.fast_scroller);
        fastScroller.setAutoHideEnabled(true);        //true is the default value!
        fastScroller.setAutoHideDelayInMillis(1000L); //1000ms is the default value!
        fastScroller.setMinimumScrollThreshold(70); //0 pixel is the default value! When > 0 it mimics the fling gesture
        // The color (accentColor) is automatically fetched by the FastScroller constructor, but you can change it at runtime
        // fastScroller.setBubbleAndHandleColor(Color.RED);
        adapter.setFastScroller(fastScroller);

    }

    //TODO run this on background thread
    private void readData() {
        data = new ArrayList<>();

        com.silencedut.taskscheduler.Task task = new com.silencedut.taskscheduler.Task<String>() {
            @Override
            public String doInBackground() {
                final String[] result = new String[1];
                db.collection("customers")
                        .orderBy("firstName")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        Docid = document.getId();
                                        fname = (String) document.get("firstName");
                                        lname = (String) document.get("lastName");
                                        String imageurl = (String) document.get("imageUrl");

                                        if (TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname)) {
                                            //NOOP
                                        } else {
                                            data.add(new SimpleItem(fname, lname, (String) document.get("imageUrl"), Docid));
                                            clientList.add(new Client(Docid, fname, lname, (String) document.get("imageUrl")));
                                        }
                                    }
                                    addAdapter();
                                    //TODO handle this better
                                    /*if (!snapshotListenerAdded) {
                                        addSnapshotListener();
                                        snapshotListenerAdded = true;
                                    }*/
                                    result[0] = "success";
                                } else {
                                    result[0] = "failure";
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                    Toast.makeText(ClientViewActivity.this, "Failed to get clients. Relaunch application.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                Log.d(TAG, Arrays.toString(data.toArray()));
                return result[0];
            }

            @Override
            public void onSuccess(String result) {
                if (result.equals("success")) {
                    Log.d(TAG, "Async OnSuccess: " + result);
                } else {
                    Log.d(TAG, "Async OnSuccess: " + result);
                }
            }

            @Override
            public void onFail(Throwable throwable) {
                super.onFail(throwable);
                Log.d(TAG, "Fail:" + throwable.getMessage());
            }

            @Override
            public void onCancel() {
                super.onCancel();
                // callback when the task is canceled
            }

        };
        TaskScheduler.execute(task);
    }

    void addSnapshotListener() {
        db.collection("customers").orderBy("firstName").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                }
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                    fname = (String) document.get("firstName");
                    lname = (String) document.get("lastName");
                    Docid = (String) document.getId();

                    if (TextUtils.isEmpty(fname) || TextUtils.isEmpty(lname)) {
                        //NOOP
                    } else {
                        data.add(new SimpleItem(fname, lname, (String) document.get("imageUrl"), (String) document.getId()));
                        clientList.add(new Client(Docid, fname, lname, (String) document.get("imageUrl")));
                    }
                }
                if (!queryDocumentSnapshots.isEmpty())
                    adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onItemClick(View view, int position) {
        if (position != currentActivatedPosition) setActivatedPosition(position);

        //remove previousEvents from calendar.
        compactCalendarView.removeAllEvents();

        showNotes.setVisibility(View.INVISIBLE);
        summaryGrid.setVisibility(View.INVISIBLE);

        //clear workoutList
        workoutList.clear();

        updateView(position);

        return true;
    }

    private Long getRelevantDate() {
        Log.d("RelevantDate: ", String.valueOf(calendarClickedDAte));
       if(calendarClickedDAte == null ) {
           long date = System.currentTimeMillis();
           return date;
       }
       else {
           return calendarClickedDAte;
       }
    }

    private void setCreateNewWorkoutListner(){
        createNewWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ClientViewActivity.this, WorkoutSelectActivity.class);
                Bundle args = new Bundle();
                args.putString("DocId", Docid);
                i.putExtras(args);
                DocId.getInstance().setId(Docid);
                Map<String, Object> data = new HashMap<>();
                data.put("exercises",new HashMap<String,Object>());
                data.put("customer",Docid);

                //get right date, i.e the previous date or current date.
                Long date = getRelevantDate();

                //TODO: get correct date fromat from the system.
                data.put("date","5.54746408998629E8");
                data.put("trainer",DocId.getInstance().getTrainer());

                db.collection("customers").document(Docid).collection("workouts").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        DocId.getInstance().setWorkoutId(documentReference.getId());
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });


                startActivity(i);

            }
        });

    }


    private void getExercisesFromDB(final Workout workout){
        db.collection("customers")
                .document(DocId.getInstance().getId())
                .collection("workouts")
                .document(DocId.getInstance().getWorkoutId())
                .collection("exercises")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                exerciseslist=new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String exerciseId = document.getId();
                        WorkoutExercise workoutExercise = new WorkoutExercise();

                        workoutExercise.setID(exerciseId);
                        try {
                            workoutExercise.setName(document.getString("name"));
                        }catch(Exception e){
                            workoutExercise.setName("");
                        }

                        try {
                            workoutExercise.setOrder((long) document.get("order"));
                        }catch (Exception e){
                            workoutExercise.setOrder(-999);
                        }
                        try {
                            workoutExercise.setType(document.getString("type"));
                        }catch (Exception e){
                            workoutExercise.setType("");
                        }
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        exerciseslist.add(workoutExercise);
                        Log.d("Inside: ", exerciseslist.size()+"");
                    }

                }
                workout.setExerciseList(exerciseslist);
                Log.d("ExerciseList:", exerciseslist.size()+"");
                workoutList.add(workout);
            }
        });

    }

   private  void setWorkoutLog(){

       Task<QuerySnapshot> messageRef = db
               .collection("customers")
               .document(DocId.getInstance().getId())
               .collection("workouts")
               .get()
               .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()) {
//                                                    List<DocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
                           for (QueryDocumentSnapshot document : task.getResult()) {

                               String workoutId = document.getId();
                               DocId.getInstance().setWorkoutId(workoutId);
                               final Workout workout = new Workout();

                               workout.setCustomerId(document.getString("customer"));
                               workout.setTrainer(document.getString("trainer"));
                               workout.setID(workoutId);

                               if (document.get("date") != null) {
                                   //convertDateToMilliSec
                                   Log.d("Date from Db: ", document.get("date").toString());
                                   try{
                                       Long dateinMSec = convertDateToMsec((double) document.get("date"));
                                       Date currentDate = new Date(dateinMSec);
                                       String[] d1 = currentDate.toString().split(" ");
                                       String dateFromDb = d1[0] + " " + d1[1] +" "+ d1[2] +" "+ d1[5];
                                       workoutdates.add(dateFromDb);
                                       workout.setDate(dateFromDb);

                                       Log.d("currentDate: ", dateFromDb + "currentDate: "+String.valueOf(currentDate));
                                       addToCalendar(dateinMSec);
                                   }
                                   catch (Exception e){
                                       Toast.makeText(ClientViewActivity.this, String.format("No date of workout found at DB."), Toast.LENGTH_SHORT).show();
                                   }

                               } else {
                                   //convertDateToMilliSec
                                   Long dateinMSec = convertDateToMsec(5.54747367437372E8);
                                   workout.setDate("Tue Aug 25 2018");
                                   //addtoCalendar
                                   addToCalendar(dateinMSec);
                               }

                               Log.d(TAG, document.getId() + " => " + document.getData());
                               getExercisesFromDB(workout);
                           }
                       }
                   }
               });

   }
   private void getLogFromDB(){

        db.collection("customers")
                .whereEqualTo("firstName", client.getfName())
                .whereEqualTo("lastName", client.getlName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.d("WORKOUTLOG", document.getId() + " => " + document.getData());

                                //id of the document
                                DocId.getInstance().setId(document.getId());

                                setWorkoutLog();

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getClientNotes(){
        db.collection("workouts").document(Docid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String notesText="";
                    try {
                        notesText=task.getResult().get("notes").toString();


                    }catch (Exception e){
                       // Toast.makeText(ClientViewActivity.this, String.format("No Notes for the client yet."), Toast.LENGTH_SHORT).show();
                    }
                    DocId.getInstance().setNotes(notesText);
                }
            }
        });
    }

    private void displayClientNotes(){
        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String notesText = DocId.getInstance().getNotes();

                if (TextUtils.isEmpty(notesText)){
                    Toast.makeText(ClientViewActivity.this, String.format("No Notes for the client yet."), Toast.LENGTH_SHORT).show();
                    showNotes.setVisibility(View.VISIBLE);
                }
                else {
                    notesTextView.setText(notesText);
                }



            }
        });

        saveNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotes.setVisibility(View.INVISIBLE);
            }
        });

        cancelNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotes.setVisibility(View.INVISIBLE);
            }
        });


    }
    private void addShowWorkoutListener(){
        showWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ClientViewActivity.this, WorkoutSelectActivity.class);
                Bundle args = new Bundle();
                args.putString("DocId", Docid);
                args.putSerializable("clickedDate", calendarClickedDay);
                args.putSerializable("workouts",workoutOnClickedDay);
                i.putExtras(args);
                DocId.getInstance().setId(Docid);
                startActivity(i);

            }
        });
    }

    private void saveClickedDate(Date date) {
        Long epoch = date.getTime();
        calendarClickedDAte = epoch;

    }

    private void updateView(int position) {
        calendarYear = findViewById(R.id.calendarYearMonth);
        client = clientList.get(position);
        Glide.with(this).load(client.getImageURL()).into(clientImageView);
        String name = client.getfName() + " " + client.getlName();
        Docid = client.getDocId();
        clientNameTextView.setText(name);
        linearLayoutClientView.setVisibility(View.VISIBLE);
        setCreateNewWorkoutListner();

        getClientNotes();
        displayClientNotes();   //to display client notes on click
        addShowWorkoutListener(); // to add on-click listener to show workout button
        getLogFromDB();        //get workout logs from db for that customer.

        //set calendar title
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat m = new SimpleDateFormat("MMM");
        SimpleDateFormat y = new SimpleDateFormat("yyyy");
        String s1 = m.format(new Date()) + " " + y.format(new Date());
        calendarYear.setText(s1);
        Log.d("Cal: ",s1 );

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                // save date format so that it can be used while adding the workout.
                saveClickedDate(dateClicked);

                summaryGrid.setVisibility(View.VISIBLE);

                String[] s1 = dateClicked.toString().split(" ");
                String day = s1[1];
                String year = s1[5];
                String s2 = day + " " + year;
                calendarYear.setText(s2);

                Log.d(TAG, "Day was clicked: " + dateClicked);

                StringBuilder sb = new StringBuilder();
                //update summary box.

               String d2 = s1[0] + " " + s1[1] + " " + s1[2] + " " + s1[5];

                if (workoutdates.contains(d2)) {


                    for (Workout workout : workoutList) {
                        Log.d("Workout: ", workout.getDate() + "d2: " + d2 + "WorkoutExercises: " + workout.getExerciseList().size());
                        if (workout.getDate()!=null && workout.getDate().equals(d2)) {
                            workoutOnClickedDay = workout;
                            calendarClickedDay = d2;
                            sb.append("Trainer: ");
                            sb.append(workout.getTrainer());
                            sb.append("\n");
                        }
                    }

                }
                else {
                   sb.setLength(0);
                    Toast.makeText(ClientViewActivity.this, "No Workout on this day!",
                            Toast.LENGTH_SHORT).show();
                    summaryGrid.setVisibility(View.INVISIBLE);
                }

                Log.d("SB: " ,sb.toString());
                summary.setText(sb.toString());

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                calendarYear = findViewById(R.id.calendarYearMonth);
                String[] s1 = firstDayOfNewMonth.toString().split(" ");
                String day = s1[1];
                String year = s1[5];
                String s2 = day + " " + year;
                calendarYear.setText(s2);
                Log.d(TAG, "SCROLLED! ");
            }
        });
    }

    private void setActivatedPosition(int position) {
        currentActivatedPosition = position;
        adapter.toggleSelection(position); //Important!
    }

    private void addToCalendar(long date) {
        //add date to list.

        Event ev = new Event(Color.BLUE, date);
        compactCalendarView.addEvent(ev);

    }

    private Long convertDateToMsec(double date) {
        Log.d("Double : ", String.valueOf(date));
        //for some reason we have to add 31 years and/or 1 day to get the current date
        long myLong = ((long) (date * 1000));
        myLong += 978264706000L + 86400000L;

        return myLong;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // handle arrow click here
        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        if (id == R.id.action_stopwatch) {
            if (Settings.canDrawOverlays(this)) {
                // continue here - permission was granted

                Log.d(TAG, "permission floating");
                startService(new Intent(ClientViewActivity.this, FloatingViewService.class));
            } else {

                AlertDialog alertDialog = new AlertDialog.Builder(ClientViewActivity.this).create();
                alertDialog.setTitle("Permission to draw over other apps required!");
                alertDialog.setMessage("*To show the stopwatch, System Overlay permission has to be granted.\n\n" +
                        "*This permission has to be granted only once.\n\n" +
                        "*Please tap on the icon to see the stopwatch after granting the permission.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "GRANT",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DENY",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * created: sourabh
    * This will update the client list dynamically.
    * The adapter used is flexible adapter where we can update list online when connected to different devices.
    *
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {

            Client client = (Client) data.getBundleExtra("client").getSerializable("client");
            clientList.add(client);
            ClientViewActivity.data.add(new SimpleItem(client.getfName(), client.getlName(), (String) client.getImageURL(), client.getDocId()));
            adapter.updateDataSet(ClientViewActivity.data);
            adapter.notifyDataSetChanged();
            Log.d("ClientViewActivity","inside the onActivityResult client added");
        }
    }
}
