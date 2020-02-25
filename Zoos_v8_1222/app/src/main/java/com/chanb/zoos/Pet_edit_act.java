package com.chanb.zoos;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class Pet_edit_act extends AppCompatActivity {

    GlobalApplication globalApplication;
    String id, petNo, kind, petRealNumber, petRealGender, petRealNeutral, nickname;
    RequestQueue requestQueue;
    Intent intent;
    SNS_frag sns_frag;
    EditText nameEdit, contentEdit;
    ImageView petProfileEdit;
    Upload_act upload_act;
    Bitmap photo;
    Button upload_pet_edit;
    ImageButton backEdit;
    TextView petKindView, petAgeView, petGenderView, petNeutralView,
            petNumberView, petRaceView, petNumberNoneView;
    MThread mThread;
    CustomDialog_Progress customDialog_progress;
    private final U_Handler_Edit u_handler = new U_Handler_Edit(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_edit_act);
        try {
            setting();
            dialogControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setting() {
        globalApplication = new GlobalApplication();
        globalApplication.getWindow(this);
        customDialog_progress = new CustomDialog_Progress(this);
        sns_frag = SNS_frag._SNS_frag;
        backEdit = findViewById(R.id.backButton_edit);
        upload_pet_edit = findViewById(R.id.petEdit_upload);
        backEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        nameEdit = findViewById(R.id.edit_petName);
        contentEdit = findViewById(R.id.edit_petContent);
        petProfileEdit = findViewById(R.id.edit_petProfile);

        upload_act = new Upload_act();

        //TextView petKindView, petAgeView, petGenderView, petNeutralView, petWeightView, petNumberView;
        petRaceView = findViewById(R.id.pet_Race_kind_view);
        petKindView = findViewById(R.id.petKindView);
        petAgeView = findViewById(R.id.petAgeView);
        petGenderView = findViewById(R.id.petGenderView);
        petNeutralView = findViewById(R.id.petNeutralView);

        petNumberNoneView = findViewById(R.id.petNumberNoneView);
        petNumberView = findViewById(R.id.petNumberView);

        intent = getIntent();
        if (intent != null) {
            id = sns_frag.id;
            nickname = sns_frag.nickname;
            petNo = intent.getStringExtra("petNo");

            //0: insert // 1 : update.
            kind = intent.getStringExtra("kind");
            Log.d("test_pet_edit", id + petNo + kind);

            if (kind.equals("1")) {
                Log.d("edit", "kind");
                gridConnect();
            } else if (kind.equals("0")) {
                Glide.with(this)
                        .load(R.drawable.round_pet_insert_gray)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .skipMemoryCache(true)
                                .dontTransform()
                                .centerCrop()
                                .circleCrop()
                        ).into(petProfileEdit);
            }
        }


        petProfileEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pet_edit_act.this, Album_act.class);
                intent.putExtra("key", "1");
                intent.putExtra("from", "1");
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        upload_pet_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadConnect();
                mThread = new MThread(u_handler);
                mThread.setDaemon(true);
                mThread.start();
                upload_pet_edit.setEnabled(false);
            }
        });


    }

    public void dialogControl() {
        petRaceView.setOnClickListener(new View.OnClickListener() {
            CustomDialog_petRace customDialog_petRace = new CustomDialog_petRace(Pet_edit_act.this, "race");

            @Override
            public void onClick(View v) {
                customDialog_petRace.show();
                customDialog_petRace.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        String text = customDialog_petRace.getPetRace();
                        if (!TextUtils.isEmpty(text))
                            petRaceView.setText(text);
                    }
                });


            }
        });

        petKindView.setOnClickListener(new View.OnClickListener() {
            CustomDialog_Insert customDialog_insert = new CustomDialog_Insert(Pet_edit_act.this, "kind");

            @Override
            public void onClick(View v) {
                customDialog_insert.show();
                customDialog_insert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        String text = customDialog_insert.getKind();
                        if (!TextUtils.isEmpty(text))
                            petKindView.setText(text);
                    }
                });
            }
        });

        petAgeView.setOnClickListener(new View.OnClickListener() {
            CustomDialog_Insert customDialog_insert = new CustomDialog_Insert(Pet_edit_act.this, "age");

            @Override
            public void onClick(View v) {
                customDialog_insert.show();
                customDialog_insert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        String text = customDialog_insert.getAge();
                        if (!TextUtils.isEmpty(text))
                            petAgeView.setText(text);
                    }
                });
            }
        });

        petGenderView.setOnClickListener(new View.OnClickListener() {
            CustomDialog_petRace customDialog_petRace = new CustomDialog_petRace(Pet_edit_act.this, "gender");

            @Override
            public void onClick(View v) {
                customDialog_petRace.show();
                customDialog_petRace.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        String text = customDialog_petRace.getPetGender();
                        if (!TextUtils.isEmpty(text)) {
                            if (text.equals("1")) {
                                petGenderView.setText("수컷");
                                petRealGender = "1";
                            } else {
                                petGenderView.setText("암컷");
                                petRealGender = "0";
                            }
                        }
                    }
                });


            }
        });

        petNeutralView.setOnClickListener(new View.OnClickListener() {
            CustomDialog_petRace customDialog_petRace = new CustomDialog_petRace(Pet_edit_act.this, "neutral");

            @Override
            public void onClick(View v) {
                customDialog_petRace.show();
                customDialog_petRace.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        String text = customDialog_petRace.getPetNeutral();
                        if (!TextUtils.isEmpty(text)) {
                            Log.d("petEdit", text);
                            if (text.equals("1")) {
                                petNeutralView.setText("실시");
                                petRealNeutral = "1";
                            } else {
                                petNeutralView.setText("미실시");
                                petRealNeutral = "0";
                            }
                        }
                    }
                });

            }
        });

        //동물 번호가 존재할 때.
        petNumberView.setOnClickListener(new View.OnClickListener() {
            CustomDialog_Insert customDialog_insert = new CustomDialog_Insert(Pet_edit_act.this, "number");

            @Override
            public void onClick(View v) {
                customDialog_insert.show();
                customDialog_insert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        String text = customDialog_insert.getNumber();
                        if (!TextUtils.isEmpty(text)) {
                            petNumberView.setText(text);
                            petRealNumber = text;
                            petNumberNoneView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        //동물 번호가 존재하지 않을 때.
        petNumberNoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                petNumberView.setVisibility(View.INVISIBLE);
                petRealNumber = "없음";
            }
        });
    }

    //서버로 부터 json 받아오는 부분. 저장한 글들 받아오는 부분.
    public void gridConnect() {
        String SNSUrl = "http://133.186.135.41/zozo_edit_pet_show.php";
        StringRequest request = new StringRequest(Request.Method.POST, SNSUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("EditAct", response);
                try {
                    doJSONParser_grid(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Pet_edit_act.this, "인터넷 접속 상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", id);
                parameters.put("petNo", petNo);
                Log.d("Save", id);

                return parameters;
            }
        };
        requestQueue.add(request);
    }

    //실제 레이아웃 구현 좋아요 표시한 글 그리드 뷰.
    public void doJSONParser_grid(String response) {

        try {
            JSONArray jArray = new JSONObject(response).getJSONArray("data");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String petName = jObject.getString("petName");
                String petProfile = jObject.getString("petProfile");
                String petGender = jObject.getString("petGender");
                String petKind = jObject.getString("petKind");
                String petAge = jObject.getString("petAge");
                String petContent = jObject.getString("content");
                String petRace = jObject.getString("petRace");
                String petNeutral = jObject.getString("neutral");
                String petNumber = jObject.getString("petNumber");

                //String petName, String petContent
                viewSetting(petName, petContent, petProfile, petRace, petKind, petAge, petGender, petNeutral, petNumber);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void uploadConnect() {
        String sendUrl = "http://133.186.135.41/zozo_edit_pet.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonOutput = null;
                String text = null;

                try {
                    jsonOutput = JsonUtil.getJSONObjectFrom(response);
                    text = JsonUtil.getStringFrom(jsonOutput, "state");
                    Log.d("fail", response);
                    if (text.equals("sus1")) {
                        Intent intent = new Intent(Pet_edit_act.this, Story_act.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("id", id);
                        intent.putExtra("target_id", id);
                        intent.putExtra("nickname", nickname);
                        startActivity(intent);
                        finish();
                        mThread.stop();
                        upload_pet_edit.setEnabled(true);
                    } else if (text.equals("fail")) {
                        Toast.makeText(getApplicationContext(), "등록 실패.", Toast.LENGTH_SHORT).show();
                        upload_pet_edit.setEnabled(true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "인터넷 또는 자료값 확인.", Toast.LENGTH_SHORT).show();
                upload_pet_edit.setEnabled(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<>();
                if (photo != null) {
                    Bitmap photo_temp = photo;
                    String petProfile = getStringImage(photo_temp);
                    photo = null;
                    parameters.put("file", petProfile);
                } else {
                    Log.d("photo err", photo + "****");
                }

                String petName = nameEdit.getText().toString();
                String content = contentEdit.getText().toString();
                String petKind = petKindView.getText().toString();
                String petAge = petAgeView.getText().toString();
                String petRace = petRaceView.getText().toString();

                if (petNo == null) {
                    petNo = "0";
                }

                parameters.put("id", id);
                parameters.put("petNo", petNo);
                parameters.put("petName", petName);
                parameters.put("content", content);
                parameters.put("petGender", petRealGender);
                parameters.put("petKind", petKind);
                parameters.put("petAge", petAge);
                parameters.put("neutral", petRealNeutral);
                parameters.put("petNumber", petRealNumber);
                parameters.put("petRace", petRace);
                parameters.put("kind", kind);

                Message msg = new Message();
                msg.what = 0;
                msg_progress(msg);

                return parameters;
            }
        };

        requestQueue.add(request);
    }

    //petRace, petKind, petAge, petGender, petNeutral, petWeight, petNumber
    private void viewSetting(String petName, String petContent, String petProfile, String petRace,
                             String petKind, String petAge, String petGender, String petNeutral, String petNumber) {
        nameEdit.setText(petName);
        contentEdit.setText(petContent);
        petRaceView.setText(petRace);
        petKindView.setText(petKind);
        petAgeView.setText(petAge);
        petNumberView.setText(petNumber);
        petRealNumber = petNumber;
        petRealGender = petGender;
        petRealNeutral = petNeutral;

        //분류
        if (petGender.equals("1")) {
            petGenderView.setText("수컷");
        } else {
            petGenderView.setText("암컷");
        }


        if (petNeutral.equals("1")) {
            petNeutralView.setText("실시");
        } else {
            petNeutralView.setText("미실시");
        }


        String url = "http://133.186.135.41/zozoPetProfile/" + petProfile;
        Log.d("viewSetting", petProfile + "  " + url);
        //메인 이미지
        Glide.with(this)
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .skipMemoryCache(true)
                        .dontTransform()
                        .centerCrop()
                        .circleCrop()
                ).into(petProfileEdit);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.get("file") == null) {
                Log.d("camera", "이미지 값 확인!");
                Toast.makeText(this, "이미지 값 확인!", Toast.LENGTH_SHORT).show();
            } else if (extras.get("file") != null) {
                Log.d("camera", "사진 등록 완료!");
                Toast.makeText(this, "사진 등록 완료.", Toast.LENGTH_SHORT).show();
                Uri uri = (Uri) extras.get("file");
                if (uri != null) {
                    photo = rotation(uri);
                    Glide.with(this)
                            .load(photo)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .centerCrop()
                                    .circleCrop())
                            .into(petProfileEdit);
                }


            }

            if (id == null)
                id = extras.getString("id");
        }
    }

    public void msg_progress(Message msg2) {
        mThread.mBackHandler.sendMessage(msg2);
    }

    public void dialog_show() {
        customDialog_progress.show();
        customDialog_progress.setCancelable(false);
    }

    public static final class U_Handler_Edit extends Handler {

        private final WeakReference<Pet_edit_act> ref;

        private U_Handler_Edit(Pet_edit_act act) {
            ref = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            Pet_edit_act act = ref.get();
            if (act != null) {
                switch (msg.what) {
                    case 0:
                        act.dialog_show();
                        act.upload_pet_edit.setEnabled(true);
                        break;
                }
            }

        }

    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customDialog_progress != null) {
            customDialog_progress.dismiss();
        }
    }
}
