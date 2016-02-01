package ru.prokatvros.veloprokat.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Message;

public class ChatAdapter extends ArrayAdapter<Message> {

    private int resource;

    public ChatAdapter(Context context, int resource, List<Message> list) {
        super(context, resource, list);
        this.resource = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
            holder = initHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        Message message = getItem(position);

        holder.tvText.setText(message.message);

        if (message.admin != null && message.admin.name != null) {
            holder.tvName.setText(message.admin.name);
            ImageLoader.getInstance().displayImage(message.admin.getAvatarUrl(), holder.ivAvatar);
        }


        return convertView;
    }

    private Holder initHolder(View convertView) {
        Holder holder = new Holder();
        holder.tvText = (TextView) convertView.findViewById(R.id.tvText);
        holder.tvName = (TextView) convertView.findViewById(R.id.tvPhone);
        holder.ivAvatar = (ImageView) convertView.findViewById(R.id.ivAvatar);
        return holder;
    }

    class Holder {
        ImageView ivAvatar;
        TextView tvText;
        TextView tvName;
    }
}
