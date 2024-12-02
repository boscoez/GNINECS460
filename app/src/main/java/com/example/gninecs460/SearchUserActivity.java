package com.example.gninecs460;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gninecs460.adapter.SearchUserRecyclerAdapter;
import com.example.gninecs460.model.UserModel;
import com.example.gninecs460.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
/**
 * Activity for searching and displaying users based on username input.
 * Developed by Boscoe and Howey as part of the authentication, login,
 * signup, profile, and chat features of the Academic Alliance Chat Application.
 */
public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;

    SearchUserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.seach_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);
        // Focus the input field immediately when the activity starts
        searchInput.requestFocus();

        // Set listener to handle back navigation
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
        // Set listener for the search button to initiate a user search
        searchButton.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString();
            if(searchTerm.isEmpty() || searchTerm.length()<3){
                searchInput.setError("Invalid Username");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });
    }
    /**
     * Sets up the RecyclerView for displaying search results. This includes
     * configuring the Firestore query and the FirestoreRecyclerAdapter.
     * @param searchTerm The username to search for in the database.
     */
    void setupSearchRecyclerView(String searchTerm){
        // Create a query against the 'users' collection in Firestore
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username",searchTerm)
                .whereLessThanOrEqualTo("username",searchTerm+'\uf8ff');
        // Set up options for the FirestoreRecyclerAdapter using the query
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class).build();

        adapter = new SearchUserRecyclerAdapter(options,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Begin listening for real-time data updates when the activity starts
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop listening for data updates to save resources when the activity is not visible
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure adapter is listening for updates when the activity resumes
        if (adapter != null)
            adapter.startListening();
    }
}