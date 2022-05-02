package uk.ac.tees.aad.b1475063;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.tees.aad.b1475063.Adapters.ToDoAdapter;
import uk.ac.tees.aad.b1475063.Model.ToDoModel;
import uk.ac.tees.aad.b1475063.Utils.DatabaseHandler;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

//    Button button;
    Animation rotateOpen;
    Animation rotateClose;
    Animation fromBottom;
    Animation toBottom;
    //api to find location based on longitude and lattitude
//https://api.myptv.com/geocoding/v1/locations/by-position/54.574226/-1.234956?language=en&apiKey=MjhhNDBjN2JmMmI2NGNmNGIyMWFjOWZlMDQ3OWIwOWI6MmQyMDY5YmYtOWJmMy00ZTg4LWE5NjctNDE1ZmFlMDM2MDdj
    private DatabaseHandler db;

    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;
    private FloatingActionButton add_task_button;
    private FloatingActionButton add_location_button;
    private FloatingActionButton search_location_button;
    private boolean clicked = false;
    String currentLocationAddress;

    private List<ToDoModel> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        db = new DatabaseHandler(this);
        db.openDatabase();



        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
//        button = (Button)findViewById(R.id.map_button);
        fab = findViewById(R.id.fab);
        add_task_button = findViewById(R.id.add_task);
        add_location_button = findViewById(R.id.add_location);
        search_location_button = findViewById(R.id.search_location);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoAdapter(db,MainActivity.this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);


        taskList = db.getAllTasks();
        Collections.reverse(taskList);

        tasksAdapter.setTasks(taskList);



//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(),MapsActivity.class);
//                startActivity(i);
//            }
//        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
                onfabClicked();
            }
        });
        add_task_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
        add_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
        //loads google maps activity
        search_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
                Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(i);
            }
        });


        //=================================
        String url = "https://api.myptv.com/geocoding/v1/locations/by-position/54.574226/-1.234956?language=en&apiKey=MjhhNDBjN2JmMmI2NGNmNGIyMWFjOWZlMDQ3OWIwOWI6MmQyMDY5YmYtOWJmMy00ZTg4LWE5NjctNDE1ZmFlMDM2MDdj";
        Log.d("STATE","HELLO");
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("STATE","received response");
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("locations");
                    Log.d("STATE", String.valueOf(jsonArray.length()));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
//                        Log.d("STATE", String.valueOf(jObject.getString("formattedAddress")));
                        currentLocationAddress = jObject.getString("formattedAddress");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
        //==================================

    }

    public void assignAnimations(){
        //this is put here so that the animations don't bug out after the first play
        rotateOpen = AnimationUtils.loadAnimation(
                this,R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(
                this,R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(
                this,R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(
                this,R.anim.to_bottom_anim);
    }
    public void onfabClicked(){
        assignAnimations();
        setVisibility(clicked);
        setAnimation(clicked);
        clicked = !clicked;
    }
    public void setVisibility(boolean clicked){
        if(clicked){
            add_task_button.setVisibility(View.INVISIBLE);
            add_location_button.setVisibility(View.INVISIBLE);
            search_location_button.setVisibility(View.INVISIBLE);

        }else{
            add_task_button.setVisibility(View.VISIBLE);
            add_location_button.setVisibility(View.VISIBLE);
            search_location_button.setVisibility(View.VISIBLE);

        }

    }
    public void setAnimation(boolean clicked){
        if(clicked){
            add_task_button.setAnimation(toBottom);
            add_location_button.setAnimation(toBottom);
            search_location_button.setAnimation(toBottom);
            fab.setAnimation(rotateClose);

        }else{
            add_task_button.setAnimation(fromBottom);
            add_location_button.setAnimation(fromBottom);
            search_location_button.setAnimation(fromBottom);
            fab.setAnimation(rotateOpen);

        }
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }
}