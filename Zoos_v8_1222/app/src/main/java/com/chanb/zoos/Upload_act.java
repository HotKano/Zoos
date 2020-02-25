package com.chanb.zoos;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.soundcloud.android.crop.Crop;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Bitmap.createScaledBitmap;

public class Upload_act extends AppCompatActivity { // 글 사진 업로드 하는 액티비티

    ImageButton picture_btn, picture_btn1, picture_btn2, picture_btn3;
    Button upload_btn, back_btn;
    RequestQueue requestQueue;
    EditText edit_upload, title_upload, tag_upload;
    String id, check; // 유저 아이디
    Uri uri1;
    Bitmap photo, photo1, photo2, photo3;
    CustomDialog_Progress customDialog_progress;
    Intent intent_upload;
    MThread mThread;
    GlobalApplication globalApplication;
    private final U_Handler u_handler = new U_Handler(this);
    //ArrayList<String> images = new ArrayList<>();
    //ArrayList<String> exes = new ArrayList<>();

    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;
    private String mCurrentPhotoPath;
    private Uri photoUri;
    Bitmap img;

    String[] images = new String[4];
    String[] exes = new String[4];

    Button cancel, cancel1, cancel2, cancel3;
    Switch toggle_upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_act);
        try {
            checkPermissions();
            setting();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void msg_progress(Message msg2) {
        mThread.mBackHandler.sendMessage(msg2);
    }

    private void setting() {
        check = "false";
        toggle_upload = findViewById(R.id.toggle_upload);
        toggle_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckedChanged(toggle_upload.isChecked());
            }
        });
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        picture_btn = findViewById(R.id.pic_btn_upload);
        picture_btn1 = findViewById(R.id.pic_btn_upload1);
        picture_btn2 = findViewById(R.id.pic_btn_upload2);
        picture_btn3 = findViewById(R.id.pic_btn_upload3);
        Bitmap bigPictureBitmap = BitmapFactory.decodeResource(Upload_act.this.getResources(), R.drawable.plus_gray);
        bigPictureBitmap = createScaledBitmap(bigPictureBitmap, 100, 100, false);
        picture_btn.setImageBitmap(bigPictureBitmap);
        picture_btn1.setImageBitmap(bigPictureBitmap);
        picture_btn2.setImageBitmap(bigPictureBitmap);
        picture_btn3.setImageBitmap(bigPictureBitmap);


        picture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgBtn(view);
            }
        });
        picture_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgBtn(view);
            }
        });
        picture_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgBtn(view);
            }
        });
        picture_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgBtn(view);
            }
        });

        //이미지 적용 취소 버튼.
        cancel = findViewById(R.id.pic_btn_upload_cancel);
        cancel1 = findViewById(R.id.pic_btn_upload_cancel1);
        cancel2 = findViewById(R.id.pic_btn_upload_cancel2);
        cancel3 = findViewById(R.id.pic_btn_upload_cancel3);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photo != null) {
                    photo = null;
                    uri1 = null;
                    images[0] = null;
                    exes[0] = null;
                    picture_btn.setImageResource(0);
                    cancel.setVisibility(View.GONE);
                } else {
                    uri1 = null;
                    images[0] = null;
                    exes[0] = null;
                    picture_btn.setImageResource(0);
                    cancel.setVisibility(View.GONE);
                }
                Bitmap bigPictureBitmap = BitmapFactory.decodeResource(Upload_act.this.getResources(), R.drawable.plus_gray);
                bigPictureBitmap = createScaledBitmap(bigPictureBitmap, 100, 100, false);
                picture_btn.setImageBitmap(bigPictureBitmap);

            }
        });

        cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photo1 != null) {
                    photo1 = null;
                    uri1 = null;
                    images[1] = null;
                    exes[1] = null;
                    picture_btn1.setImageResource(0);
                    cancel1.setVisibility(View.GONE);
                } else {
                    uri1 = null;
                    images[1] = null;
                    exes[1] = null;
                    picture_btn1.setImageResource(0);
                    cancel1.setVisibility(View.GONE);
                }

                Bitmap bigPictureBitmap = BitmapFactory.decodeResource(Upload_act.this.getResources(), R.drawable.plus_gray);
                bigPictureBitmap = createScaledBitmap(bigPictureBitmap, 100, 100, false);
                picture_btn1.setImageBitmap(bigPictureBitmap);
            }
        });

        cancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photo2 != null) {
                    photo2 = null;
                    uri1 = null;
                    images[2] = null;
                    exes[2] = null;
                    picture_btn2.setImageResource(0);
                    cancel2.setVisibility(View.GONE);
                } else {
                    uri1 = null;
                    images[2] = null;
                    exes[2] = null;
                    picture_btn2.setImageResource(0);
                    cancel2.setVisibility(View.GONE);
                }

                Bitmap bigPictureBitmap = BitmapFactory.decodeResource(Upload_act.this.getResources(), R.drawable.plus_gray);
                bigPictureBitmap = createScaledBitmap(bigPictureBitmap, 100, 100, false);
                picture_btn2.setImageBitmap(bigPictureBitmap);
            }
        });

        cancel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photo3 != null) {
                    photo3 = null;
                    uri1 = null;
                    images[3] = null;
                    exes[3] = null;
                    picture_btn3.setImageResource(0);
                    cancel3.setVisibility(View.GONE);
                } else {
                    uri1 = null;
                    images[3] = null;
                    exes[3] = null;
                    picture_btn3.setImageResource(0);
                    cancel3.setVisibility(View.GONE);
                }

                Bitmap bigPictureBitmap = BitmapFactory.decodeResource(Upload_act.this.getResources(), R.drawable.plus_gray);
                bigPictureBitmap = createScaledBitmap(bigPictureBitmap, 100, 100, false);
                picture_btn3.setImageBitmap(bigPictureBitmap);
            }
        });

        upload_btn = findViewById(R.id.upload_btn_upload);

        edit_upload = findViewById(R.id.edit_upload);
        title_upload = findViewById(R.id.title_edit);
        tag_upload = findViewById(R.id.tag_upload);

        back_btn = findViewById(R.id.backbutton_upload);
        intent_upload = getIntent();
        if (intent_upload != null) {
            id = intent_upload.getStringExtra("id");
        }
        customDialog_progress = new CustomDialog_Progress(this);

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgBtn(view);
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgBtn(v);
            }
        });
    }

    public void imgBtn(View view) {
        int id_view = view.getId();

        if (id_view == R.id.pic_btn_upload
                || id_view == R.id.pic_btn_upload1
                || id_view == R.id.pic_btn_upload2
                || id_view == R.id.pic_btn_upload3) {
            goToAlbum();
        }

        if (id_view == R.id.upload_btn_upload) {
            mThread = new MThread(u_handler);
            mThread.setDaemon(true);
            mThread.start();
            String content = edit_upload.getText().toString();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(Upload_act.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else if (photoUri == null) {
                Toast.makeText(Upload_act.this, "사진을 등록해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                uploadConnect();
            }

        }


        if (id_view == R.id.backbutton_upload) {
            onBackPressed();
        }

    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    protected void uploadConnect() {
        String sendUrl = "http://133.186.135.41/zozo_sns_upload_story.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonOutput = null;
                String text = null;

                try {
                    jsonOutput = JsonUtil.getJSONObjectFrom(response);
                    text = JsonUtil.getStringFrom(jsonOutput, "state");
                    Log.d("upload_act_data", response);
                    if (text.equals("sus1")) {
                        Intent intent = new Intent(Upload_act.this, Story_act.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("page", 1);
                        startActivity(intent);
                        finish();
                        upload_btn.setEnabled(true);
                    } else if (text.equals("fail")) {
                        Toast.makeText(Upload_act.this, "등록 실패.", Toast.LENGTH_SHORT).show();
                        upload_btn.setEnabled(true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Upload_act.this, "인터넷 또는 자료값 확인.", Toast.LENGTH_SHORT).show();
                upload_btn.setEnabled(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<>();

                String content = edit_upload.getText().toString();
                String tag = tag_upload.getText().toString();
                String id_local = id;
                String temp_result_for_title;
                if (content.length() > 15) {
                    temp_result_for_title = content.substring(0, 15) + "..";
                } else {
                    temp_result_for_title = content;
                }

                parameters.put("id", id_local);
                parameters.put("title", temp_result_for_title);
                parameters.put("content", content);
                parameters.put("tag", tag);
                parameters.put("private", check);

                for (int i = 0; i < images.length; i++) {
                    String fileName = images[i];
                    String exeName = exes[i];
                    if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(exeName)) {
                        continue;
                    }
                    parameters.put("file" + i, images[i]);
                    parameters.put("exe" + i, exes[i]);
                }

                Message msg = new Message();
                msg.what = 0;
                msg_progress(msg);
                return parameters;
            }
        };

        requestQueue.add(request);
    }

    //앨범 또는 사진 촬영을 위한 커스텀 다이얼로그.
    public void dialog_show() {
        customDialog_progress.show();
        customDialog_progress.setCancelable(false);
    }

    //액티비티 파괴 시 커스텀 다이얼로그 파괴.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customDialog_progress != null) {
            customDialog_progress.dismiss();
            customDialog_progress = null;
        }

    }

    //로딩 이미지를 위한 핸들러.
    public static final class U_Handler extends Handler {

        private final WeakReference<Upload_act> ref;

        private U_Handler(Upload_act act) {
            ref = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            Upload_act act = ref.get();
            if (act != null) {
                switch (msg.what) {
                    case 0:
                        act.dialog_show();
                        break;
                }
            }

        }

    }

    public void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                return;
            }
            Uri selectedImage = data.getData();
            String filePath = getPath(selectedImage);
            String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
            File tempFile = new File(filePath);

            String strFileSize;

            if (tempFile.exists()) {
                long lFileSize = tempFile.length();
                strFileSize = Long.toString(lFileSize) + " bytes";
                if (lFileSize > 8000000) {
                    Toast.makeText(this, "파일 사이즈가 너무 큽니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                strFileSize = "파일없음";
            }
            Log.d("upload_act_data", strFileSize + file_extn);
            photoUri = FileProvider.getUriForFile(this,
                    "com.chanb.zoos.fileprovider", tempFile);

            if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                if (file_extn.equals("gif")) {
                    try {
                        //.gif
                        if (photoUri != null && TextUtils.isEmpty(images[0])) {
                            InputStream inputStream = getContentResolver().openInputStream(photoUri);
                            byte[] inputData = getBytes(inputStream);
                            String image_name_gif = Base64.encodeToString(inputData, Base64.NO_WRAP);
                            images[0] = image_name_gif;
                            exes[0] = ".gif";
                            Glide.with(picture_btn)
                                    .asBitmap()
                                    .load(photoUri)
                                    .apply(new RequestOptions()
                                            .placeholder(R.drawable.placeholder)
                                            .error(R.drawable.placeholder)
                                            .dontTransform()
                                            .skipMemoryCache(false)
                                            .transform(new RoundedCorners(20))
                                    )
                                    .into(picture_btn);
                            cancel.setVisibility(View.VISIBLE);
                        } else if (photoUri != null && TextUtils.isEmpty(images[1])) {
                            InputStream inputStream = getContentResolver().openInputStream(photoUri);
                            byte[] inputData = getBytes(inputStream);
                            String image_name_gif = Base64.encodeToString(inputData, Base64.NO_WRAP);
                            images[1] = image_name_gif;
                            exes[1] = ".gif";
                            Glide.with(this)
                                    .asBitmap()
                                    .load(photoUri)
                                    .apply(new RequestOptions()
                                            .placeholder(R.drawable.placeholder)
                                            .error(R.drawable.placeholder)
                                            .dontTransform()
                                            .skipMemoryCache(false)
                                            .transform(new RoundedCorners(20))
                                    )
                                    .into(picture_btn1);
                            cancel1.setVisibility(View.VISIBLE);
                        } else if (photoUri != null && TextUtils.isEmpty(images[2])) {
                            InputStream inputStream = getContentResolver().openInputStream(photoUri);
                            byte[] inputData = getBytes(inputStream);
                            String image_name_gif = Base64.encodeToString(inputData, Base64.NO_WRAP);
                            images[2] = image_name_gif;
                            exes[2] = ".gif";
                            Glide.with(this)
                                    .asBitmap()
                                    .load(photoUri)
                                    .apply(new RequestOptions()
                                            .placeholder(R.drawable.placeholder)
                                            .error(R.drawable.placeholder)
                                            .dontTransform()
                                            .skipMemoryCache(false)
                                            .transform(new RoundedCorners(20))
                                    )
                                    .into(picture_btn2);
                            cancel2.setVisibility(View.VISIBLE);
                        } else if (photoUri != null && TextUtils.isEmpty(images[3])) {
                            InputStream inputStream = getContentResolver().openInputStream(photoUri);
                            byte[] inputData = getBytes(inputStream);
                            String image_name_gif = Base64.encodeToString(inputData, Base64.NO_WRAP);
                            images[3] = image_name_gif;
                            exes[3] = ".gif";
                            Glide.with(this)
                                    .asBitmap()
                                    .load(photoUri)
                                    .apply(new RequestOptions()
                                            .placeholder(R.drawable.placeholder)
                                            .error(R.drawable.placeholder)
                                            .dontTransform()
                                            .skipMemoryCache(false)
                                            .transform(new RoundedCorners(20))
                                    )
                                    .into(picture_btn3);
                            cancel3.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //gif 제외한 나머지 확장자.
                    //크롭.
                    Uri source = data.getData();
                    Uri destination_uri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                    Crop.of(source, destination_uri).asSquare().start(this);
                }
            } else {
                //NOT IN REQUIRED FORMAT
                Toast.makeText(this, "올 바른 확장자가 아닙니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

        } else if (requestCode == PICK_FROM_CAMERA) {
            cropImage();

            // 갤러리에 나타나게
            MediaScannerConnection.scanFile(this,
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });

        } else if (requestCode == Crop.REQUEST_CROP) {
            handle_crop(resultCode, data);
        }
    }

    //Android N crop image
    public void cropImage() {
        this.grantUriPermission("com.android.camera", photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 960);
            intent.putExtra("outputY", 720);
            intent.putExtra("scale", true);
            File croppedFileName = null;
            try {
                croppedFileName = createImageFile(); // 이미지 to 갤러리.
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/Zoos/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());


            photoUri = FileProvider.getUriForFile(this,
                    "com.chanb.zoos.fileprovider", tempFile);
            intent.putExtra("return-data", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG);

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                grantUriPermission(res.activityInfo.packageName, photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);
        }
    }

    private void handle_crop(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            photoUri = Crop.getOutput(data);
            try {
                if (photoUri != null && TextUtils.isEmpty(images[0])) {
                    photo = rotation(photoUri);
                    //Bitmap resized = Bitmap.createScaledBitmap(photo, 1024, 768, true);
                    String imageName = getStringImage(photo);
                    images[0] = imageName;
                    exes[0] = ".jpg";
                    Glide.with(this)
                            .asBitmap()
                            .load(photo)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .dontTransform()
                                    .transform(new RoundedCorners(20))
                            )
                            .into(picture_btn);
                    cancel.setVisibility(View.VISIBLE);
                } else if (photoUri != null && TextUtils.isEmpty(images[1])) {
                    photo1 = rotation(photoUri);
                    //Bitmap resized = Bitmap.createScaledBitmap(photo, 1024, 768, true);
                    String imageName = getStringImage(photo1);
                    images[1] = imageName;
                    exes[1] = ".jpg";
                    Glide.with(this)
                            .asBitmap()
                            .load(photo1)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .dontTransform()
                                    .transform(new RoundedCorners(20))
                            )
                            .into(picture_btn1);
                    cancel1.setVisibility(View.VISIBLE);
                } else if (photoUri != null && TextUtils.isEmpty(images[2])) {
                    photo2 = rotation(photoUri);
                    //Bitmap resized = Bitmap.createScaledBitmap(photo, 1024, 768, true);
                    String imageName = getStringImage(photo2);
                    images[2] = imageName;
                    exes[2] = ".jpg";
                    Glide.with(this)
                            .asBitmap()
                            .load(photo2)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .dontTransform()
                                    .transform(new RoundedCorners(20))
                            )
                            .into(picture_btn2);
                    cancel2.setVisibility(View.VISIBLE);
                } else if (photoUri != null && TextUtils.isEmpty(images[3])) {
                    photo3 = rotation(photoUri);
                    //Bitmap resized = Bitmap.createScaledBitmap(photo, 1024, 768, true);
                    String imageName = getStringImage(photo3);
                    images[3] = imageName;
                    exes[3] = ".jpg";
                    Glide.with(this)
                            .asBitmap()
                            .load(photo3)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .dontTransform()
                                    .transform(new RoundedCorners(20))
                            )
                            .into(picture_btn3);
                    cancel3.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, "image error", Toast.LENGTH_SHORT).show();
        }

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "Zoos" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Zoos/");

        // 파일 생성 (촬영 후 실제 저장할지 말지 결정)
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void onCheckedChanged(boolean isChecked) {

        if (isChecked) {
            check = "true";
        } else {
            check = "false";
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private Bitmap rotation(Uri imageUri) {
        try {
            // 비트맵 이미지로 가져온다
            String imagePath = imageUri.getPath();
            Bitmap image = BitmapFactory.decodeFile(imagePath);

            // 이미지를 상황에 맞게 회전시킨다
            ExifInterface exif = new ExifInterface(imagePath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifDegree = exifOrientationToDegrees(exifOrientation);
            image = rotate(image, exifDegree);

            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }
}