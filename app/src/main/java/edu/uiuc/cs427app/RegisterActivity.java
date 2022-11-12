package edu.uiuc.cs427app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Activity that allows user to signup
 */
public class RegisterActivity extends AppCompatActivity {
    EditText username,password;
    Button btnsignUp;
    ProgressBar bar;
    int color = 1;
    RadioGroup radioGroup;

    /**
     * Runs when RegisterActivity is rendered
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.edit_text_username);
        password = findViewById(R.id.edit_text_pass);
        btnsignUp = findViewById(R.id.letTheUserSignUp);
        radioGroup = findViewById(R.id.radiogroup);
        ((RadioButton) radioGroup.findViewById(R.id.red)).setChecked(true);
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.red) {
                color = 1;
            }
            else if (i == R.id.blue) {
                color = 2;
            }
        });
        bar = findViewById(R.id.progress);

        btnsignUp.setOnClickListener(view -> {
            String name = username.getText().toString();
            String pass = password.getText().toString();
            if (name.isEmpty()) {
                username.setError("Enter Username");
            }
            else if (pass.isEmpty()) {
                password.setError("Enter Password");

            } else{
                FirebaseDatabase.getInstance().getReference().child("users").child(name).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            Toast.makeText(RegisterActivity.this, "Username already exists.", Toast.LENGTH_SHORT).show();

                        } else {
                            bar.setVisibility(View.VISIBLE);

                            // initializes default values for user and saves to database
                            HashMap<String, String> map = new HashMap<>();
                            map.put("password", pass);
                            map.put("username", name);
                            FirebaseDatabase.getInstance().getReference().child("users").child(name).setValue(map);
                            FirebaseDatabase.getInstance().getReference().child("users").child(name).child("settings").child("color").setValue(color);

                            // saves user data for application across activities
                            SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
                            editor.putString("name", name);
                            editor.putString("password", pass);
                            editor.putInt("color", color);
                            editor.putBoolean("isLogin", true);
                            editor.commit();

                            // saves data to and starts MainActivity intent
                            ArrayList<String> cities = new ArrayList<>();
                            startActivity(new Intent(
                                    RegisterActivity.this, MainActivity.class)
                                    .putExtra("cities", cities));

                            finish();
                            LoginActivity.act.finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        bar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "A database error occurred.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        TextView signuphere = findViewById(R.id.signuphere);
        signuphere.setOnClickListener(view -> finish());
    }
}