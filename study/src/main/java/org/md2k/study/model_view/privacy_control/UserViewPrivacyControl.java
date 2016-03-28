package org.md2k.study.model_view.privacy_control;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.R;
import org.md2k.study.Status;
import org.md2k.study.model_view.Model;
import org.md2k.study.model_view.UserView;
import org.md2k.utilities.Report.Log;


/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class UserViewPrivacyControl extends UserView {
    private static final String TAG = UserViewPrivacyControl.class.getSimpleName();
    Handler handler;

    public UserViewPrivacyControl(Activity activity, Model model) {
        super(activity, model);
        handler=new Handler();
        addView();
    }

    @Override
    public void disableView() {
        handler.removeCallbacks(runnablePrivacy);
        if (view == null) return;
        activity.findViewById(R.id.button_privacy).setEnabled(false);
        Log.d(TAG, "updateView()...");
        ((TextView) activity.findViewById(R.id.text_view_privacy)).setText("Not Active");
        ((TextView) activity.findViewById(R.id.text_view_privacy)).setTextColor(ContextCompat.getColor(activity, R.color.teal_700));
        activity.findViewById(R.id.button_privacy).setBackground(ContextCompat.getDrawable(activity, R.drawable.button_teal));
        ((Button) activity.findViewById(R.id.button_privacy)).setText("Turn On");
    }
    public void stop(){
        handler.removeCallbacks(runnablePrivacy);
    }

    @Override
    public void enableView() {
        if (view == null) return;
        handler.removeCallbacks(runnablePrivacy);
        handler.post(runnablePrivacy);
    }
    Runnable runnablePrivacy=new Runnable() {
        @Override
        public void run() {
            activity.findViewById(R.id.button_privacy).setEnabled(true);
            Log.d(TAG, "updateView()...");
            PrivacyControlManager privacyControlManager = (PrivacyControlManager) model;
            Status status = privacyControlManager.getCurrentStatusDetails();

            if (status.getStatus() == Status.PRIVACY_ACTIVE) {
                long remainingTime = privacyControlManager.getPrivacyData().getStartTimeStamp() + privacyControlManager.getPrivacyData().getDuration().getValue() - DateTime.getDateTime();
                if (remainingTime > 0) {
                    remainingTime /= 1000;
                    int sec = (int) (remainingTime % 60);
                    int min = (int) (remainingTime / 60);
                    ((TextView) activity.findViewById(R.id.text_view_privacy)).setText("Resumed after " + String.format("%02d:%02d", min, sec));
                    ((TextView) activity.findViewById(R.id.text_view_privacy)).setTextColor(ContextCompat.getColor(activity, R.color.red_700));
                    activity.findViewById(R.id.button_privacy).setBackground(ContextCompat.getDrawable(activity, R.drawable.button_red));
                    ((Button)activity.findViewById(R.id.button_privacy)).setTextColor(Color.WHITE);
                    ((Button) activity.findViewById(R.id.button_privacy)).setText("Turn Off");
                    handler.postDelayed(this,1000);
                }
            }else {
                ((TextView) activity.findViewById(R.id.text_view_privacy)).setText("Not Active");
                ((TextView) activity.findViewById(R.id.text_view_privacy)).setTextColor(ContextCompat.getColor(activity, R.color.teal_700));
                activity.findViewById(R.id.button_privacy).setBackground(ContextCompat.getDrawable(activity, R.drawable.button_teal));
                ((Button) activity.findViewById(R.id.button_privacy)).setText("Turn On");
                ((Button)activity.findViewById(R.id.button_privacy)).setTextColor(Color.BLACK);
            }
        }
    };

    private void addView() {
        LinearLayout linearLayoutMain = (LinearLayout) activity.findViewById(R.id.linear_layout_main);
        view = activity.getLayoutInflater().inflate(R.layout.layout_privacy_control, null);
        linearLayoutMain.addView(view);
        prepareButton();
    }

    void prepareButton() {
        Button button = (Button) activity.findViewById(R.id.button_privacy);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(model.getAction().getPackage_name(), model.getAction().getClass_name());
                activity.startActivity(intent);
            }
        });
    }
}