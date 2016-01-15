package org.md2k.study.view.app_settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import org.md2k.study.R;
import org.md2k.study.Status;
import org.md2k.study.operation.app_settings.AppSettings;
import org.md2k.study.operation.app_settings.AppsSettings;

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
public class PrefsFragmentAppSettings extends PreferenceFragment {

    private static final String TAG = PrefsFragmentAppSettings.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_app_settings);
        setupButtons();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ListView lv = (ListView) v.findViewById(android.R.id.list);
        lv.setPadding(0, 0, 0, 0);
        return v;
    }
    void updatePreference(AppSettings settingsApp) {
        Preference preference = findPreference(settingsApp.getName());
        Status status = settingsApp.getStatus();
        if (status.getStatusCode()==Status.APP_CONFIG_ERROR) {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_error_red_50dp));
            preference.setSummary(status.getStatusMessage());
        } else {
            preference.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ok_teal_50dp));
            preference.setSummary(status.getStatusMessage());
        }
    }
    @Override
    public void onResume(){
        setupAppSettings();
        super.onResume();
    }

    void setupAppSettings(){
        AppsSettings settingsApps = AppsSettings.getInstance(getActivity());
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("key_settings");
        preferenceCategory.removeAll();
        for (int i = 0; i < settingsApps.getAppSettingsList().size(); i++) {
            Preference preference = new Preference(getActivity());
            preference.setTitle(settingsApps.getAppSettingsList().get(i).getName());
            preference.setKey(settingsApps.getAppSettingsList().get(i).getName());
            final int finalI = i;
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AppSettings settingsApp = AppsSettings.getInstance(getActivity()).getAppSettingsList().get(finalI);
                    Intent intent = new Intent();
                    intent.setClassName(settingsApp.getPackage_name(), settingsApp.getSettings());
                    startActivity(intent);
                    return false;
                }
            });
            preferenceCategory.addPreference(preference);
            updatePreference(settingsApps.getAppSettingsList().get(i));

        }
    }
    void setupButtons() {
        final Button button1 = (Button) getActivity().findViewById(R.id.button_1);
        button1.setText("Close");
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

}