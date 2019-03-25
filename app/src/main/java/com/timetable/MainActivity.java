package com.timetable;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Script;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.io.Serializable;


public class MainActivity extends AppCompatActivity {
    @IgnoreExtraProperties
    static class Item implements Serializable{

        public String position_lesson;
        public String name_lesson;
        public String room_lesson;

        public Item() {
        }

        Item(String position, String name, String room) {
            this.position_lesson = position;
            this.name_lesson = name;
            this.room_lesson = room;
        }

    }

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String day_of_week;
    private  LinearLayout ll;
    private EditText week, name, price;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date currentDate = new Date();
        SimpleDateFormat dateFormat= null;
        dateFormat = new SimpleDateFormat("E",Locale.ENGLISH);
        day_of_week = dateFormat.format(currentDate);

        database= FirebaseDatabase.getInstance();
        //database.setPersistenceEnabled(true);

        myRef = database.getReference("timetable").child("day").child(day_of_week);
        week = (EditText) findViewById(R.id.et_day_week);
        name = (EditText) findViewById(R.id.name);
        price = (EditText) findViewById(R.id.et_room_lesson);
        final Button add = (Button) findViewById(R.id.add);
        final Button push = (Button) findViewById(R.id.push);

        final EditText position_of_lesson = (EditText) findViewById(R.id.position);
        final ListView items = (ListView) findViewById(R.id.items);
        final ItemsAdapter adapter = new ItemsAdapter();
        ll = (LinearLayout) findViewById(R.id.curator_editor);

        Log.v("OnCreate", "Oncreate");
        ll.setVisibility(LinearLayout.GONE);

        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item = new Item(position_of_lesson.getText().toString(),name.getText().toString(), price.getText().toString());
                //   adapter.add(item);
                myRef.push().setValue(item);
                //changeDay(week.getText().toString(),Integer.parseInt(position_of_lesson.getText().toString()),name.getText().toString(),price.getText().toString());
            }
        });

        items.setAdapter(adapter);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item = new Item(position_of_lesson.getText().toString(),name.getText().toString(), price.getText().toString());
                //   adapter.add(item);
                //myRef.push().setValue(item);
                changeDay(week.getText().toString(),Integer.parseInt(position_of_lesson.getText().toString()),name.getText().toString(),price.getText().toString());
            }
        });

          Query myQuery = myRef;

        myQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Item item = dataSnapshot.getValue(Item.class);
                adapter.add(item);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void testing(){
        Log.v("test","ddfd");
    }

    private void showAll(){
        final ListView items = (ListView) findViewById(R.id.items);
        final ItemsAdapter adapter = new ItemsAdapter();


        items.setAdapter(adapter);
        adapter.clear();
        myRef=database.getReference("timetable").child("day");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = null;
                Item item;
                String current_day = null;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                        key = dataSnapshot.getKey();
                        if (!key.equals(current_day) ){
                            current_day = key;
                            item = new Item("",key, "");
                            adapter.add(item);
                        }

                        item = new Item(datas.child("position_lesson").getValue().toString(),datas.child("name_lesson").getValue().toString(), datas.child("room_lesson").getValue().toString());
                        adapter.add(item);
                    }
                }
                else Log.v("ShowAll ", "child not exist");

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void changeDay(String week, final int pos_less, final String name_less, final String room_less){
            myRef = database.getReference("timetable").child("day").child(week);

            myRef.orderByChild("position_lesson").equalTo(String.valueOf(pos_less)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String key = null;
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot datas : dataSnapshot.getChildren()) {
                            key = datas.getKey();
                            String name = datas.child("name_lesson").getValue().toString();
                            String room = datas.child("room_lesson").getValue().toString();
                            String pos = datas.child("position_lesson").getValue().toString();
                            Log.v(" Change Key is = ", key);
                            Log.v(" Change name is = ", name);
                            Log.v(" Change room is = ", room);
                            Log.v(" Change position is = ", pos);
                        }
                        myRef.child(key).child("room_lesson").setValue(room_less);
                        myRef.child(key).child("name_lesson").setValue(name_less);
                        

                    } else  {
                        Toast.makeText(MainActivity.this, "Пары с номером "+pos_less+" не существует ",
                                Toast.LENGTH_SHORT).show();
                        Log.v("ChangeDay : ","No exist");
                    }

                    //Item item = new Item(9,"CHANGED",228);
                    //String key = dataSnapshot.getKey();
                    //myRef.child("day").child(key).setValue(item);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private class ItemsAdapter extends ArrayAdapter<Item> {
        ItemsAdapter() {
            super(MainActivity.this, R.layout.item);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final View view = getLayoutInflater().inflate(R.layout.item, null);
            final Item item = getItem(position);
            ((TextView) view.findViewById(R.id.name)).setText(item.name_lesson);
            ((TextView) view.findViewById(R.id.price)).setText(String.valueOf(item.room_lesson));
            ((TextView) view.findViewById(R.id.position_lesson)).setText(String.valueOf(item.position_lesson));
            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.itm_week_timetable:
                showAll();
                return true;
            case R.id.itm_log_in:
                Log.v("onOptionMenu ","loging");
                Intent intent = new Intent(MainActivity.this, LogIn.class);
                startActivityForResult(intent,1);
                //ll.setVisibility(LinearLayout.GONE);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("OnActivityRedult ", "I am here");
        if (data == null) {return;}

        String str = data.getStringExtra("streetkey");
        if (str.equals("ok")) {
            Log.v("OnActivityRedult ", str);
            ll.setVisibility(LinearLayout.VISIBLE);

        }
    }
}