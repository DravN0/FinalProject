package com.example.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.finalproject.models.Users;

import java.util.List;

public class UserAdapter extends ArrayAdapter<Users> {

    public UserAdapter(Context context, List<Users> users) {
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Users user = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView txtHeader = convertView.findViewById(android.R.id.text1);
        TextView txtSubtitle = convertView.findViewById(android.R.id.text2);

        txtHeader.setText(user.getUser().getName());
        txtSubtitle.setText(user.getUser().getUserType());

        return convertView;
    }
}
