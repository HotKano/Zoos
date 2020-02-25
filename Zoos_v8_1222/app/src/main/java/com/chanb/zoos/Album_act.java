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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Album_act extends AppCompatActivity {
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;
    private String mCurrentPhotoPath;
    private Uri photoUri;
    ImageView sample;
    Bitmap img;
    Button upload_btn;
    String id, text, kind, from;
    Button camera_test, album_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_act);

        try {
            Intent intent = getIntent();
            if (intent != null) {
                text = intent.getStringExtra("key");
                id = intent.getStringExtra("id");
                kind = intent.getStringExtra("kind");
                //0 upload //1 edit
                from = intent.getStringExtra("from");
            }

            boolean check = checkPermissions();

            if (!text.isEmpty() && check) {
                if (text.equals("1"))
                    goToAlbum();
                else if (text.equals("2"))
                    takePhoto();
            } else {
                Toast.makeText(this, "권한을 확인해주세요.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            upload_btn();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void backToMain() {
        finish();
    }

    private void upload_btn() {

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (photoUri.equals(""))
                        Toast.makeText(Album_act.this, "이미지 값 확인", Toast.LENGTH_SHORT).show();
                    else {
                        Intent intent = new Intent(Album_act.this, Upload_act.class);
                        intent.putExtra("id", id);
                        intent.putExtra("file", photoUri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private void upload(String kind, Uri photoUri) {

        //from : 0 upload, from : 1 edit
        if (from.equals("0")) {
            if (kind.equals("1")) {
                Intent intent = new Intent(Album_act.this, Upload_act.class);
                intent.putExtra("id", id);
                intent.putExtra("file", photoUri);
                intent.putExtra("kind", "1");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            } else if (kind.equals("3")) {
                try {
                    if (photoUri.equals(""))
                        Toast.makeText(Album_act.this, "이미지 값 확인", Toast.LENGTH_SHORT).show();
                    else {
                        Intent intent = new Intent(Album_act.this, Upload_act.class);
                        intent.putExtra("id", id);
                        intent.putExtra("file", photoUri);
                        intent.putExtra("kind", "3");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (from.equals("1")) {
            Intent intent = new Intent(Album_act.this, Pet_edit_act.class);
            intent.putExtra("id", id);
            intent.putExtra("file", photoUri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[0])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(Album_act.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(Album_act.this,
                    "com.chanb.zoos.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
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

    public void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            backToMain();
            return;
        }
        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                return;
            }


            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                selectedImage = getImageUri(getApplication(), bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String filePath = getPath(selectedImage);
            String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
            File tempFile = new File(filePath);
            photoUri = FileProvider.getUriForFile(Album_act.this,
                    "com.chanb.zoos.fileprovider", tempFile);

            if (file_extn.equals("gif")) {
                Toast.makeText(this, "gif 파일 이미지는 기능을 제공하지 않습니다.", Toast.LENGTH_SHORT).show();
                onBackPressed();
                return;
            }
            Uri source = data.getData();
            Uri destination_uri = Uri.fromFile(new File(getCacheDir(), "cropped"));
            Crop.of(source, destination_uri).asSquare().start(this);
        } else if (requestCode == PICK_FROM_CAMERA) {

            // 갤러리에 나타나게
            MediaScannerConnection.scanFile(Album_act.this,
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });

        } else if (requestCode == CROP_FROM_CAMERA) {
            try {
                img = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                mCurrentPhotoPath = getStringImage(img);
                //upload("1", photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == Crop.REQUEST_CROP) {
            handle_crop(resultCode, data);
        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }

    private void handle_crop(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            photoUri = Crop.getOutput(data);
            upload("1", photoUri);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, "image error", Toast.LENGTH_SHORT).show();
        }

    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 10, bytes);
        Bitmap resized = Bitmap.createScaledBitmap(inImage, 1, 1, true);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), resized, "Title", null);
        return Uri.parse(path);
    }

}
