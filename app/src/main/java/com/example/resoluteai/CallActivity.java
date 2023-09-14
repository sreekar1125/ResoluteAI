package com.example.resoluteai;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.Collections;

public class CallActivity extends AppCompatActivity {

    private TextView nameTv;
    private EditText frndIdEt;
    private ZegoSendCallInvitationButton voiceCallBtn, videoCallBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        nameTv = findViewById(R.id.nameTv);
        frndIdEt = findViewById(R.id.frndIdEt);
        voiceCallBtn = findViewById(R.id.voiceCallBtn);
        videoCallBtn = findViewById(R.id.videoCallBtn);


        String name = getIntent().getStringExtra("name");
        nameTv.setText("Your user id is : " + name);
        startCallService(name);


        frndIdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String targetUserId = frndIdEt.getText().toString().trim();
                setVoiceCall(targetUserId);
                setVideoCall(targetUserId);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    void setVoiceCall(String targetUserId){
        voiceCallBtn.setIsVideoCall(true);
        voiceCallBtn.setResourceID("zego_uikit_call");
        voiceCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserId)));
    }

    void setVideoCall(String targetUserId){
        videoCallBtn.setIsVideoCall(true);
        videoCallBtn.setResourceID("zego_uikit_call");
        videoCallBtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserId)));
    }

    private void startCallService(String name) {
        Application application = getApplication(); // Android's application context
        long appID = 894274097;   // yourAppID
        String appSign = "bc8510ba0a8169ba1262078376e11e9d7688694b63d2413e1f9e9670179181c8";  // yourAppSign
        String userID = name; // yourUserID, userID should only contain numbers, English characters, and '_'.
        String userName = name;   // yourUserName

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;
        ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
        notificationConfig.sound = "zego_uikit_sound_call";
        notificationConfig.channelID = "CallInvitation";
        notificationConfig.channelName = "CallInvitation";
        ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }
}