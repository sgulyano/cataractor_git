package com.tucad.cataractor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;

public class FormActivity extends AppCompatActivity {

    public static final String TAG = "FormActivity";
    private static final int TAKE_PHOTO_REQUEST = 1;
    public static final String EXTRA_FIRSTNAME = "com.tucad.cataractor.FIRSTNAME";
    public static final String EXTRA_LASTNAME = "com.tucad.cataractor.LASTNAME";
    public static final String EXTRA_SEX = "com.tucad.cataractor.SEX";
    public static final String EXTRA_AGE = "com.tucad.cataractor.AGE";
    public static final String EXTRA_IMAGEPATH = "com.tucad.cataractor.IMAGEPATH";

    @BindView(R.id.firstname) EditText firstname;
    @BindView(R.id.lastname) EditText lastname;
    @BindView(R.id.radiogpsex) RadioGroup radiogpsex;
    @BindView(R.id.age) EditText age;

    @BindView(R.id.text_input_layout_firstname) TextInputLayout til_firstname;
    @BindView(R.id.text_input_layout_lastname) TextInputLayout til_lastname;
    @BindView(R.id.text_input_layout_age) TextInputLayout til_age;

    @BindView(R.id.savebutton) Button savebutton;
    @BindView(R.id.takepicbutton) ImageButton takepicbutton;
    @BindView(R.id.eyeimage) ImageView eyeimageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        ButterKnife.bind(this);
    }

    /**
     * Save form by passing eye record to Detail Activity
     */
    @OnClick(R.id.savebutton) void saveform() {
        if (validateForm()) {
            Log.e("TAG", "Save form");
            Intent intent = new Intent(this, DetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            Bundle extras = new Bundle();

            String firstname_str = firstname.getText().toString();
            extras.putString(EXTRA_FIRSTNAME, firstname_str);
            String lastname_str = lastname.getText().toString();
            extras.putString(EXTRA_LASTNAME, lastname_str);
            String age_str = age.getText().toString();
            extras.putString(EXTRA_AGE, age_str);
            final String sex_str = ((RadioButton) findViewById(radiogpsex.getCheckedRadioButtonId()))
                    .getText().toString();
            extras.putString(EXTRA_SEX, sex_str);

            intent.putExtras(extras);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Check that all fields in the form are filled
     */
    private Boolean validateForm() {
        Boolean result = true;
        if(firstname.getText().toString().trim().length() == 0) {
            til_firstname.setError("กรุณาใส่ชื่อจริง");
            result = false;
        }
        if(lastname.getText().toString().trim().length() == 0) {
            til_lastname.setError("กรุณาใส่นามสกุล");
            result = false;
        }
        if(age.getText().toString().trim().length() == 0) {
            til_age.setError("กรุณาใส่อายุ");
            result = false;
        }
        if (ResultHolder.getImage() == null) {
            Toast.makeText(this, "กรุณาถ่ายรูปตา",
                    Toast.LENGTH_LONG).show();
        }
        return result;
    }

    /**
     * Take eye picture
     */
    @OnClick(R.id.takepicbutton) void takepicture() {
        Log.e("TAG", "Take picture begins");
        Intent intent = new Intent(this, PhotoActivity.class);
        startActivityForResult(intent, TAKE_PHOTO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PHOTO_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                Log.e(TAG, "Result code OK");

                byte[] jpeg = ResultHolder.getImage();

                if (jpeg != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);

                    if (bitmap != null) {
                        eyeimageView.setImageBitmap(bitmap);
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "Result code canceled");
            }
        }
    }
}
