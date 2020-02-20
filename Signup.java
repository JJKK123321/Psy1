package com.example.psy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class Signup extends AppCompatActivity {

    Button zapisz, wczytaj;
    ImageView image;
    EditText erasa, ewiek, einf;
    StorageReference storageReference;
    Dane dane;
    DatabaseReference ref;
    public Uri imageuri;
    private StorageTask uploadtask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        storageReference = FirebaseStorage.getInstance().getReference("Images");

        dane = new Dane();
        ref = FirebaseDatabase.getInstance().getReference().child("Dane");

        erasa = (EditText) findViewById(R.id.editrasa);
        ewiek = (EditText) findViewById(R.id.editwiek);
        einf = (EditText) findViewById(R.id.editinf);

        zapisz = (Button) findViewById(R.id.zapisz);
        wczytaj = (Button) findViewById(R.id.wczytaj);
        image = (ImageView) findViewById(R.id.zdj);

        wczytaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseFile();

            }
        });

        zapisz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadtask != null && uploadtask.isInProgress()) {
                    Toast.makeText(Signup.this, "Dodawanie trwa", Toast.LENGTH_LONG).show();


                }

                else{

                    UploadFile();
            }}
        });
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void UploadFile() {

        String piesid;
        piesid = System.currentTimeMillis()+"."+ getExtension(imageuri);
        dane.setRasa(erasa.getText().toString().trim());
        dane.setInformacje(einf.getText().toString().trim());
        int w = Integer.parseInt(ewiek.getText().toString().trim());
        dane.setWiek(w);
        ref.push().setValue(dane);

        StorageReference storageRef = storageReference.child(piesid);

       uploadtask = storageRef.putFile(imageuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                      //  Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(Signup.this,"Dodane", Toast.LENGTH_LONG).show();
                        if(uploadtask.isSuccessful()) {

                            zapisz.setEnabled(false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(Signup.this,"ZLE", Toast.LENGTH_LONG);

                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    private void ChooseFile(){

        Intent intent = new Intent();
        intent.setType("image/'");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData() != null){

            imageuri = data.getData();
            image.setImageURI(imageuri);
            zapisz.setEnabled(true);
        }
    }

}
