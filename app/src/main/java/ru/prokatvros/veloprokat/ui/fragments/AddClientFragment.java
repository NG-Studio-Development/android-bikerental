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

    //private static final int REQUEST_CODE_CHOOSE_IMAGE = 2;
    
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 3;

    private static final int BASE_64_IMAGE_HANDLER = 1;

    File file;
    Client client;
    String base64Image;
    Button buttonAddPhoto;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if ( msg.what == BASE_64_IMAGE_HANDLER ) {
                if (base64Image != null) {
                    postImageToServer(base64Image);
                    buttonAddPhoto.setText(getString(R.string.photo_was_added));
                }

            }
        }
    };


    public AddClientFragment() {}

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_add_client;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_client, container, false);
        Button buttonAdd = (Button) view.findViewById(R.id.buttonAdd);
        buttonAddPhoto = (Button) view.findViewById(R.id.buttonAddPhoto);

        final EditText etName = (EditText) view.findViewById(R.id.etName);
        final EditText etSurname = (EditText) view.findViewById(R.id.etSurname);
        final EditText etPhone = (EditText) view.findViewById(R.id.etSearch);

        client = new Client();

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClient(etName.getText().toString(),
                        etSurname.getText().toString(),
                        etPhone.getText().toString());

                //getHostActivity().onBackPressed();
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

    public void capturePhoto( /*String targetFilename*/ ) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(FileUtils.getFilesDir(), Calendar.getInstance().getTimeInMillis()+"jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE);
        }
    }

    private void addClient( String name, String surname, String phone ) {

        if ( name.isEmpty() || surname.isEmpty() || phone.isEmpty() ) {
            Toast.makeText( getHostActivity(), getString(R.string.warning_can_not_have_empty_field), Toast.LENGTH_LONG ).show();
            return;
        }


        client.name = name;
        client.surname = surname;
        client.phone = phone;
        client.save();

        sendToServer(client);

    }


    protected void sendToServer(final Client client) {
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
            /*if (requestCode == REQUEST_CODE_CHOOSE_IMAGE) {
                uri = data.getData();
            } else*/
            if (requestCode == REQUEST_CODE_CAPTURE_IMAGE) {
                uri = Uri.fromFile(file);
            } else {
                return;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Bitmap avatarImage = BitmapUtils.decodeUri(getActivity(), uri, BitmapUtils.DESIRED_SIZE, BitmapUtils.DESIRED_SIZE, BitmapUtils.DecodeType.BOTH_SHOULD_BE_EQUAL_CUT);
                        base64Image = BitmapUtils.convertBitmapToBase64(avatarImage, false);
                        handler.sendMessage( handler.obtainMessage( BASE_64_IMAGE_HANDLER) );

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
