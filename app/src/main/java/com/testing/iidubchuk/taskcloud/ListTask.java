package com.testing.iidubchuk.taskcloud;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListTask extends AppCompatActivity {
    TextView TVname;
    TextView TVemail;
    EditText ETNewTask;
    ListView listViewTask;


    List<String> tasks;
    FirebaseUser user;
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    int LastID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);
        TVname = findViewById(R.id.ListTask_name);
        TVemail = findViewById(R.id.ListTask_email);
        listViewTask = findViewById(R.id.ListTask_for_task);
        ETNewTask = findViewById(R.id.et_new_task);

        user = FirebaseAuth.getInstance().getCurrentUser();

        TVemail.setText(user.getEmail());
        TVname.setText(usernameFromEmail(user.getEmail()));
        mRef = FirebaseDatabase.getInstance().getReference("/users/" + user.getUid() + "/Task");
        LastID = 0;
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LastID = 0;
                tasks = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    tasks.add(postSnapshot.getValue(String.class));
                    LastID = Integer.parseInt(postSnapshot.getKey());
                }
                UpdateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        UpdateUI();
    }

    private void UpdateUI() {

        if (tasks != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
            listViewTask.setAdapter(adapter);
        } else {

            Log.e("UpdeteUI", "task list == null");
        }
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_new_task:
                if (isValidi()) {
                    AddTask(ETNewTask.getText().toString());
                    Toast.makeText(this, "Task add", Toast.LENGTH_SHORT).show();
                    ETNewTask.setText("");

                }
                break;
//            case R.id.btn_exit:
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(this, SignInActivity.class));
//                break;
            case R.id.ListTask_email:
            case R.id.ListTask_name:
                startActivity(new Intent(this,ProfileActivity.class));
                break;
        }
    }

    private void AddTask(String t) {
        LastID++;
        mRef.child(String.valueOf(LastID)).setValue(t);
    }

    private boolean isValidi() {
        if (TextUtils.isEmpty(ETNewTask.getText().toString())) {
            return false;
        } else return true;
    }
}
