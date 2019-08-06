package com.example.travelmentic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ListActivity extends AppCompatActivity {
/*    ArrayList<TravelDeal>deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
         inflater.inflate(R.menu.list_activity_menu,menu);
         MenuItem Insertmenu=menu.findItem(R.id.insert_menu);
          if (FirebaseUtil.isAdmin==true){
              Insertmenu.setVisible(true);
          }
          else{
              Insertmenu.setVisible(false);
          }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.insert_menu:
                Intent intent= new Intent(this, DealActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("logout","The user has logged out");
                                FirebaseUtil.attachListener();
                            }

                        });
                FirebaseUtil.detachListener();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.openFbReference("traveldeals",this);
        RecyclerView rvDeals=findViewById(R.id.rvDeals);
        final DealAdapter Adapter=new DealAdapter();
        rvDeals.setAdapter(Adapter);
        LinearLayoutManager DealsLinearlayoutManager =new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        rvDeals.setLayoutManager(DealsLinearlayoutManager);
        FirebaseUtil.attachListener();

    }
    public void ShowMenu(){
        invalidateOptionsMenu();
    }
}
