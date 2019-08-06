package com.example.travelmentic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    public static final int PICTURE_REQUEST=42;
    EditText textTitle,textDescription,textPrice;
    TravelDeal deal;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
      //  FirebaseUtil.openFbReference("traveldeals",this);
        mFirebaseDatabase=FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference=FirebaseUtil.mDatabaseReference;
      textTitle=findViewById(R.id.textTitle);
      textDescription=findViewById(R.id.textDescription);
      textPrice=findViewById(R.id.textPrice);


        Intent intent=getIntent();
        TravelDeal deal=(TravelDeal)intent.getSerializableExtra("Deal");
        if (deal==null){
            deal =new TravelDeal();
        }
        this.deal=deal;
        textTitle.setText(deal.getTitle());
        textDescription.setText(deal.getDescription());
        textPrice.setText(deal.getPrice());
        ShowImage(deal.getImageURL());
        Button btnImage=findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(intent.createChooser(intent,"upload picticture"),PICTURE_REQUEST);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICTURE_REQUEST&&resultCode==RESULT_OK){
            Uri Imageurl=data.getData();
            StorageReference ref=FirebaseUtil.mStorageRef.child(Imageurl.getLastPathSegment());
            ref.putFile(Imageurl).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String Url=taskSnapshot.getTask().toString();
                    deal.setImageURL(Url);
                    String pictureName=taskSnapshot.getStorage().getPath();
                    deal.setImageName(pictureName);
                    Log.d("url",Url);
                    Log.d("name",pictureName);
                    ShowImage(Url);

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this,"Deal saved",Toast.LENGTH_LONG).show();
                clean();
                BackToList();
                return true;
            case R.id.delete_menu:
                DeleteDeal();
                Toast.makeText(this,"Deal deleted",Toast.LENGTH_LONG).show();
                BackToList();
                default:
                    return super.onOptionsItemSelected(item);
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.save_menu,menu);
        if (FirebaseUtil.isAdmin){
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditTexts(true);
        }
        else{
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditTexts(false);

        }

        return true;
    }

    private void saveDeal() {
        deal.setTitle(textTitle.getText().toString());
        deal.setDescription(textDescription.getText().toString());
       deal.setPrice(textPrice.getText().toString());
       if (deal.getId()==null){
           mDatabaseReference.push().setValue(deal);
       }
      else {
          mDatabaseReference.child(deal.getId()).setValue(deal);
       }
    }
     private void DeleteDeal(){
        if (deal==null){
            Toast.makeText(this,"Please save the deal before deleting,no deal found",Toast.LENGTH_LONG).show();
            return;
        }
        else {
            mDatabaseReference.child(deal.getId()).removeValue();
            Log.d("image name",deal.getImageName());
            if (deal.getImageName()!=null&&deal.getImageName().isEmpty()==false){
                StorageReference picref=FirebaseUtil.mStorage.getReference().child(deal.getImageName());
                picref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("delete image","image deleted successful");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("delete image",e.getMessage());

                    }
                });
            }
        }
     }

     public void BackToList(){
        Intent intent=new Intent(this,ListActivity.class);
        startActivity(intent);
     }
    private void clean() {
        textTitle.setText("");
        textDescription.setText("");
        textPrice.setText("");
    }

    private void enableEditTexts(boolean isEnabled){
        textTitle.setEnabled(isEnabled);
        textDescription.setEnabled(isEnabled);
        textPrice.setEnabled(isEnabled);
    }
    private void ShowImage(String Url){


    }

      }




