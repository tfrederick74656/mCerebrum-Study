package org.md2k.study.model_view.selfreport;

import com.google.gson.Gson;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataTypeString;
import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platform.PlatformType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.study.Status;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.model_view.Model;
import org.md2k.utilities.Report.Log;
import org.md2k.utilities.data_format.DayTypeInfo;
import org.md2k.utilities.data_format.Event;

import java.util.ArrayList;
import java.util.HashMap;

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
public class SelfReportManager extends Model {
    private static final String TAG = SelfReportManager.class.getSimpleName();
    DataSourceBuilder dataSourceBuilder;
    DataSourceClient dataSourceClient;
    DataKitAPI dataKitAPI;

    public SelfReportManager(ModelManager modelManager, String id, int rank) {
        super(modelManager, id, rank);
        Log.d(TAG, "constructor..id=" + id + " rank=" + rank);
    }

    public void clear(){
        status=new Status(rank, Status.NOT_DEFINED);
    }
    public void set(){
        dataKitAPI =DataKitAPI.getInstance(modelManager.getContext());
        dataSourceBuilder = createDataSourceBuilder();
        dataSourceClient = dataKitAPI.register(createDataSourceBuilder());
        Status lastStatus;
        lastStatus= new Status(rank,Status.SUCCESS);
        notifyIfRequired(lastStatus);
    }

    private boolean writeToDataKit() {
        if (!dataKitAPI.isConnected()) return false;
        Gson gson = new Gson();
        String sample = gson.toJson(new Event(Event.SMOKING, Event.TYPE_SELF_REPORT));
        DataTypeString dataTypeString = new DataTypeString(DateTime.getDateTime(), sample);
        dataKitAPI.insert(dataSourceClient, dataTypeString);
        return true;
    }
    DataSourceBuilder createDataSourceBuilder() {
        Platform platform = new PlatformBuilder().setType(PlatformType.PHONE).build();
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.EVENT).setPlatform(platform);
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.NAME, "Event");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DESCRIPTION, "Event with type (ex: smoking, selfreport)");
        dataSourceBuilder = dataSourceBuilder.setMetadata(METADATA.DATA_TYPE, DataTypeString.class.getName());
        dataSourceBuilder = dataSourceBuilder.setDataDescriptors(createDataDescriptors());
        return dataSourceBuilder;
    }

    ArrayList<HashMap<String, String>> createDataDescriptors() {
        ArrayList<HashMap<String, String>> dataDescriptors = new ArrayList<>();
        dataDescriptors.add(createDescriptor("Event with type (ex: smoking, selfreport)"));
        return dataDescriptors;
    }

    HashMap<String, String> createDescriptor(String name) {
        HashMap<String, String> dataDescriptor = new HashMap<>();
        dataDescriptor.put(METADATA.NAME, name);
        dataDescriptor.put(METADATA.UNIT, "String");
        dataDescriptor.put(METADATA.DESCRIPTION, name);
        dataDescriptor.put(METADATA.DATA_TYPE, DayTypeInfo.class.getName());
        return dataDescriptor;
    }
    public void save(){
        writeToDataKit();
    }
}

