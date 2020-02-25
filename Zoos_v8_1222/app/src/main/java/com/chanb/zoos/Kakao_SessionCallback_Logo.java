package com.chanb.zoos;

import android.content.Intent;
import android.util.Log;

import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

public class Kakao_SessionCallback_Logo implements ISessionCallback {
    Logo_act logo_act = new Logo_act();

    @Override
    public void onSessionOpened() {
        requestMe();
    }

    private void requestMe() {
        // 사용자정보 요청 결과에 대한 Callback

        UserManagement.getInstance().requestMe(new MeResponseCallback() {

            // 세션 오픈 실패. 세션이 삭제된 경우,

            @Override

            public void onSessionClosed(ErrorResult errorResult) {

                Log.e("SessionCallback :: ", "onSessionClosed : " + errorResult.getErrorMessage());

            }


            // 회원이 아닌 경우,

            @Override

            public void onNotSignedUp() {

                Log.e("SessionCallback :: ", "onNotSignedUp");

            }


            // 사용자정보 요청에 성공한 경우,

            @Override

            public void onSuccess(UserProfile userProfile) {


                Log.e("SessionCallback :: ", "onSuccess");


                String nickname = userProfile.getNickname();

                String email = userProfile.getEmail();

                String profileImagePath = userProfile.getProfileImagePath();

                String thumnailPath = userProfile.getThumbnailImagePath();

                String UUID = userProfile.getUUID();

                long id = userProfile.getId();


                Log.e("Profile : ", nickname + "");

                Log.e("Profile : ", email + "");

                Log.e("Profile : ", profileImagePath + "");

                Log.e("Profile : ", thumnailPath + "");

                Log.e("Profile : ", UUID + "");

                Log.e("Profile : ", id + "");

                try {
                    logo_act = ((Logo_act) Logo_act._logo_act);
                    Intent intent = new Intent(logo_act, Join_Act2.class);
                    Log.d("kakao", id + "");
                    intent.putExtra("kind", "kakao");
                    intent.putExtra("id", id);
                    intent.putExtra("nickname", nickname);
                    intent.putExtra("email", email);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    logo_act.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            // 사용자 정보 요청 실패

            @Override

            public void onFailure(ErrorResult errorResult) {

                Log.e("SessionCallback :: ", "onFailure : " + errorResult.getErrorMessage());

            }

        });

    }


    @Override
    public void onSessionOpenFailed(KakaoException exception) {

    }

}
