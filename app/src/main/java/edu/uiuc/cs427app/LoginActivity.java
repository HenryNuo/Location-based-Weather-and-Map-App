package edu.uiuc.cs427app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Activity that allows user to login
 */
public class LoginActivity extends AppCompatActivity {
    EditText username,password;
    Button btnLogin;
    ProgressBar bar;

    public static Activity act;

    /**
     * Runs when LoginActivity is rendered
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        act = this;

        TextView tvsignup = findViewById(R.id.loginhere);
        tvsignup.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));
        SharedPreferences prefs = getSharedPreferences("Login", MODE_PRIVATE);

        Boolean isLogin = prefs.getBoolean("isLogin",false);
//        if (isLogin) {
//            startActivity(new Intent(LoginActivity.this,MainActivity.class));
//            finish();
//        }

        bar = findViewById(R.id.progress);
        username = findViewById(R.id.edit_text_username);
        password = findViewById(R.id.edit_text_pass);
        btnLogin = findViewById(R.id.letTheUserLogIn);

        /**
         * Add onclick listener to be triggered whenever login button is pressed
         */
        btnLogin.setOnClickListener(view -> {
            String name = username.getText().toString();
            String pass = password.getText().toString();

            if (name.isEmpty()) {
                username.setError("Enter Username");
            } else if (pass.isEmpty()) {
                password.setError("Enter Password");
            } else {
                bar.setVisibility(View.VISIBLE);
                /**
                 * Calls firebase instance to retrieve data given a username
                 */
                FirebaseDatabase.getInstance().getReference().child("users").child(name).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            String password1 = dataSnapshot.child("password").getValue(String.class);
                            int color = dataSnapshot.child("settings").child("color").getValue(Integer.class);

                            if (password1.equals(pass)) {
                                // saves user data for application across activites
                                SharedPreferences.Editor editor = getSharedPreferences("Login", MODE_PRIVATE).edit();
                                editor.putString("name", name);
                                editor.putString("password", pass);
                                editor.putInt("color", color);
                                editor.putBoolean("isLogin", true);
                                editor.commit();

                                // saves city list data from database
                                ArrayList<String> cities = new ArrayList<>();

                                for (DataSnapshot ds : dataSnapshot.child("cities").getChildren()) {
                                    cities.add((String) ds.getValue());
                                }

                                // saves data to and starts MainActivity intent
                                startActivity(new Intent(
                                        LoginActivity.this, MainActivity.class)
                                        .putExtra("cities", cities));
                                finish();
                            } else {
                                // incorrect password
                                bar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // no username was found
                            bar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Enter Correct Username!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    /**
                     * Called when having trouble connecting to database
                     * @param databaseError
                     */
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        bar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "A database error occurred.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}