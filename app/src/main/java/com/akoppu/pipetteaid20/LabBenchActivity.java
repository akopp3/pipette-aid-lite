package com.akoppu.pipetteaid20;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LabBenchActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private InventoryFragment ivf;
    private final String TAG  = "LabBenchActivity";
    private static Map<String, Integer> inventoryMap;
    private TextToSpeech tts;
    private static ArrayList<Pair<String, Integer>> items;
    private static String protocolId;
    private static Semiprotocol currProtocol;
    private static List<LabTask> steps;
    private static boolean protocolBegun = false;

    private static Map<String, String> labBench;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_bench);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ivf = new InventoryFragment();
        ivf.setup();

        labBench = new HashMap<>();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            protocolId = (String) b.get("ID");
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("semiprotocols").document(protocolId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            currProtocol = task.getResult().toObject(Semiprotocol.class);
                            steps = currProtocol.getSteps();
                        }
                        else {
                            Log.e("LabBenchFragment", "Error: ", task.getException());
                        }
                    }
                });



        inventoryMap = new HashMap<>();
        inventoryMap.put("P1000", 0);
        inventoryMap.put("P200", 1);
        inventoryMap.put("P20", 2);
        inventoryMap.put("pcr_plate_96", 3);
        inventoryMap.put("pcr_strip", 4);
        inventoryMap.put("pcr_tube", 5);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listen();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lab_bench, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class LabBenchFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private final String TAG = LabBenchFragment.class.getName();
        private TextView textView;
        private Button button;
        private int i;

        public LabBenchFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static LabBenchFragment newInstance(int sectionNumber) {
            LabBenchFragment fragment = new LabBenchFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lab_bench, container, false);
            textView = (TextView) rootView.findViewById(R.id.section_label);
            button = (Button) rootView.findViewById(R.id.button3);
            textView.setText("Begin protocol ?");
            button.setText("Start");
            i = 0;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iterateStep(i);
                    button.setText("Next");
                    protocolBegun = true;
                }
            });

            return rootView;
        }

        private void iterateStep(int i) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            final DocumentReference df = db.collection("inventory").document("inventory1");
            if (i == steps.size() - 1) {
                textView.setText("Finished");
                button.setText("End Protocol");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getActivity(), ProtocolListActivity.class));
                    }
                });
            }
            else {
                LabTask task = steps.get(i);
                if (task.getOperation().equals("addContainer")) {
                    final String type = task.getType();
                    if (task.getisNew()) {
                        textView.setText("New " + type + " " + task.getName() + " added to " + task.getSource());
                        db.runTransaction(new Transaction.Function<Void>() {
                            @Override
                            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot snapshot = transaction.get(df);
                                int newNum = snapshot.getLong(type).intValue();
                                transaction.update(df, type, newNum - 1);

                                // Success
                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Transaction success!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Transaction failure.", e);
                            }
                        });
                    }
                    else {
                        textView.setText(type + " " + task.getName() + " added to " + task.getSource());
                    }
                    String name = task.getName().replace("_"," ").concat(" ");
                    String temp = task.getSource().replace("_", " ").replace("/", " ").concat(" ");
                    labBench.put(name.toLowerCase(), temp);

                } else if (task.getOperation().equals("transfer")) {
                    textView.setText("Transfer " + task.getVol() + " uL from " + task.getSource() + " to " + task.getDest());

                    String name = task.getDest().replace("_", " ").replace("/", " ").concat(" ").toLowerCase();
                    if (labBench.get(name) == null) {
                        String temp = task.getSource();
                        labBench.put(name.toLowerCase(), temp);
                    } else {
                        String temp = labBench.get(name).concat("and " + task.getSource());
                        labBench.put(name, temp);
                    }

                    double vol = task.getVol();
                    String tubetype = "";
                    if (vol <= 20) {
                        tubetype = "P20";
                    } else if (vol <= 200) {
                        tubetype = "P200";
                    } else {
                        tubetype = "P1000";
                    }
                    final String tube = tubetype;
                    db.runTransaction(new Transaction.Function<Void>() {
                        @Override
                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot snapshot = transaction.get(df);
                            int newNum = snapshot.getLong(tube).intValue();
                            transaction.update(df, tube, newNum - 1);

                            // Success
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Transaction success!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Transaction failure.", e);
                        }
                    });
                }
                this.i += 1;

            }
        }
    }

    public static class InventoryFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private final String TAG = "InventoryFragment";
        private RecyclerView rv;
        private static InventoryAdapter adapter;

        public InventoryFragment() {
            items = new ArrayList<>();
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_inventory, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText("Inventory");

            rv = (RecyclerView) rootView.findViewById(R.id.inventory_recycler);
            LinearLayoutManager lm = new LinearLayoutManager(getActivity());
            adapter = new InventoryAdapter(items);
            rv.setLayoutManager(lm);
            rv.setAdapter(adapter);

            return rootView;
        }

        public void setup() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("inventory")
                    .document("inventory1")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    Log.d("InventoryFragment", document.getId() + " => " + document.getData());
                                    Inventory iv = document.toObject(Inventory.class);
                                    inventoryToItems(iv);
                            } else {
                                Log.d("InventoryFragment", "Error getting documents: ", task.getException());
                            }
                        }
                    });
            db.collection("inventory")
                    .document("inventory1")
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                                    ? "Local" : "Server";

                            if (protocolBegun && snapshot != null && snapshot.exists() && source.equals("Server")) {
                                Log.d(TAG, "Current data: " + snapshot.getData());
                                items.clear();
                                inventoryToItems(snapshot.toObject(Inventory.class));
                            } else {
                                Log.d(TAG, "Current data: null");
                            }
                        }
                    });

        }

        private static void inventoryToItems(Inventory iv) {
                items.add(new Pair<>("P1000", iv.getP1000()));
                items.add(new Pair<>("P200", iv.getP200()));
                items.add(new Pair<>("P20", iv.getP20()));
                items.add(new Pair<>("pcr_plate_96", iv.getPcr_plate_96()));
                items.add(new Pair<>("pcr_strip", iv.getPcr_strip()));
                items.add(new Pair<>("pcr_tube", iv.getPcr_tube()));
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return LabBenchFragment.newInstance(position);
            }
            else {
                return ivf;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }

    private void speak(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void listen(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(LabBenchActivity.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroy(){
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String inSpeech = res.get(0);
                recognition(inSpeech);
            }
        }
    }

    private void recognition (String text) {
        Log.d("Speech", ""+text);
        String[] speech = text.split(" ");
        if (text.contains("my name is")) {
            String name = speech[speech.length-1];
            speak("Your name is " + name);
        }
        //Where is alibaba oligos?
        if (text.contains("where is") || text.contains("where are")) {
            int length = speech.length;
            String name = "";
            for (int i = 2; i < speech.length; i++) {
                name += speech[i] + " ";
                Log.d("SPEECH", name);
            }
            name = name.toLowerCase().replace("-","").replace("'","");
            if (labBench.get(name) != null) {
                speak(name + " is " + "located at " + labBench.get(name));
            } else {
                speak("Sorry, couldn't find " + name);
            }
        }
        if (text.contains("what is in")) {
            int length = speech.length;
            String name = "";
            for (int i = 3; i < speech.length; i++) {
                name += speech[i] + " ";
                Log.d("SPEECH", name);
            }
            name = name.toLowerCase().replace("-","");
            if (labBench.get(name) != null) {
                speak(name + " contains " + labBench.get(name));
            } else {
                speak("Sorry, I don't know what's in " + name);
            }
        }
        //speak("You said" + text);
    }
}
