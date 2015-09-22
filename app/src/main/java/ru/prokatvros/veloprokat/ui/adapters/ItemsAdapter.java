package ru.prokatvros.veloprokat.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.ui.activities.ProfileActivity;



public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private static final MenuItem[] sideMenuItems = { new MenuItem(R.string.inventory),
            new MenuItem(R.string.rents),
            new MenuItem(R.string.chat),
            new MenuItem(R.string.breakdowns),
            new MenuItem(R.string.clients) };

    // new MenuItem(R.drawable.drawable_item_menu_about, R.string.text_item_menu_about)

    private Context context;
    private String name;
    //private String email;

    private View previousView;

    OnItemClickListener onItemClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int holderId;

        TextView textView;
        ImageView ivAvatar;
        TextView tvName;

        public ViewHolder(View itemView,int ViewType) {
            super(itemView);

            if (ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.tvItem);
                //imageView = (ImageView) itemView.findViewById(R.id.ivIcon);
                holderId = 1;
            } else {
                tvName = (TextView) itemView.findViewById(R.id.name);
                //tvEmail = (TextView) itemView.findViewById(R.id.email);
                ivAvatar = (ImageView) itemView.findViewById(R.id.ivAvatar);
                holderId = 0;
            }
        }
    }

    public ItemsAdapter(Context context, String name, OnItemClickListener onItemClickListener) {
        this(context, name);
        this.onItemClickListener = onItemClickListener;
    }

    public ItemsAdapter(Context context, String name) {
        this.context = context;
        this.name = name;

    }

    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu,parent,false); //Inflating the layout

            final ViewHolder viewHolderItem = new ViewHolder(view,viewType); //Creating ViewHolder and passing the object of type view
            viewHolderItem.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null) {
                        onItemClickListener.onItemClick(viewHolderItem.getAdapterPosition() - 1);

                        if (previousView != null)
                            previousView.setSelected(false);
                        previousView = view;

                        view.setSelected(true);


                    }
                }
            });
            return viewHolderItem;

        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_drawer,parent,false);
            ViewHolder vhHeader = new ViewHolder(v,viewType);
            return vhHeader;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ItemsAdapter.ViewHolder holder, int position) {

        if (holder.holderId == 1) {
            holder.textView.setText(sideMenuItems[position - 1].textResourceId);
            //holder.imageView.setImageResource(sideMenuItems[position - 1].iconId);
        } else if(holder.holderId == 0){
            //WhereAreYouApplication.getInstance()
                    //.getAvatarCache().displayImage(AvatarBase64ImageDownloader.getImageUriFor(WhereAreYouApplication.getInstance().getUserName()),holder.ivAvatar);

            holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileActivity.startProfileActivity(context);
                    //ProfileActivity.startProfileActivity(context, null);
                }
            });
            holder.ivAvatar.setImageResource(R.mipmap.ic_launcher);
            holder.tvName.setText(name);
            //holder.tvEmail.setText(email);
        }
    }

    @Override
    public int getItemCount() {
        return sideMenuItems.length+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }


    public MenuItem getItem(int position) {
        return sideMenuItems[position];
    }

    public static final class MenuItem {

        //private int iconId;
        private int textResourceId;
        private String text;

        private MenuItem(/*int iconId,*/ int textResourceId) {
            //this.iconId = iconId;
            this.textResourceId = textResourceId;
        }

        public MenuItem(/*int iconId,*/ String text) {
            this.text = text;
            //this.iconId = iconId;
        }

        /*public int getIconId() {
            return iconId;
        }*/
    }

     public interface OnItemClickListener {
        public void onItemClick(int position);
     }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}
