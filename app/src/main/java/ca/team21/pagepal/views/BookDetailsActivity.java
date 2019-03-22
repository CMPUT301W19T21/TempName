package ca.team21.pagepal.views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.team21.pagepal.R;
import ca.team21.pagepal.models.Notification;
import ca.team21.pagepal.models.Request;
import ca.team21.pagepal.models.User;
import ca.team21.pagepal.models.Book;

import static ca.team21.pagepal.views.MainActivity.BOOK_EXTRA;
import static ca.team21.pagepal.views.MainActivity.USER_EXTRA;

/**
 * Activity for viewing book details. Is also used for accepting/declining/requesting
 */
public class BookDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    Book book;
    User user;
    DatabaseReference dbRefUsers = FirebaseDatabase.getInstance().getReference().child("users");
    String ownerUsername;
    String ownerLabel;

    TextView titleView;
    TextView authorView;
    TextView isbnView;
    TextView statusView;
    TextView descriptionView;
    TextView ownerView;
    Button requestButton;
    Button acceptButton;
    Button declineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        Intent intent = getIntent();
        book = intent.getParcelableExtra(BOOK_EXTRA);
        user = intent.getParcelableExtra(USER_EXTRA);

        titleView = findViewById(R.id.title_view);
        authorView = findViewById(R.id.author_view);
        isbnView = findViewById(R.id.isbn_view);
        statusView = findViewById(R.id.status_view);
        descriptionView = findViewById(R.id.description_view);
        ownerView = findViewById(R.id.owner_view);
        requestButton = findViewById(R.id.request_button);
        acceptButton = findViewById(R.id.accept_button);
        declineButton = findViewById(R.id.decline_button);

        requestButton.setOnClickListener(this);
        acceptButton.setOnClickListener(this);
        declineButton.setOnClickListener(this);

        titleView.setText(book.getTitle());
        authorView.setText(book.getAuthor());
        String isbnLabel = "ISBN: " + book.getIsbn();
        isbnView.setText(isbnLabel);
        statusView.setText(book.getStatus().toUpperCase());
        descriptionView.setText(book.getDescription());


        ownerUsername = book.getOwner();

        if (ownerUsername.equals(user.getUsername())) { // current user owns this book
            ownerLabel = "You own this book";
            if (book.getStatus().equals(Book.REQUESTED)) {
                acceptButton.setVisibility(View.VISIBLE);
                declineButton.setVisibility(View.VISIBLE);
            }
        } else {
            ownerLabel = "Owner: " + ownerUsername;
            requestButton.setVisibility(View.VISIBLE);

            if (book.getStatus().equals(Book.AVAILABLE) || book.getStatus().equals(Book.REQUESTED)) {
                requestButton.setText("Request Book");
            } else if (book.getStatus().equals(Book.ACCEPTED) || book.getStatus().equals(Book.BORROWED)) {
                requestButton.setClickable(false);
                requestButton.setText("This book is unavailable");
            }
        }

        ownerView.setText(ownerLabel);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.request_button:
                Request request = new Request(book.getOwner(), user.getUsername(), book.getIsbn());
                request.writeToDb();
                String message = user.getUsername() + " has requested " + book.getTitle();
                String senderUsername = user.getUsername();
                String recipientUsername = book.getOwner();
                Notification notification = new Notification(message, senderUsername, recipientUsername, book.getIsbn(), book.getOwner());
                notification.writeToDb();
        }

    }

}
