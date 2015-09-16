package ru.prokatvros.veloprokat.ui.fragments;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import ru.prokatvros.veloprokat.R;
import ru.prokatvros.veloprokat.ui.activities.ProfileActivity;


public class ProfileFragment extends BaseFragment<ProfileActivity> /* implements NotificationManager.Client */ {

    private static final int REQUEST_CODE_CHOOSE_IMAGE = 2;
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 3;

    ImageButton avatarMenu, delete, capture, gallery;
    LinearLayout profileButtons;
    RelativeLayout contactData;

    ImageView avatar;
    TextView profileName;

    TextView contactName;
    TextView contactEmail;
    //ContactStep currentContact;
    File file;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //NotificationManager.registerClient(this);

        //if (getArguments() != null)
            //currentContact = (ContactStep) getArguments().getSerializable(WhereAreYouAppConstants.KEY_CONTACT);

        setHasOptionsMenu(true);
        //getHostActivity().getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getHostActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_profile;
    }

    /*
    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager.unregisterClient(this);
    }




    @Override
    public void findChildViews(@NotNull View view) {
        avatarMenu = (ImageButton) view.findViewById(R.id.ibAvatarMenu);
        avatar = (ImageView) view.findViewById(R.id.ivAvatar);
        delete = (ImageButton) view.findViewById(R.id.ibDelete);
        capture = (ImageButton) view.findViewById(R.id.ibCapture);
        gallery = (ImageButton) view.findViewById(R.id.ibGallery);
        profileButtons = (LinearLayout) view.findViewById(R.id.lnProfileButtons);
        profileName = (TextView) view.findViewById(R.id.tvProfileName);
        contactData = (RelativeLayout) view.findViewById(R.id.rlContactData);
        contactName = (TextView) view.findViewById(R.id.tvContactName);
        contactEmail = (TextView) view.findViewById(R.id.tvContactEmail);

        contactName.setText(WhereAreYouApplication.getInstance().getUserName());
        contactEmail.setText(WhereAreYouApplication.getInstance().getUserEmail());

        if(currentContact != null) {

            avatarMenu.setImageResource(R.drawable.drawable_ic_chat);
            avatarMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            contactData.setVisibility(View.VISIBLE);

            getHostActivity().getActionBarHolder().setMenuItemClickListener(R.id.ivDelete, new View.OnClickListener() {
                @Override
                public void onClick(View v) { }
            });

            getHostActivity().getActionBarHolder().setMenuItemClickListener(R.id.ivEdit, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getHostActivity().getActionBarHolder().expandSearchField(v);
                }
            });

            getHostActivity().getActionBarHolder().setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public void onQueryTextSubmit(final String query) {
                    if (TextUtils.isEmpty(query)) {
                        Toast.makeText(getActivity(), R.string.toast_empty_text, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    getHostActivity().showProgressDialog();
                }

                @Override
                public void onQueryTextChange(String newText) {

                }
            });

            getHostActivity().getActionBarHolder().setSearchField(R.drawable.drawable_ic_edit, null);

        } else {

            avatarMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (CommonUtils.isConnected(getHostActivity())) {
                        if (profileButtons.getVisibility() == View.VISIBLE) {
                            profileButtons.setVisibility(View.GONE);
                            avatarMenu.setSelected(false);
                        } else {
                            profileButtons.setVisibility(View.VISIBLE);
                            avatarMenu.setSelected(true);
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            avatarMenu.setImageResource(R.drawable.drawable_ic_camera);
            profileName.setText(R.string.text_my_profile);
        }

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto(Calendar.getInstance().getTimeInMillis()+"jpg");
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHostActivity().showProgressDialog();
                HttpServer.submitToServer(BaseAvatarRequest.getImagePostRequest(WhereAreYouApplication.getInstance().getUuid(), System.currentTimeMillis(), " "), new BaseResponseCallback<String>() {
                    @Override
                    public void onResponse(String result) {
                        getHostActivity().hideProgressDialog();
                        WhereAreYouApplication.removeAvatarFromCache(WhereAreYouApplication.getInstance().getUserName());
                    }

                    @Override
                    public void onErrorResponse(Exception error) {

                    }
                });
            }
        });

        WhereAreYouApplication.getInstance().getAvatarCache().displayImage(
                AvatarBase64ImageDownloader.getImageUriFor(currentContact == null ? WhereAreYouApplication.getInstance().getUserName() : currentContact.getName()), avatar);
    }

    public void capturePhoto(String targetFilename) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(FileUtils.getFilesDir(), Calendar.getInstance().getTimeInMillis()+"jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE);
        }
    }

    @Override
    public void handleNotificationMessage(int what, int arg1, int arg2, Object obj) {
        if(obj == null)
            return;

        ContactLocation contactLocation;
        if (what == WhereAreYouAppConstants.NOTIFICATION_CONTACTS_LOCATION) {
            List<ContactLocation> loaded = (List<ContactLocation>) obj;
            contactLocation = loaded.get(0);
            MapForPushActivity.startMapProfile(getActivity(),contactLocation);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getHostActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            final Uri uri;
            if (requestCode == REQUEST_CODE_CHOOSE_IMAGE) {
                uri = data.getData();
            } else {
                uri = Uri.fromFile(file);
             }

            if (uri == null)
                return;

            getHostActivity().showProgressDialog();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Bitmap avatarImage = BitmapUtils.decodeUri(getActivity(), uri, BitmapUtils.DESIRED_SIZE, BitmapUtils.DESIRED_SIZE, BitmapUtils.DecodeType.BOTH_SHOULD_BE_EQUAL_CUT);
                        final String base64Image = BitmapUtils.convertBitmapToBase64(avatarImage, false);
                        WhereAreYouApplication.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                AvatarRequestStepServer request = AvatarRequestStepServer.getImagePostRequest(WhereAreYouApplication.getInstance().getUserName(),base64Image);
                                HttpServer.submitToServer(request, new BaseResponseCallback<String>() {
                                    @Override
                                    public void onResponse(String result) {
                                        getHostActivity().hideProgressDialog();
                                        String avatarUri = uri.toString();
                                        WhereAreYouApplication.removeAvatarFromCache(WhereAreYouApplication.getInstance().getUserName());
                                        WhereAreYouApplication.getInstance().getAvatarCache().displayImage(avatarUri, avatar, new ImageLoadingListener() {
                                            @Override
                                            public void onLoadingStarted(String imageUri, View view) {}

                                            @Override
                                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {}

                                            @Override
                                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                NotificationManager.notifyClients(WhereAreYouAppConstants.NOTIFICATION_USER_AVATAR_LOADED);
                                            }

                                            @Override
                                            public void onLoadingCancelled(String imageUri, View view) {}
                                        });
                                    }

                                    @Override
                                    public void onErrorResponse(Exception error) {
                                        getHostActivity().hideProgressDialog();
                                    }
                                });
                            }
                        },true);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    } */
}