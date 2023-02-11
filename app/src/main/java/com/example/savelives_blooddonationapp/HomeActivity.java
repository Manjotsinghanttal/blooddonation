package com.example.savelives_blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.savelives_blooddonationapp.Adapter.UserAdapter;
import com.example.savelives_blooddonationapp.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
private DrawerLayout drawerLayout;
private Toolbar toolbar;
private NavigationView nav_view;
private CircleImageView nav_profile_image;
private TextView nav_fullname,nav_bloodgroup,nav_type,nav_email;
private DatabaseReference userRef;
private RecyclerView recyclerView;
private ProgressBar progressBar;
private List<User> userList;
private UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Blood Donation App");
        nav_view= findViewById(R.id.nav_View);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(HomeActivity.this,drawerLayout,
                toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(this);
        nav_profile_image = nav_view.getHeaderView(0).findViewById(R.id.nav_user_image);
        nav_fullname = nav_view.getHeaderView(0).findViewById(R.id.nav_user_fullname);
        nav_email = nav_view.getHeaderView(0).findViewById(R.id.nav_user_email);
        nav_bloodgroup = nav_view.getHeaderView(0).findViewById(R.id.nav_user_bloodgroup);
        nav_type = nav_view.getHeaderView(0).findViewById(R.id.nav_user_type);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recycleView);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(HomeActivity.this,userList);
        recyclerView.setAdapter(userAdapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();
                if (type.equals("donor")){
                    readRecipient();
                }else{
                    readDonors();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("name").getValue().toString();
                    nav_fullname.setText(name);
                    String email = snapshot.child("email").getValue().toString();
                    nav_email.setText(email);
                    String bloodgroup = snapshot.child("bloodgroup").getValue().toString();
                    nav_bloodgroup.setText(bloodgroup);
                    String type = snapshot.child("type").getValue().toString();
                    nav_type.setText(type);
                    if(snapshot.hasChild("profilepictureurl")){
                        String imageUrl = snapshot.child("profilepictureurl").getValue().toString();
                        Glide.with(getApplicationContext()).load(imageUrl).into(nav_profile_image);
                    }else{
                        nav_profile_image.setImageResource(R.drawable.emptycontact);
                    }
                    Menu nav_menu = nav_view.getMenu();
                    if(type.equals("donor")){
                        nav_menu.findItem(R.id.sendEmail).setTitle("Received Emails");
                        nav_menu.findItem(R.id.notifications).setVisible(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readDonors() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("type").equalTo("donor");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if(userList.isEmpty()){
                    Toast.makeText(HomeActivity.this, "NO Recipient", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readRecipient() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("type").equalTo("recipient");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if(userList.isEmpty()){
                    Toast.makeText(HomeActivity.this, "NO Recipient", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.aplus:
                Intent intent2 = new Intent(HomeActivity.this,CategorySelectedActivity.class);
                intent2.putExtra("group","A+");
                startActivity(intent2);
                break;
            case R.id.aminus:
                Intent intent3 = new Intent(HomeActivity.this,CategorySelectedActivity.class);
                intent3.putExtra("group","A-");
                startActivity(intent3);
                break;
            case R.id.bplus:
                Intent intent4 = new Intent(HomeActivity.this,CategorySelectedActivity.class);
                intent4.putExtra("group","B+");
                startActivity(intent4);
                break;
            case R.id.bminus:
                Intent intent5 = new Intent(HomeActivity.this,CategorySelectedActivity.class);
                intent5.putExtra("group","B-");
                startActivity(intent5);
                break;
            case R.id.abplus:
                Intent intent6 = new Intent(HomeActivity.this,CategorySelectedActivity.class);
                intent6.putExtra("group","AB+");
                startActivity(intent6);
                break;
            case R.id.abminus:
                Intent intent7 = new Intent(HomeActivity.this,CategorySelectedActivity.class);
                intent7.putExtra("group","AB-");
                startActivity(intent7);
                break;
            case R.id.oplus:
                Intent intent8 = new Intent(HomeActivity.this,CategorySelectedActivity.class);
                intent8.putExtra("group","O+");
                startActivity(intent8);
                break;
            case R.id.ominus:
                Intent intent9 = new Intent(HomeActivity.this,CategorySelectedActivity.class);
                intent9.putExtra("group","0-");
                startActivity(intent9);
                break;
            case R.id.profile:
                Intent intent = new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.compatible:
                Intent intent10 = new Intent(HomeActivity.this,CategorySelectedActivity.class);
                intent10.putExtra("group","Compatible with Me");
                startActivity(intent10);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.sendEmail:
                Intent intent11 = new Intent(HomeActivity.this,SendEmailActivity.class);
                startActivity(intent11);
                break;
            case R.id.notifications:
                Intent intent12 = new Intent(HomeActivity.this,NotificationsActivity.class);
                startActivity(intent12);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}