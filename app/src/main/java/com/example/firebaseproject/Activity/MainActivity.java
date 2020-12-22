package com.example.firebaseproject.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.firebaseproject.*;
import com.example.firebaseproject.Model.Message;
import com.example.firebaseproject.Adapter.*;
import com.example.firebaseproject.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private static final int RC_IMAGE = 111;

    FirebaseDatabase database;
    DatabaseReference messagesDatabaseReference;
    //Слушатель событий в потомке Reference
    ChildEventListener messagesChildEventListener;

    DatabaseReference usersDatabaseReference;
    ChildEventListener usersChildEventListener;

    FirebaseStorage storage;
    StorageReference chatImagesStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Подключаем базу данных по значению
        //https://console.firebase.google.com/project/firstproject-6b914/database/firstproject-6b914-default-rtdb/data
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        //Создаем узел для данных
        messagesDatabaseReference = database.getReference().child("messages");
        usersDatabaseReference = database.getReference().child("users");
        chatImagesStorageReference = storage.getReference().child("chat_images");


        Intent intent = getIntent();
        if(intent!=null){
            userName = intent.getStringExtra("userName");
        } else {
            userName = "Unknown";
        }
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
                Message message = new Message();
                message.setText(messageEditText.getText().toString());
                message.setName(userName);
                message.setImageUrl(null);

                //Отправка данных в базу данных с авто. сген. коду
                messagesDatabaseReference.push().setValue(message);

                //Удаляем то, что писали до этого
                messageEditText.setText("");
            }
        });

        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Создаем интенет для получения контента
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                //Все типы изображений image/*, или тип image/jpeg
                intent1.setType("image/*");
                //Только с телефона
                intent1.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent1, "Choose an image"),
                        RC_IMAGE);

            }
        });

        //Случаем узел пользователей
        usersChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Добавляется потомок
                //DataSnapshot — это список текущих значений в одном каталоге

                User user = snapshot.getValue(User.class);
                //Отображаем имя пользователя
                if(user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    userName = user.getName();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersDatabaseReference.addChildEventListener(usersChildEventListener);


        //Инициализируем обработчик событий
        messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Добавляется потомок
                //DataSnapshot — это список текущих значений в одном каталоге
                Message message = snapshot.getValue(Message.class);
                adapter.add(message);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Меняется потомок
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //Удаляется потомок
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Двигается потомок
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Ошибка в базе данных
            }
        };

        messagesDatabaseReference.addChildEventListener(messagesChildEventListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sing_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, SingInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //В результате будет адес изображение
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_IMAGE && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            //content://images/some/3 - последний элемент
            final StorageReference imageReference = chatImagesStorageReference.child(selectedImageUri
            .getLastPathSegment());

            UploadTask uploadTask = imageReference.putFile(selectedImageUri);

            uploadTask = imageReference.putFile(selectedImageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Message message = new Message();
                        message.setImageUrl(downloadUri.toString());
                        message.setName(userName);
                        messagesDatabaseReference.push().setValue(message);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }
    }
}






        /*
        FirebaseDatabase database;
        database = FirebaseDatabase.getInstance();
        DatabaseReference usersDatabaseReference;
        //Создаем узел для данных
        messagesDatabaseReference = database.getReference().child("messages");
        usersDatabaseReference = database.getReference().child("users");

        //Создаем значения для узла messages, Ключ - message1, Значение - Hello Firebase
        messagesDatabaseReference.child("message1").setValue("Hello Firebase");
        messagesDatabaseReference.child("message2").setValue("Hello world");

        usersDatabaseReference.child("user1").setValue("Joe");
        usersDatabaseReference.child("user2").setValue("Max");
        */

