package com.example.firebaseproject.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firebaseproject.Model.Message;
import com.example.firebaseproject.R;

import java.util.List;

public class Adapter extends ArrayAdapter<Message> {

    private List<Message> messages;
    private Activity activity;

    public Adapter(Activity context, int resource, List<Message> messageList) {
        super(context, resource, messageList);
        this.messages = messageList;
        this.activity = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        LayoutInflater layoutInflater = (LayoutInflater)activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        Message message = getItem(position);
        int layoutResource = 0;
        int viewType = getItemViewType(position);


        if(convertView == null){
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.message_item,
                    parent, false);
        }

        ImageView photoImageView = convertView.findViewById(R.id.photoImageView);
        TextView textTextView = convertView.findViewById(R.id.textTextView);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);

        Message messageObject = getItem(position);

        boolean isText = message.getImageUrl() == null;

        if(isText) {

            textTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            textTextView.setText(message.getText());

        } else {

            textTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext())
                    .load(message.getImageUrl())
                    .into(photoImageView);
        }

        nameTextView.setText(message.getName());

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {

        int flag;
        Message message = messages.get(position);
        if (message.isMine()){
            flag = 0;
        } else flag = 1;
        return flag;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class ViewHolder {

        private ImageView photoImageView;
        private TextView messageTextView;

        public ViewHolder(View view){
            photoImageView = view.findViewById(R.id.photoImageView);
            messageTextView = view.findViewById(R.id.messageTextView);
        }
    }

}
