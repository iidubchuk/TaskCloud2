package com.testing.iidubchuk.taskcloud;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    List<Task> Ctasks;
    FirebaseUser user;
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    int LastID;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_task);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                Ctasks = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    tasks.add(postSnapshot.getValue(String.class));
                    Ctasks.add(new Task(postSnapshot.getKey(), postSnapshot.getValue(String.class)));
                    LastID = Integer.parseInt(postSnapshot.getKey());
                }
                UpdateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        listViewTask.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(ListTask.this);

                final Task itemTask = Ctasks.get(i);
                dlg.setMessage("Delete item?");
                dlg.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ListTask.this, "no delete", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DeleteTAsk(itemTask);
                        Toast.makeText(ListTask.this, "delete " + String.valueOf(i), Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.menu_exit:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void UpdateUI() {

        if (Ctasks != null) {

            ArrayAdapter<Task> adapter = new ArrayAdapter<Task>(this, android.R.layout.simple_list_item_1, Ctasks);
            listViewTask.setAdapter(adapter);
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

            case R.id.ListTask_email:
            case R.id.ListTask_name: startActivity(new Intent(this, ProfileActivity.class));
                startActivity(new Intent(this, ProfileActivity.class));
                break;
        }
    }

    private void AddTask(String t) {

        Task task;
        if (Ctasks != null && Ctasks.size() > 0) {
            task = new Task(Ctasks.get(Ctasks.size() - 1).GetIdInt() + 1, t);
        } else
            task = new Task(0, t);

        mRef.child(task.Id).setValue(task.TaskName);
//
//        LastID++;
//        mRef.child(String.valueOf(LastID)).setValue(t);
    }

    private void DeleteTAsk(Task task) {
        mRef.child(task.Id).removeValue();
    }

    private boolean isValidi() {
        if (TextUtils.isEmpty(ETNewTask.getText().toString())) {
            return false;
        } else return true;
    }
}
