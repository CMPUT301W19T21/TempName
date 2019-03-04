package ca.team21.pagepal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import static ca.team21.pagepal.MainActivity.USER_EXTRA;

public class EditUserActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "EditUserActivity";

    private FirebaseUser authUser;
    private DatabaseReference dbRef;
    private User user;
    private EditText email;
    private Button saveButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        // TODO force users to reauthenticate before editing.

        authUser = FirebaseAuth.getInstance().getCurrentUser();

        dbRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(authUser.getUid());

        Intent intent = getIntent();
        user = intent.getParcelableExtra(USER_EXTRA);

        email = findViewById(R.id.email_input);
        saveButton = findViewById(R.id.save);
        cancelButton = findViewById(R.id.cancel);

        email.setText(user.getEmail());

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.save:
                writeChanges();
                setResult(RESULT_OK);
                finish();
            case R.id.cancel:
                setResult(RESULT_CANCELED);
                finish();
        }

    }

    /**
     * Write the new user data to the database and the FirebaseUser profile if necessary.
     */
    private void writeChanges() {
        String newEmail = email.getText().toString();
        dbRef.child("email").setValue(newEmail);
        authUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User email address updated");
                }
            }
        });
    }
}
