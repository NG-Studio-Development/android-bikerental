package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import ru.prokatvros.veloprokat.BikerentalApplication;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Admin;
import ru.prokatvros.veloprokat.model.db.Point;
import ru.prokatvros.veloprokat.model.db.Rent;
import ru.prokatvros.veloprokat.model.db.Shift;
import ru.prokatvros.veloprokat.model.requests.LoadAllDataRequest;
import ru.prokatvros.veloprokat.model.requests.PostResponseListener;
import ru.prokatvros.veloprokat.model.requests.ShiftRequest;
import ru.prokatvros.veloprokat.ui.activities.LoginActivity;
import ru.prokatvros.veloprokat.ui.activities.ProfileActivity;
import ru.prokatvros.veloprokat.utils.DataParser;


public class ProfileFragment extends BaseFragment<ProfileActivity> {

    private static final String TAG = "PROFILE_FRAGMENT";

    private static final int REQUEST_CODE_CHOOSE_IMAGE = 2;
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 3;

    private static final float ALPHA_DISABLE = 0.5f;

    ImageButton avatarMenu, delete, capture, gallery;
    LinearLayout profileButtons;
    RelativeLayout contactData;

    ImageView avatar;
    TextView profileName;

    TextView contactName;
    TextView contactEmail;
    //ContactStep currentContact;
    File file;
    List<Shift> shiftList;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getHostActivity().getSupportActionBar().hide();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_profile;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ImageView ivAvatar = (ImageView) view.findViewById(R.id.ivAvatar);
        ImageButton ibBack = (ImageButton) view.findViewById(R.id.ibBack);
        TextView tvProfileName = (TextView) view.findViewById(R.id.tvProfileName);
        final Button buttonSend = (Button) view.findViewById(R.id.buttonSend);
        final Button buttonShifts = (Button) view.findViewById(R.id.buttonShifts);
        final Button buttonDeliver = (Button) view.findViewById(R.id.buttonDeliver);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().onBackPressed();
            }
        });

        Admin admin = BikerentalApplication.getInstance().getAdmin();

        ivAvatar.setImageResource(R.mipmap.ic_launcher);

        ImageLoader.getInstance().displayImage(admin.getAvatarUrl(), ivAvatar);

        tvProfileName.setText(admin.name);

        String dataFromPool = DataParser.getInstance(getHostActivity()).loadDataFromPool();

        if ( dataFromPool.isEmpty() )
            disableSendButton(buttonSend);

        final LoadAllDataRequest request = LoadAllDataRequest.saveRequestAllData(dataFromPool, new PostResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data = new JSONObject(response);
                    data = data.getJSONObject("data");

                    Gson gson = new Gson();
                    List<Rent> rentList = gson.fromJson(data.getJSONArray("rents").toString(), new TypeToken<List<Rent>>(){}.getType());

                    for ( Rent rent : rentList ) {
                        rent.save();
                    }


                    Log.d(TAG, "Save request all data SUCCESS");
                    Toast.makeText(getHostActivity(), "Save request all data SUCCESS", Toast.LENGTH_LONG).show();
                    DataParser.getInstance(getHostActivity()).clear();
                    //disableSendButton(buttonSend);


                } catch (JSONException ex) {
                    ex.printStackTrace();
                }


            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Save request all data ERROR");
                //Toast.makeText(getHostActivity(), "Save request all data ERROR", Toast.LENGTH_LONG).show();
            }
        });


        requestShifts(new PostResponseListener() {
            @Override
            public void onResponse(String response) {
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

                shiftList = gson.fromJson(response, new TypeToken<List<Shift>>() {
                }.getType());

                for (Shift shift : shiftList) {
                    shift.point = Point.getByServerId("1");
                    shift.save();
                }

                buttonShifts.setText( getString(R.string.count_shifts)+": "+shiftList.size() );

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        buttonDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestShiftsDeliver ();
            }
        });

        buttonShifts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shiftList != null) {
                    getHostActivity().replaceFragment(ListShiftFragment.newInstance(shiftList), true);
                }
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Volley.newRequestQueue(getHostActivity()).add(request);
                disableSendButton(buttonSend);
            }
        });

        return view;
    }

    protected void disableSendButton(Button buttonSend) {
        buttonSend.setText(getString(R.string.notice_sanding_is_all_send));
        buttonSend.setEnabled(false);
        buttonSend.setAlpha(ALPHA_DISABLE);
    }

    protected void requestShifts ( PostResponseListener listener ) {
        Admin admin = BikerentalApplication.getInstance().getAdmin();
        ShiftRequest request = ShiftRequest.requestAllAdminShift(admin, listener);
        Volley.newRequestQueue(getHostActivity()).add(request);
    }

    protected void requestShiftsDeliver () {

        Shift shift = BikerentalApplication.getInstance().getShift();
        shift.state = Shift.STATE_DELIVERED;
        shift.save();

        ShiftRequest request = ShiftRequest.requestDeliverShift(shift, new PostResponseListener() {
            @Override
            public void onResponse(String response) {
                //BikerentalApplication.getInstance().logout();

                BikerentalApplication.getInstance().getApplicationPreferencesEditor().clear();
                BikerentalApplication.getInstance().getApplicationPreferencesEditor().commit();
                Intent intent = new Intent(getHostActivity(), LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        Volley.newRequestQueue(getHostActivity()).add(request);
    }

}