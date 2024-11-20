package com.example.app1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass for displaying the user's profile.
 */
public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ImageView avatartv;
    private TextView name, email;
    private FloatingActionButton fab;
    private ProgressDialog pd;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase and UI components
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        avatartv = view.findViewById(R.id.avatartv);
        name = view.findViewById(R.id.nametv);
        email = view.findViewById(R.id.emailtv);
        fab = view.findViewById(R.id.fab);
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading profile...");
        pd.setCanceledOnTouchOutside(false);

        // Check if the user is logged in
        if (firebaseUser == null) {
            // Redirect to login screen if user is not logged in
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
            return null;
        }

        // Show loading dialog
        pd.show();

        // Query user data from Firebase Realtime Database
        Query query = databaseReference.orderByChild("email").equalTo(firebaseUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Hide loading dialog
                pd.dismiss();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    // Retrieve user data
                    String userName = "" + dataSnapshot1.child("name").getValue();
                    String userEmail = "" + dataSnapshot1.child("email").getValue();
                    String userImage = "" + dataSnapshot1.child("image").getValue();

                    // Set data to UI components
                    name.setText(userName);
                    email.setText(userEmail);

                    // Load image using Glide, set default if null or empty
                    try {
                        if (userImage.isEmpty()) {
                            Glide.with(getActivity()).load(R.drawable.default_avatar).into(avatartv);
                        } else {
                            Glide.with(getActivity()).load(userImage).into(avatartv);
                        }
                    } catch (Exception e) {
                        Log.e("ProfileFragment", "Error loading image", e);
                        Glide.with(getActivity()).load(R.drawable.default_avatar).into(avatartv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Hide loading dialog and log error
                pd.dismiss();
                Log.e("ProfileFragment", "Database error: " + databaseError.getMessage());
            }
        });

        // FAB to open EditProfilePage
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfilePage.class));
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // To include options menu in the fragment
    }
}
