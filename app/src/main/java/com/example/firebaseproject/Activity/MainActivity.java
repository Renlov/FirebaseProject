package com.example.firebaseproject.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.firebaseproject.*;
import com.example.firebaseproject.Model.Message;
import com.example.firebaseproject.Adapter.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView messageListView;
    private Adapter adapter;
    private ProgressBar progressBar;
    private ImageButton sendImageButton;
    private Button sendMessageButton;
    private EditText messageEditText;
    private List<Message> messageList;

    private String userName;

    FirebaseDatabase database;
    DatabaseReference messagesDatabaseReference;
    DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Подключаем базу данных по значению
        //https://console.firebase.google.com/project/firstproject-6b914/database/firstproject-6b914-default-rtdb/data
        database = FirebaseDatabase.getInstance();

        //Создаем узел для данных
        messagesDatabaseReference = database.getReference().child("messages");
        usersDatabaseReference = database.getReference().child("users");

        //Создаем значения для узла messages, Ключ - message1, Значение - Hello Firebase
        messagesDatabaseReference.child("message1").setValue("Hello Firebase");
        messagesDatabaseReference.child("message2").setValue("Hello world");

        usersDatabaseReference.child("user1").setValue("Joe");
        usersDatabaseReference.child("user2").setValue("Max");

        userName = "Unknown";

        progressBar = findViewById(R.id.progressBar);
        sendImageButton = findViewById(R.id.sendPhotoButton);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        messageEditText = findViewById(R.id.massageEditText);
        messageListView = findViewById(R.id.listView);

        messageList = new ArrayList<>();
        adapter = new Adapter(this, R.layout.message_item, messageList);
        messageListView.setAdapter(adapter);

        progressBar.setVisibility(ProgressBar.INVISIBLE);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                //Если вводим текст, то кнопка отправки становиться видимой
                if(s.toString().trim().length() > 0 ){
                    sendMessageButton.setEnabled(true);
                //Иначе кнопка будет не активной
                } else {
                    sendMessageButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //Ограничение по вводимому сообщению
        messageEditText.setFilters(new InputFilter[]
                {new InputFilter.LengthFilter(500)});

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageEditText.setText("");
            }
        });

        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}