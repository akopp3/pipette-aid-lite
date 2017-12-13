package com.akoppu.pipetteaid20;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProtocolListActivity extends AppCompatActivity {

    private final String TAG = "ProtocolListActivity";

    private RecyclerView rv;
    private RecyclerView.Adapter adapter;
    private ArrayList<Semiprotocol> existingProtocols;
    private ArrayList<String> protocolIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol_list);

        rv = findViewById(R.id.protocol_recycler);

        LinearLayoutManager lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);

        existingProtocols = new ArrayList<>();
        protocolIds = new ArrayList<>();

        adapter = new ProtocolListAdapter(existingProtocols, protocolIds, getApplicationContext());
        rv.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("semiprotocols")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Semiprotocol protocol = document.toObject(Semiprotocol.class);
                                existingProtocols.add(protocol);
                                protocolIds.add(document.getId());
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void parseToSemiProtocol (String data) {

    }
}
