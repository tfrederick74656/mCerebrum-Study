package org.md2k.study;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.PlatformId;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.study.config.ConfigManager;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model.data_quality.DataQualityManager;

import java.util.ArrayList;

public class ActivityDataQuality extends ActivityPrivacy {
    ImageView[] imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onStart(){
        super.onStart();
        if(!modelManager.isValid()) return;
        DataQualityManager dataQualityManager = (DataQualityManager) ModelManager.getInstance(this).getUserManager().getModel(ModelManager.MODEL_DATA_QUALITY);
        if (dataQualityManager != null) {
            findViewById(R.id.linear_layout_header_dataquality).setVisibility(View.VISIBLE);
            findViewById(R.id.linear_layout_content_dataquality).setVisibility(View.VISIBLE);
            addImageView();
        } else {
            findViewById(R.id.linear_layout_header_dataquality).setVisibility(View.GONE);
            findViewById(R.id.linear_layout_content_dataquality).setVisibility(View.GONE);
        }
    }

    void addImageView() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout_dataquality_all);
        linearLayout.removeAllViews();
        ArrayList<DataSource> dataSources = ModelManager.getInstance(this).getConfigManager().getConfig().getData_quality();
        imageView = new ImageView[dataSources.size()];
        for (int i = 0; i < dataSources.size(); i++) {
            LinearLayout linearLayoutOne = new LinearLayout(this);
            linearLayoutOne.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f / (dataSources.size()));
            linearLayoutOne.setLayoutParams(LLParams);

            TextView textViewOne = new TextView(this);
            textViewOne.setGravity(Gravity.CENTER_HORIZONTAL);
            ImageView imageViewOne = new ImageView(this);
            linearLayoutOne.addView(textViewOne);
            linearLayoutOne.addView(imageViewOne);
            imageView[i] = imageViewOne;
            linearLayout.addView(linearLayoutOne);
            imageViewOne.setImageResource(R.drawable.ic_error_red_50dp);
            LinearLayout.LayoutParams ll_params_imageView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f / (dataSources.size()));
            ll_params_imageView.gravity = Gravity.CENTER_HORIZONTAL;

            imageViewOne.setLayoutParams(ll_params_imageView);
            imageViewOne.requestLayout();
            imageViewOne.getLayoutParams().height = 60;
            imageViewOne.getLayoutParams().width = 60;
            if (dataSources.get(i).getType() != null) {
                switch (dataSources.get(i).getType()) {
                    case DataSourceType.RESPIRATION:
                        textViewOne.setText("Respiration");
                        break;
                    case DataSourceType.ECG:
                        textViewOne.setText("ECG");
                        break;
                    default:
                        textViewOne.setText("-");
                }
            } else if(dataSources.get(i).getPlatform().getId()!=null){
                switch (dataSources.get(i).getPlatform().getId()) {
                    case PlatformId.LEFT_WRIST:
                        textViewOne.setText("Wrist (L)");
                        break;
                    case PlatformId.RIGHT_WRIST:
                        textViewOne.setText("Wrist (R)");
                        break;
                    default:
                        textViewOne.setText("-");
                }
            }else if(dataSources.get(i).getPlatform().getType()!=null){
                switch (dataSources.get(i).getPlatform().getType()) {
                    case PlatformType.MICROSOFT_BAND:
                        textViewOne.setText("Microsoft Band");
                        break;
                    case PlatformType.AUTOSENSE_WRIST:
                        textViewOne.setText("AutoSense Wrist");
                        break;
                    default:
                        textViewOne.setText("-");
                }
            }
        }
    }

    public void updateDataQuality(Status[] status) {
        Status curStatus = status[0];
        for (int i = 0; i < status.length; i++) {
            switch (status[i].getStatusCode()) {
                case Status.DATAQUALITY_GOOD:
                    imageView[i].setImageResource(R.drawable.ic_ok_teal_50dp);
                    break;
                case Status.DATAQUALITY_OFF:
                    imageView[i].setImageResource(R.drawable.ic_error_red_50dp);
                    curStatus = status[i];
                    break;
                case Status.DATAQUALITY_NOT_WORN:
                    if (curStatus.getStatusCode() != Status.DATAQUALITY_OFF)
                        curStatus = status[i];
                case Status.DATAQUALITY_LOOSE:
                case Status.DATAQUALITY_NOISY:
                    if (curStatus.getStatusCode() != Status.DATAQUALITY_OFF && curStatus.getStatusCode() != Status.DATAQUALITY_NOT_WORN) {
                        if (curStatus.getStatusCode() != Status.DATAQUALITY_OFF)
                            curStatus = status[i];
                    }
                    imageView[i].setImageResource(R.drawable.ic_warning_amber_50dp);
                    break;
            }
        }
        ((TextView) findViewById(R.id.text_view_data_quality_message)).setText(curStatus.getStatusMessage());
        if (curStatus.getStatusCode() == Status.DATAQUALITY_GOOD)
            ((TextView) findViewById(R.id.text_view_data_quality_message)).setTextColor(ContextCompat.getColor(this,R.color.teal_700));
        else
            ((TextView) findViewById(R.id.text_view_data_quality_message)).setTextColor(ContextCompat.getColor(this,R.color.red_900));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
