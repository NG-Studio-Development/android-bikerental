package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.prokatvros.veloprokat.ConstantsBikeRentalApp;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Inventory;
import ru.prokatvros.veloprokat.model.requests.FilesRequest;
import ru.prokatvros.veloprokat.model.requests.InventoryRequest;
import ru.prokatvros.veloprokat.model.requests.PostResponseListener;
import ru.prokatvros.veloprokat.ui.activities.InventoryActivity;
import ru.prokatvros.veloprokat.utils.BitmapUtils;
import ru.prokatvros.veloprokat.utils.FileUtils;

public class InventoryFragment extends BaseFragment<InventoryActivity> {

    private static final String ARG_INVENTORY = "inventory";

    protected Inventory inventory;
    protected Map<String, Integer> keyStateMap;

    protected ImageView ivAvatar;

    File file;

    public static InventoryFragment newInstance(Inventory inventory) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_INVENTORY, inventory);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keyStateMap = new HashMap<>();

        keyStateMap.put(getString(R.string.item_rented), Inventory.RENTED_STATE);
        keyStateMap.put(getString(R.string.item_missing), Inventory.MISSING_STATE);
        keyStateMap.put(getString(R.string.item_refit), Inventory.REFIT_STATE);
        keyStateMap.put(getString(R.string.item_free), Inventory.FREE_STATE);

        if (getArguments() != null) {
            inventory = getArguments().getParcelable(ARG_INVENTORY);
        }

        if ( getHostActivity().getSupportActionBar() != null && inventory != null) {
            getHostActivity().getSupportActionBar().setTitle(inventory.model);
            getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_inventory;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        TextView tvNumber = (TextView) view.findViewById(R.id.tvNumber);
        TextView tvNumberFrame = (TextView) view.findViewById(R.id.tvNumberFrame);
        TextView tvCount = (TextView) view.findViewById(R.id.tvRentsCount);
        TextView tvCost = (TextView) view.findViewById(R.id.tvCost);
        ivAvatar = (ImageView) view.findViewById(R.id.ivAvatar);

        if ( inventory.hasAvatar() ) {
            ImageLoader.getInstance().displayImage(ConstantsBikeRentalApp.URL_SERVER+"/"+inventory.avatar, ivAvatar);
        }

        Button buttonAddPhoto = (Button) view.findViewById(R.id.buttonAddPhoto);
        Button buttonSave = (Button) view.findViewById(R.id.buttonSave);
        Spinner spinnerState = (Spinner) view.findViewById(R.id.spinnerState);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().getProgressDialog().show();
                Volley.newRequestQueue(getHostActivity()).add(requestPutInventory(inventory));
            }
        });

        buttonAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto();
            }
        });

        final ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, getSinnerItem() );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(adapter);
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                inventory.state = keyStateMap.get(getSinnerItem().get(position));
                inventory.save();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        setHasOptionsMenu(true);

        if ( inventory != null ) {
            tvName.setText(inventory.model);
            tvNumber.setText(inventory.number);
            tvNumberFrame.setText(inventory.numberFrame);


            tvCost.setText(String.valueOf(getString(R.string.hour)+": "+inventory.tarif.sumHour +"\n"+
                                            getString(R.string.day)+": "+inventory.tarif.sumDay+"\n"+
                                            getString(R.string.th_hour)+": "+inventory.tarif.sumTsHour) );
            tvCount.setText(String.valueOf(inventory.countRents));

            if ( inventory.state != Inventory.RENTED_STATE ) {
                spinnerState.setVisibility(View.VISIBLE);
                spinnerState.setSelection(getSinnerItem().indexOf(getKeyByObject(inventory.state) ) );
            }
        }

        return view;
    }

    protected InventoryRequest requestPutInventory(Inventory inventory) {
        return InventoryRequest.requestPutInventory(inventory, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getHostActivity().getProgressDialog().hide();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getHostActivity().getProgressDialog().hide();
            }
        });
    }

    protected List<String> getSinnerItem() {
        List<String> list = new ArrayList<>();

        list.add(getString(R.string.item_free));
        list.add(getString(R.string.item_missing));
        list.add(getString(R.string.item_refit));
        list.add(getString(R.string.item_rented));

        return list;
    }

    protected String getKeyByObject(Integer value) {
        for (Map.Entry<String, Integer> e : keyStateMap.entrySet())
            if ( value == e.getValue() )
                return e.getKey();

        throw new Error("Set unavialable object in map !!!");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /* switch (item.getItemId()) {
            case android.R.id.home:
                getHostActivity().onBackPressed();
                break;
        } */

        return super.onOptionsItemSelected(item);
    }

    public void capturePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(FileUtils.getFilesDir(), Calendar.getInstance().getTimeInMillis()+".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        if ( intent.resolveActivity( getActivity().getPackageManager() ) != null ) {
            startActivityForResult(intent, ConstantsBikeRentalApp.REQUEST_CODE_CAPTURE_IMAGE);
        }
    }

    private void wasChanged() {

    }

    private void postImageToServer(String base64) {
        FilesRequest request = FilesRequest.requestPostImage(base64, new PostResponseListener() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int errorCode = jsonResponse.getInt("error_code");

                    if (errorCode != 0) {
                        Toast.makeText(getHostActivity(), "Can't send image to server", Toast.LENGTH_LONG).show();
                        return;
                    }

                    inventory.avatar = jsonResponse.getString("url");
                    ImageLoader.getInstance().displayImage(ConstantsBikeRentalApp.URL_SERVER+"/"+inventory.avatar, ivAvatar);
                    wasChanged();

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(getHostActivity()).add(request);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if ( msg.what == ConstantsBikeRentalApp.BASE_64_IMAGE_HANDLER ) {
                if (base64Image != null) {
                    postImageToServer(base64Image);
                    //buttonAddPhoto.setText(getString(R.string.photo_was_added));
                }

            }
        }
    };

    String base64Image;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            final Uri uri;

            if (requestCode == ConstantsBikeRentalApp.REQUEST_CODE_CAPTURE_IMAGE)
                uri = Uri.fromFile(file);
            else
                return;


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Bitmap avatarImage = BitmapUtils.decodeUri(getActivity(), uri, BitmapUtils.DESIRED_SIZE, BitmapUtils.DESIRED_SIZE, BitmapUtils.DecodeType.BOTH_SHOULD_BE_EQUAL_CUT);
                        base64Image = BitmapUtils.convertBitmapToBase64(avatarImage, false);
                        handler.sendMessage( handler.obtainMessage( ConstantsBikeRentalApp.BASE_64_IMAGE_HANDLER) );

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


}
