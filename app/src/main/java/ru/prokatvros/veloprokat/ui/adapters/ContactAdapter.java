package ru.prokatvros.veloprokat.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Client;

// public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {

    private static final int TYPE_CONTACT = 0;
    private static final int TYPE_PASSPORT = 1;

    Context context;
    protected static List<ContactItem> sideMenuItems;

    public ContactAdapter (Context context, Client client) {
        this.context = context;
        sideMenuItems = new ArrayList();

        if ( client.phone!=null && !client.phone.isEmpty() )
            sideMenuItems.add( new ContactItem(R.mipmap.ic_action_call, client.phone, context.getString(R.string.phone)) );

        if ( client.hasVipNumber() )
            sideMenuItems.add( new ContactItem( R.mipmap.ic_vip, client.vipNumber, context.getString(R.string.vip) ) );

        if ( client.blackList == 1 )
            sideMenuItems.add( new ContactItem( R.mipmap.ic_black_list, context.getString(R.string.present_in), context.getString(R.string.black_list_text) ) );

    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {
            case TYPE_CONTACT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
                return new ContactHolder(view);

            case TYPE_PASSPORT:
                throw new Error(" TYPE_PASSPORT temporeraly is unavialable !!!");
            default:
                throw new Error("Was used unavialable typa !!!");

        }
    }

    @Override
    public void onBindViewHolder( ContactHolder holder, int position ) {
        holder.fillHolder(position);
    }

    @Override
    public int getItemCount() {
        return sideMenuItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_CONTACT;
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        protected int holderId;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        protected abstract void fillHolder(int position);
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView text;
        TextView description;

        public ContactHolder(View itemView) {
            super(itemView);
            //holderId = 1;

            icon = (ImageView) itemView.findViewById(R.id.ivIcon);
            text = (TextView) itemView.findViewById(R.id.tvText);
            description = (TextView) itemView.findViewById(R.id.tvDescription);
        }

        protected void fillHolder(int position) {
            ContactItem contactItem = sideMenuItems.get(position);

            icon.setImageResource( contactItem.iconId );
            text.setText(contactItem.text);
            description.setText( contactItem.description );
        }

    }

    public class PassportHolder extends ViewHolder {

        TextView serialsAnNumber;

        TextView whoIssued;

        TextView dateIssued;

        TextView subdivision;

        public PassportHolder(View itemView) {
            super(itemView);
            holderId = 0;
        }

        @Override
        protected void fillHolder(int position) {

        }

    }

    public static final class ContactItem {

        private int iconId;
        private String text;
        private String description;

        private ContactItem(int iconId, String text, String description) {
            this.iconId = iconId;
            this.text = text;
            this.description = description;
        }

    }

    public static final class PassportItem {

        private String series;

        private String number;

        private String whoIssued;

        private String dateIssued;

        private String subdivision;

        private PassportItem ( String series,
                               String number,
                               String whoIssued,
                               String dateIssued,
                               String subdivision ) {

            this.series = series;
            this.number = number;
            this.whoIssued = whoIssued;
            this.dateIssued = dateIssued;
            this.subdivision = subdivision;

        }

    }

}
