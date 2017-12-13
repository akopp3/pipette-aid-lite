package com.akoppu.pipetteaid20;

import android.content.Intent;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private Button newP, exP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /*String text = readResourceFile("test_semi.txt");
        Semiprotocol protocol = null;
        ParseSemiprotocol parser = new ParseSemiprotocol();
        try {
            protocol = parser.run(text);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("FAILURE", "Couldnt parse" );
        }

        db.collection("semiprotocols")
                .add(protocol)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
        */

        newP = findViewById(R.id.button);
        exP = findViewById(R.id.button2);

        exP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplication(), ProtocolListActivity.class);
                startActivity(i);
            }
        });

    }

    private String readResourceFile(String name) {
        try {
            AssetManager am = getApplicationContext().getAssets();
            InputStream is = null;
            is = am.open(name);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(is));

            StringBuilder sb = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                sb.append(inputLine).append("\n");
            in.close();

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
