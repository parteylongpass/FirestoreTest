package com.example.firestoretest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivityForFirestoreTest extends AppCompatActivity {
    ArrayList<Stuff> dataSet;

    //검색 기능 구현
    EditText searchET;
    ArrayList<Stuff> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("YYC", "onCreate");
        setContentView(R.layout.activity_main_for_firestore_test);

        dataSet = new ArrayList<>();

        //activity_main.xml에 만든 RecyclerView와 연결
        RecyclerView recyclerView = findViewById(R.id.recycler_view_rv);

        //LayoutManager 객체 생성 후 RecylcerView 객체와 연결
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        //CustomAdapter 객체 생성 후 RecyclerView 객체와 연결
        CustomAdapter customAdapter = new CustomAdapter(dataSet);
        recyclerView.setAdapter(customAdapter);

        //Firestore 이용
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("stuff").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("TAG", "Listen failed.");
                    return;
                }

                if(value != null) {
                    dataSet.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Stuff stuff = snapshot.toObject(Stuff.class);
                        if (stuff != null) {
                            dataSet.add(stuff);
                        }
                    }
                    customAdapter.notifyDataSetChanged();
                }
            }
        });


        //검색 기능 구현
        searchET = findViewById(R.id.search_tv);
        filteredList = new ArrayList<>();

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = searchET.getText().toString();
                filteredList.clear();

                if(searchText.equals("")){
                    customAdapter.setItems(dataSet);
                }
                else {
                    // 검색 단어를 포함하는지 확인
                    for (int i = 0; i < dataSet.size(); i++) {
                        if (dataSet.get(i).getName().toLowerCase().contains(searchText.toLowerCase())) {
                            filteredList.add(dataSet.get(i));
                        }
                        customAdapter.setItems(filteredList);
                    }
                }
            }
        });

    }
}