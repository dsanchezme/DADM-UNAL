package com.dadm.reto07;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PlayersListAdapter extends ArrayAdapter<String> {

    private int layout;
    private PlayersListAdapter(Context context, int resource, List<String> objects){
        super(context, resource, objects);
        layout = resource;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mainViewHolder = null;
        if  (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView= inflater.inflate(layout, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.playerName = (TextView) convertView.findViewById(R.id.playerName);
            viewHolder.inviteButton = (Button) convertView.findViewById(R.id.inviteButton);
            viewHolder.inviteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "Hello to " + position, Toast.LENGTH_SHORT).show();
                }
            });
            convertView.setTag(viewHolder);
        }else{
            mainViewHolder = (ViewHolder) convertView.getTag();
            mainViewHolder.playerName.setText(getItem(position));
        }

        return convertView;
    }
}
