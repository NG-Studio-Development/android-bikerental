package ru.prokatvros.veloprokat.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import ru.prokatvros.veloprokat.ConstantsBikeRentalApp;
import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.model.db.Client;
import ru.prokatvros.veloprokat.model.requests.ClientRequest;
import ru.prokatvros.veloprokat.model.requests.FilesRequest;
import ru.prokatvros.veloprokat.model.requests.PostResponseListener;
import ru.prokatvros.veloprokat.ui.activities.ClientActivity;
import ru.prokatvros.veloprokat.utils.BitmapUtils;
import ru.prokatvros.veloprokat.utils.FileUtils;

public class AddClientFragment extends BaseFragment {

    private final static String TAG = "ADD_CLIENT_FRAGMENT";

    protected int sendAction = SEND_ADD_CLIENT;

    File file;
    Client client;
    String base64Image;
    Button buttonAddPhoto;

    EditText etName;// = (EditText) view.findViewById(R.id.etName);
    EditText etSurname;// = (EditText) view.findViewById(R.id.etSurname);
    EditText etPhone;// = (EditText) view.findViewById(R.id.etSearch);
    EditText etNumber;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if ( msg.what == ConstantsBikeRentalApp.BASE_64_IMAGE_HANDLER ) {
                if (base64Image != null) {
                    postImageToServer(base64Image);
                    buttonAddPhoto.setText(getString(R.string.photo_was_added));
                }

            }
        }
    };

    private final static String ARG_NUMBER = "arg_number";

    private final static String ARG_CLIENT = "arg_client";

    public static AddClientFragment newInstance(String number) {
        AddClientFragment fragment = new AddClientFragment ();
        Bundle args = new Bundle();
        args.putString(ARG_NUMBER, number);
        fragment.setArguments(args);

        return fragment;
    }

    public static AddClientFragment newInstance(Client client) {
        AddClientFragment fragment = new AddClientFragment ();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CLIENT, client);
        fragment.setArguments(args);

        return fragment;
    }

    public AddClientFragment() {}

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_add_client;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getHostActivity().getSupportActionBar() != null) {
            getHostActivity().getSupportActionBar().setTitle("Name");
            getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_client, container, false);
        Button buttonAdd = (Button) view.findViewById(R.id.buttonAdd);
        buttonAddPhoto = (Button) view.findViewById(R.id.buttonAddPhoto);

        etName = (EditText) view.findViewById(R.id.etName);
        etSurname = (EditText) view.findViewById(R.id.etSurname);
        etPhone = (EditText) view.findViewById(R.id.etSearch);
        etNumber = (EditText) view.findViewById(R.id.etNumber);



        if ( getArguments() != null ) {

            if (getArguments().getParcelable(ARG_CLIENT) != null) {
                client = getArguments().getParcelable(ARG_CLIENT);
                fillField( client );
                buttonAdd.setText(getString(R.string.save));
                sendAction = SEND_UPDATE_CLIENT;
            } else {
                client = new Client();
            }

            String phone = getArguments().getString(ARG_NUMBER);
            if (phone != null)
                etPhone.setText(phone);
        }

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClient( etName.getText().toString(),
                        etSurname.getText().toString(),
                        etPhone.getText().toString(),
                        etNumber.getText().toString(),  sendAction);
            }
        });


        buttonAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto();
            }
        });

        return view;
    }


    protected void fillField(Client client) {
        etName.setText(client.name);
        etSurname.setText(client.surname);
        etPhone.setText(client.phone);

        if (client.hasVipNumber())
            etNumber.setText(client.vipNumber);
    }

    public void capturePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(FileUtils.getFilesDir(), Calendar.getInstance().getTimeInMillis()+".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        if ( intent.resolveActivity( getActivity().getPackageManager() ) != null ) {
            startActivityForResult(intent, ConstantsBikeRentalApp.REQUEST_CODE_CAPTURE_IMAGE);
        }
    }

    protected static final int SEND_UPDATE_CLIENT = 0;
    protected static final int SEND_ADD_CLIENT = 1;

    private void addClient( String name, String surname, String phone, String vipNumber, int actionSend ) {

        if ( name.isEmpty() || surname.isEmpty() || phone.isEmpty() ) {
            Toast.makeText( getHostActivity(), getString(R.string.warning_can_not_have_empty_field), Toast.LENGTH_LONG ).show();
            return;
        }

        client.name = name;
        client.surname = surname;
        client.phone = phone;

        if (vipNumber != null && !vipNumber.isEmpty() )
            client.vipNumber = vipNumber;

        client.save();

        if (actionSend == SEND_ADD_CLIENT) {
            reqPostClient(client);
        } else if (actionSend == SEND_UPDATE_CLIENT) {
            reqUpdateClient(client);
        }
    }

    protected void reqUpdateClient(Client client) {
        ClientRequest clientRequest = ClientRequest.requestPutClient(client, new PostResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Put response: " + response);
                int errorCode;
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    errorCode = jsonResponse.getInt("error_code");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    return;
                }

                if (errorCode != ClientRequest.CODE_NOT_ERROR_POST_CLIENT) {
                    Toast.makeText(getHostActivity(),
                            ClientRequest.errorMessagePostClient(errorCode),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "Put error: ");
            }
        });

        Volley.newRequestQueue(getHostActivity()).add(clientRequest);
    }

    protected void reqPostClient(final Client client) {
        ClientRequest clientRequest = ClientRequest.requestPostClient(client, new PostResponseListener() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response: "+response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int error_code = jsonResponse.getInt("error_code");

                    if (error_code != ClientRequest.CODE_NOT_ERROR_POST_CLIENT) {
                        Toast.makeText(getHostActivity(),
                                ClientRequest.errorMessagePostClient(error_code),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (getHostActivity().getCallingActivity() != null ) {
                        Intent intent = new Intent();
                        intent.putExtra( ClientActivity.KEY_ADD_CLIENT, client );
                        getHostActivity().setResult(ConstantsBikeRentalApp.RESULT_OK, intent);
                    }

                    getHostActivity().onBackPressed();

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "ERROR");
            }
        });

        Volley.newRequestQueue(getHostActivity()).add(clientRequest);
    }


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

                    client.avatar = jsonResponse.getString("url");


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


}
