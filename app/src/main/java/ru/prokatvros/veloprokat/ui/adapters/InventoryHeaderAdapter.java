package ru.prokatvros.veloprokat.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Inventory;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class InventoryHeaderAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private LayoutInflater inflater;
    private List<Inventory> itemList;
    private Map<Long, Inventory> headersMap;
    private Context context;

    public InventoryHeaderAdapter(Context context, List<Inventory> inventoryList) {
        inflater = LayoutInflater.from(context);
        this.itemList = createItemList(inventoryList);
        this.context = context;

    }

    private List<Inventory> createItemList(List<Inventory> inventoryList) {
        List<Inventory> itemList = new ArrayList<>();
        headersMap = new HashMap<>();

        for(Inventory inventory : inventoryList) {
            if (inventory.idParent != 0)
                itemList.add(inventory);
            /*else
                headersMap.put(Long.valueOf(inventory.serverId), inventory);*/
        }

        List<Inventory> listInventoryRoot = Inventory.getRoot();
        for (Inventory inventory : listInventoryRoot) {
            headersMap.put(Long.valueOf(inventory.serverId), inventory);
        }

        return itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();/*countries.length;*/
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position); /*countries[position];*/
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).idParent;
    }

    @Override 
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_inventory, parent, false);

            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvNumber = (TextView) convertView.findViewById(R.id.tvNumber);
            holder.tvState = (TextView) convertView.findViewById(R.id.tvState);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Inventory inventory = itemList.get(position);



        if (inventory.state == Inventory.MISSING_STATE)
            convertView.setBackgroundColor(context.getResources().getColor(R.color.missing_color));
        else if (inventory.state == Inventory.REFIT_STATE)
            convertView.setBackgroundColor(context.getResources().getColor(R.color.refit_color));
        else if (inventory.state == Inventory.RENTED_STATE)
            convertView.setBackgroundColor(context.getResources().getColor(R.color.in_rent_color));
        else if (inventory.state == Inventory.FREE_STATE)
            convertView.setBackgroundColor(context.getResources().getColor(R.color.free_color));

        holder.tvName.setText(inventory.model);
        holder.tvNumber.setText(inventory.number);
        holder.tvState.setText(Inventory.getStateStringFormat(context, inventory.state));
        return convertView;
    }

    @Override 
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.header_item_inventory, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.tvHeader);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        //set header text as first char in name
        //String headerText = "" + itemList.get(position).model;//countries[position].subSequence(0, 1).charAt(0);
        String headerText = "" + headersMap.get(itemList.get(position).idParent).model;
        holder.text.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        //Long idParent = itemList.get(position).idParent;


        return Long.valueOf(headersMap.get(itemList.get(position).idParent).serverId);
        //countries[position].subSequence(0, 1).charAt(0);
    }

    class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {

        TextView tvName;
        TextView tvNumber;
        TextView tvState;

    }

}