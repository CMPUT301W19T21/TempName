package ca.team21.pagepal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import ca.team21.pagepal.Book.Book;
import ca.team21.pagepal.Book.BookFragment;
import ca.team21.pagepal.Book.EditBookActivity;

public class MainActivity extends AppCompatActivity
        implements HomeFragment.OnHomeInteractionListener,
        BorrowingFragment.OnBorrowingInteractionListener,
        BookFragment.OnListFragmentInteractionListener,
        NotificationsFragment.OnNotificationsInteractionListener,
        ProfileFragment.OnProfileInteractionListener {

    private static final String TAG = "MainActivity";
    private static final int EDIT_USER = 9;
    private static final int ADD_BOOK = 5;
    public static final String USER_EXTRA = "ca.team21.pagepal.user";
    public static final String BOOK_EXTRA = "ca.team21.pagepal.Book.Book";

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private DatabaseReference usersRef;
    private FirebaseUser authUser;
    private User user;

    /**
     * Sets up the Bottom navigation bar
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadFragment(HomeFragment.newInstance());
                    return true;
                case R.id.borrowed_books:
                    loadFragment(BorrowingFragment.newInstance());
                    return true;
                case R.id.owned_books:
                    loadFragment(BookFragment.newInstance(1, user.getOwnedBookList())); // 1 = number of columns in book list
                    return true;
                case R.id.navigation_notifications:
                    loadFragment(NotificationsFragment.newInstance());
                    return true;
                case R.id.navigation_profile:
                    loadFragment(ProfileFragment.newInstance(user));
                    return true;
                default:
                    loadFragment(HomeFragment.newInstance());
            }
            return false;
        }
    };

    /**
     * Load the given fragment in to view.
     *
     * @param fragment  The Fragment to load.
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Wait for user to authenticate or timeout
        long waitTime = new Date().getTime() + 2 * 1000;
        while ( authUser == null && new Date().getTime() < waitTime ) {
            authUser = FirebaseAuth.getInstance().getCurrentUser();
        }
        // Quit if authentication fails. Might want to add something more elegant later...
        if (authUser == null) {
            Toast.makeText(MainActivity.this, "Couldn't Authenticate",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Connect to database
        usersRef = FirebaseDatabase.getInstance().getReference();

        // Get the user who is logged in
        usersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        user = dataSnapshot.child("users").child(authUser.getUid()).getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                    }
                });

        // Set up top toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        // Set up bottom nav bar
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(HomeFragment.newInstance());



    }


    // TODO Remove the toolbar and place the sign out option in the user profile page.
    /**
     * Creates the buttons in the toolbar.
     *
     * @param menu  The menu resource to inflate.
     * @return      The result of the super call.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Responds to clicking items in the toolbar.
     *
     * @param item  The item that was pressed.
     * @return      The result of the click.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onHomeInteraction() {
    }

    @Override
    public void onBorrowingInteraction() {
    }

    @Override
    public void onListFragmentInteraction(Book book) {
        Intent intent = new Intent(this, EditBookActivity.class);
        startActivityForResult(intent, ADD_BOOK);
    }

    @Override
    public void onNotificationsInteraction() {
    }

    @Override
    public void onProfileInteraction(User user) {
        Intent intent = new Intent(this, EditUserActivity.class);
        intent.putExtra(USER_EXTRA, user);
        startActivityForResult(intent, EDIT_USER);
    }
}