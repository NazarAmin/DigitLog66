package com.example.digitlog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Handover_Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    String engine, engine_focal, current_user, datetime, current_engine_focal_name;
    EditText comment_hand;
    Button update_hand;
    TextView PIC;
    Handover_c handover_c;
    String general_admin = "admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handover_);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        // Spinner element
        spinner = (Spinner) findViewById(R.id.spinner);
        PIC = (TextView) findViewById(R.id.pic);
        comment_hand = (EditText) findViewById(R.id.comment_hand);
        update_hand = (Button) findViewById(R.id.update_hand);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss");

        datetime = sdf.format(new Date());
        engine = GlobalClass.engine_number;
        current_user = GlobalClass.actual_user_name;

        DatabaseReference ref2, ref3;
        ref2 = firebaseDatabase.getReference("data/" + engine + "/OIC_History/" + datetime);
        ref3 = firebaseDatabase.getReference("data/" + engine + "/OIC");
        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                current_engine_focal_name = dataSnapshot.getValue(String.class);
                PIC.setText(current_engine_focal_name);
                GlobalClass.current_engine_focal = current_engine_focal_name;

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        update_hand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handover_c = new Handover_c(current_user, engine_focal, datetime, comment_hand.getText().toString());
                if (GlobalClass.current_engine_focal.equals(GlobalClass.actual_user_name) | GlobalClass.current_engine_focal.equals(general_admin)){
                    GlobalClass.current_engine_focal = engine_focal;
                    ref2.setValue(handover_c);
                    ref3.setValue(engine_focal);
                }else{
                    Toast.makeText(getApplicationContext(), "You are not authorized to " +
                            "handover " + engine + " this can be done by " + engine_focal, Toast.LENGTH_LONG).show();
                }
            }
        });
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        DatabaseReference ref9 = firebaseDatabase.getReference("data/users");
        ref9.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot mydatasnapshot : dataSnapshot.getChildren()) {
                        Users user = mydatasnapshot.getValue(Users.class);
                        categories.add(user.getUser());
                    }

                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // attaching data adapter to spinner
                    spinner.setAdapter(dataAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        engine_focal = parent.getItemAtPosition(position).toString();
        //GlobalClass.engine_focal = engine_focal;

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + engine_focal, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}