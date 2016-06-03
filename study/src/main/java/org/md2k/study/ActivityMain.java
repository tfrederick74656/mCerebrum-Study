package org.md2k.study;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.study.config.ConfigViewContent;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.UserView;
import org.md2k.study.model_view.app_reset.ActivityAppReset;
import org.md2k.study.model_view.privacy_control.PrivacyControlManager;
import org.md2k.study.view.admin.ActivityAdmin;
import org.md2k.utilities.FileManager;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.UI.ActivityAbout;
import org.md2k.utilities.UI.ActivityCopyright;

import java.io.File;
import java.util.ArrayList;

public class ActivityMain extends AppCompatActivity {
    private static final String TAG = ActivityMain.class.getSimpleName();
    ArrayList<UserView> userViews;
    MenuItem menuItemStatus;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()...");
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            Log.d(TAG, "closing...");
            Intent intent = new Intent(this, ServiceSystemHealth.class);
            stopService(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Status.class.getSimpleName()));
        Log.d(TAG, "broadcast...set...");
        userViews = new ArrayList<>();
        Log.d(TAG, "onCreate()..isServiceRunning=" + ServiceSystemHealth.isRunning);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("mCerebrum");
            try {
                String logoName = ModelManager.getInstance(ActivityMain.this).getConfigManager().getConfig().getStudy_info().getLogo();
                if (logoName != null) {
                    String path = Constants.CONFIG_DIRECTORY + logoName;
                    Drawable d = Drawable.createFromPath(path);
                    getSupportActionBar().setIcon(d);
                }
            } catch (Exception ignored) {

            }
            try {
                String title = ModelManager.getInstance(ActivityMain.this).getConfigManager().getConfig().getStudy_info().getTitle();
                if (title != null) {
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                    getSupportActionBar().setTitle(title);
                }
            } catch (Exception ignored) {

            }
        }
        handler = new Handler();
        addUserView();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }


    void updateMenu() {
        if (menuItemStatus == null) return;
        Status status = ModelManager.getInstance(ActivityMain.this).getStatus();
        if (status.getRank() > Status.RANK_ADMIN_OPTIONAL)
            menuItemStatus.setIcon(R.drawable.ic_error_red_48dp);
        else menuItemStatus.setIcon(R.drawable.ic_ok_green_48dp);
    }

    Runnable runnableUpdateUI = new Runnable() {
        @Override
        public void run() {
            ModelManager modelManager = ModelManager.getInstance(ActivityMain.this);
            Status status = modelManager.getStatus();
            updateMenu();
            if (status.getRank() > Status.RANK_ADMIN_OPTIONAL)
                handler.postDelayed(runnableUpdateUI, 1000);
            else try {
                updateUI();
            } catch (DataKitException e) {
                e.printStackTrace();
            }
        }
    };

    void updateUI() throws DataKitException {

        Log.d(TAG, "updateUI()...");
        ModelManager modelManager = ModelManager.getInstance(this);
        Status status = modelManager.getStatus();
        updateMenu();
        if (status.getRank() > Status.RANK_ADMIN_OPTIONAL) return;


        for (int i = 0; i < userViews.size(); i++) {
            Log.d(TAG, "modelmanager Status=" + status.log() + " view status=" + userViews.get(i).getModel().getRank());
            if (status.getRank() > Status.RANK_USER_REQUIRED) {
                userViews.get(i).disableView();
                Log.d(TAG, "userView disabled");
            } else {
                if (userViews.get(i).getModel() instanceof PrivacyControlManager) {
                    Log.d(TAG, "privacyManager..view.." + i);
                    PrivacyControlManager privacyControlManager = ((PrivacyControlManager) userViews.get(i).getModel());
                    privacyControlManager.set();
                }
                userViews.get(i).enableView();
                Log.d(TAG, "userView enabled");
            }
        }
        Log.d(TAG, "updateUI...");
    }


    public void addUserView() {
        Log.d(TAG, "addUserView()...");
        ArrayList<ConfigViewContent> viewContents = ModelManager.getInstance(ActivityMain.this).getConfigManager().getConfig().getUser_view().getView_contents();
        LinearLayout linearLayoutMain = (LinearLayout) findViewById(R.id.linear_layout_main);
        linearLayoutMain.removeAllViews();

        for (int i = 0; i < viewContents.size(); i++) {
            if (!viewContents.get(i).isEnable()) continue;
            UserView userView = UserView.getUserView(this, viewContents.get(i).getId());
            if (userView != null) {
                userViews.add(userView);
            }
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()...");
        handler.removeCallbacks(runnableUpdateUI);
        handler.post(runnableUpdateUI);
        super.onResume();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Status status = intent.getParcelableExtra(Status.class.getSimpleName());
                Log.d(TAG, "received broadcast...rank =" + status.getRank());
                if (status.getStatus() == Status.CONFIG_FILE_NOT_EXIST)
                    finish();
                else {
                    handler.removeCallbacks(runnableUpdateUI);
                    handler.post(runnableUpdateUI);
                    updateUI();
                }
            } catch (DataKitException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if (ServiceSystemHealth.RANK_LIMIT == Status.RANK_ADMIN_OPTIONAL)
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        menuItemStatus = menu.findItem(R.id.action_status);
        updateMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_about:
                intent = new Intent(this, ActivityAbout.class);
                try {
                    intent.putExtra(org.md2k.utilities.Constants.VERSION_CODE, String.valueOf(this.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode));
                    intent.putExtra(org.md2k.utilities.Constants.VERSION_NAME, this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                break;
            case R.id.action_copyright:
                intent = new Intent(this, ActivityCopyright.class);
                startActivity(intent);
                break;
            case R.id.action_status:
                showStatus();
                break;
            case R.id.action_settings:
                intent = new Intent(this, ActivityAdmin.class);
                startActivity(intent);
                break;
            case R.id.action_tutorial:
                openPDF();
                break;
            case R.id.action_reset_app:
                intent = new Intent(this, ActivityAppReset.class);
                startActivity(intent);
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    void showStatus() {
        Status status = ModelManager.getInstance(this).getStatus();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("System Status");
        if (status.getRank() > Status.RANK_ADMIN_OPTIONAL) {
            builder.setIcon(R.drawable.ic_error_red_48dp);
            builder.setMessage(status.getMessage());
        } else {
            builder.setIcon(R.drawable.ic_ok_green_48dp);
            builder.setMessage("System is Okay.");
        }
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    void openPDF() {
        if (!FileManager.isExist(Constants.CONFIG_DIRECTORY + "tutorial.pdf"))
            return;
        Intent intent = new Intent();
        intent.setPackage("com.adobe.reader");
        File file = new File(Constants.CONFIG_DIRECTORY + "tutorial.pdf");
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        startActivity(intent);
    }

}
