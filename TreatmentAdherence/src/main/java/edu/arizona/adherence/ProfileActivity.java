package edu.arizona.adherence;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ProfileActivity extends Activity {

    private EditText txtName;
    private EditText txtHeight;
    private EditText txtWeight;
    private Spinner spnAge;
    private Spinner spnGender;

    public static final String PREFS_NAME = "CrowdsensingPrefs";
    public static final String NAME = "profile_name";
    public static final String AGE = "profile_age";
    public static final String GENDER = "profile_gender";
    public static final String HEIGHT = "profile_height";
    public static final String WEIGHT = "profile_weight";

    public static final String PROFILE_NEWUSER = "profile_newuser";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        Button btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmClick();
            }
        });

        txtName = (EditText) findViewById(R.id.txtName);
        txtHeight = (EditText) findViewById(R.id.txtHeight);
        txtWeight = (EditText) findViewById(R.id.txtWeight);

        spnAge = (Spinner) findViewById(R.id.spnAge);
        Integer[] validAges = getValidAges();
        ArrayAdapter<Integer> ageAdapter = new ArrayAdapter<Integer>(
                this, android.R.layout.simple_spinner_item, validAges);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAge.setAdapter(ageAdapter);

        spnGender = (Spinner) findViewById(R.id.spnGender);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this, R.array.gender, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGender.setAdapter(genderAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        String name = preferences.getString(NAME, "");
        System.out.println("NAME: " + name);

        if (!name.isEmpty()) // The user already filled out the profile
            startMainActivity(false);
    }

    private Integer[] getValidAges() {
        Integer[] validAges = new Integer[101];
        for (int i = 10; i <= 110; i++)
            validAges[i - 10] = i;
        return validAges;
    }

    private void confirmClick() {
        String name = txtName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, "The field name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        int age = Integer.valueOf(spnAge.getSelectedItem().toString());
        String gender = spnGender.getSelectedItem().toString();

        float height, weight;
        try {
            height = Float.valueOf(txtHeight.getText().toString());
        } catch (NumberFormatException nfe) {
            height = 0;
        }

        try {
            weight = Float.valueOf(txtWeight.getText().toString());
        } catch (NumberFormatException nfe) {
            weight = 0;
        }

        if ((height == 0) || (weight == 0)) {
            Toast.makeText(this, "The field height or weight cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        saveProfile(name, age, gender, height, weight);


        startMainActivity(true);
    }

    private void saveProfile(String name, int age, String gender, float height, float weight) {
        // Save profile information
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(NAME, name);
        editor.putInt(AGE, age);
        editor.putString(GENDER, gender);
        editor.putFloat(HEIGHT, height);
        editor.putFloat(WEIGHT, weight);
        editor.apply();
    }

    private void startMainActivity(boolean newuser) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(PROFILE_NEWUSER, newuser);
        startActivity(intent);
        finish();
    }


}
