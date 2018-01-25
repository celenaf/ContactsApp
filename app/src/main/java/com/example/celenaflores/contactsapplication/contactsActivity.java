package com.example.celenaflores.contactsapplication;

import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class contactsActivity extends AppCompatActivity {

    //for logging
    private final String TAG = "contacts";

    //filename for location data
    private final String FILENAME = "contact_data";

    //Views that we will update in this controller
    private ImageView mContactImage;
    private TextView mContactNameTextView;
    private TextView mContactAgeTextView;
    private Button mContactNextButton;
    private Button mContactPreviousButton;

    private EditText mContactLocationEditText; //editable field to set contact's location
    private TextView mContactLocationTextView; //to display contact's current location

    //these will update the rating bar
    private RatingBar ratingBar;
    private TextView txtRatingValue;
    private Button btnSubmit;

    //our model, a fixed array with 5 characters from TWD
    private final Contacts[] mContacts = new Contacts[] {
            new Contacts(R.string.rick_name, R.drawable.rick, R.string.rick_age),
            new Contacts(R.string.carl_name, R.drawable.carl, R.string.carl_age),
            new Contacts(R.string.glenn_name, R.drawable.glenn, R.string.glenn_age),
            new Contacts(R.string.maggie_name, R.drawable.maggie, R.string.maggie_age),
            new Contacts(R.string.daryl_name, R.drawable.daryl, R.string.daryl_age)
    };

    //default is to show Rick, the first character of TWD
    private int mCurrentIndex = 0;

    //key to save mCurrentIndex in Bundle
    private static final String KEY_CONTACT_INDEX = "contact_index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        addListenerOnRatingBar();
        addListenerOnButton();

        try(FileInputStream in = openFileInput(FILENAME)) {
            //load locations from file, if file was written
            String line;
            char next;

            //loop over the 5 lines in the file (hardcoded, lazy!)
            for(int i = 0; i < 5; i++) {
                line = "";
                while ((next = (char) in.read()) != '\n') {
                    line += next;
                }
                mContacts[i].setLocation(line);
                Log.d(TAG, "Read the following location from file: " + line);
            }
            in.close();
        } catch (java.io.FileNotFoundException fnfe) {
            Log.d(TAG, "FileNotFoundException when trying to load file" + fnfe);
        } catch(java.io.IOException ioe) {
            Log.d(TAG, "IOException when trying to load file" + ioe);
        }

        //update with saved state when re-creating this activity
        if(savedInstanceState != null)
            mCurrentIndex = savedInstanceState.getInt(KEY_CONTACT_INDEX, 0);

        //lookup each View by ID so we can set their attributes/behaviors
        mContactImage = (ImageView) findViewById(R.id.contactImageView);
        mContactNameTextView = (TextView) findViewById(R.id.contactNameTextView);
        mContactAgeTextView = (TextView) findViewById(R.id.contactAgeTextView);
        mContactNextButton = (Button) findViewById(R.id.contactNextButton);
        mContactPreviousButton = (Button) findViewById(R.id.contactPreviousButton);
        mContactLocationEditText = (EditText) findViewById(R.id.locationEditText);
        mContactLocationTextView = (TextView) findViewById(R.id.contactLocationTextView);

        //add listener to handle changes to name
        mContactLocationEditText.addTextChangedListener(nameEditTextWatcher);

        //set location in EditText initially (bad idea to put this in update!)
        mContactLocationEditText.setText(mContacts[mCurrentIndex].getLocation());

        //update all text fields and the image view based on current state
        update();

        //define the behavior of the Next Button (go to next contact)
        mContactNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //% ensures we wrap back to index 0
                mCurrentIndex = (mCurrentIndex + 1) % 5;

                //update all text fields and the image view based on current state
                update();
                mContactLocationEditText.setText(mContacts[mCurrentIndex].getLocation());
            }
        });

        //define the behavior of the Next Button (go to previous contact)
        mContactPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1);

                //ensures we wrap back to index 4 (instead of -1)
                if(mCurrentIndex == -1)
                    mCurrentIndex = 4;

                //update all text fields and the image view based on current state
                update();

                mContactLocationEditText.setText(mContacts[mCurrentIndex].getLocation());

            }
        });
    } //end of onCreate

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        //when app is destroyed, save file info
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);

            //write each contact location to a new line
            for(int i = 0; i < 5; i++) {
                outputStream.write((mContacts[i].getLocation() + '\n').getBytes());
                Log.d(TAG, "Writing location: " + mContacts[i].getLocation() + '\n');
            }

            outputStream.close();
        } catch (FileNotFoundException fnfe) {
            Log.d(TAG, "FileNotFound Exception when trying to write output");
        } catch (IOException ioe) {
            Log.d(TAG, "IOException when trying to write output");
        }
    }

    //define the TextWatcher for mNameEditText
    private final TextWatcher nameEditTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //update location based on what user entered
            mContacts[mCurrentIndex].setLocation(s.toString());
            update();
        }

        //we have to override these, but we don't have to make them do anything
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
    };

    //we use onSaveInstanceState to keep track of current contact (mainly
    // to preserve it across rotations)
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt(KEY_CONTACT_INDEX, mCurrentIndex);
    }

    //The update method updates the contact's name and location TextViews
    // as well as the contact's ImageView.  Called whenever next/previous is clicked.
    private void update() {
        //set the ImageView with the current image and the TextView with the current name
        //recall: we are saving resource IDs in the Contact class
        mContactImage.setImageResource(mContacts[mCurrentIndex].getmImageResId());
        mContactNameTextView.setText(mContacts[mCurrentIndex].getmNameResId());
        mContactAgeTextView.setText(mContacts[mCurrentIndex].getmAgeResId());

        //update the location TextView to display the current location
        //recall: the location (string) is stored directly in the object after loading
        // from the FILENAME textfile
        mContactLocationTextView.setText(mContacts[mCurrentIndex].getLocation());
    }

    public void addListenerOnRatingBar() {

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        txtRatingValue = (TextView) findViewById(R.id.txtRatingValue);

        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                txtRatingValue.setText(String.valueOf(rating));

            }
        });
    }

    public void addListenerOnButton() {

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        //if click on me, then display the current rating value.
        btnSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(contactsActivity.this,
                        String.valueOf(ratingBar.getRating()),
                        Toast.LENGTH_SHORT).show();

            }

        });

    }

}