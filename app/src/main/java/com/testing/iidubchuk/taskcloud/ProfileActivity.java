package com.testing.iidubchuk.taskcloud;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private TextView name;
    private TextView mail;
    private FirebaseUser fuser;
    private DatabaseReference mRef;
    private  User user;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        name = findViewById(R.id.Profile_tv_name);
        mail = findViewById(R.id.Profile_tv_mail);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("/users/"+fuser.getUid());

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                UpdeteUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                startActivity(new Intent(this,ListTask.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UpdeteUI()
    {
        if (user!=null)
        {
            name.setText("Name: "+user.Name);
            mail.setText("Mail: "+user.Email);
        }
    }


    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.Profile_btn_exit:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, SignInActivity.class));
                break;
            case R.id.Profile_btn_storage:
                startActivity(new Intent(this,StorageActivity.class));
                break;
        }
    }
}
