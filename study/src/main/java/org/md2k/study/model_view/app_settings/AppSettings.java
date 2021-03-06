package org.md2k.study.model_view.app_settings;


import org.md2k.datakitapi.source.AbstractObject;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigApp;
import org.md2k.utilities.FileManager;

import java.io.File;
import java.io.FileNotFoundException;
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
class AppSettings {
    private static final String TAG = AppSettings.class.getSimpleName();
    private ConfigApp app;
    private int rank;

    public AppSettings(ConfigApp app, int rank) {
        this.app = app;
        this.rank=rank;
    }

    public Status getStatus(){
        if(!isEqual()) return new Status(rank,Status.APP_CONFIG_ERROR);
        return new Status(rank,Status.SUCCESS);
    }
    private boolean isDataSourceMatch(DataSource dataSource, ArrayList<DataSource> dataSourcesDefault){
        for(int i=0;i<dataSourcesDefault.size();i++){
            DataSource dataSourceDefault=dataSourcesDefault.get(i);
            if(isEqualDataSource(dataSource, dataSourceDefault)) return true;
        }
        return false;
    }
    private boolean isEqualDataSource(DataSource dataSource, DataSource dataSourceDefault){
        if(!isFieldMatch(dataSource.getId(), dataSourceDefault.getId())) return false;
        if(!isFieldMatch(dataSource.getType(), dataSourceDefault.getType())) return false;
        if(!isMetaDataMatch(dataSource.getMetadata(), dataSourceDefault.getMetadata())) return false;
        if(!isObjectMatch(dataSource.getPlatform(), dataSourceDefault.getPlatform())) return false;
        if(!isObjectMatch(dataSource.getPlatformApp(), dataSourceDefault.getPlatformApp())) return false;
        if(!isObjectMatch(dataSource.getApplication(), dataSourceDefault.getApplication())) return false;
        return true;
    }
    private boolean isObjectMatch(AbstractObject object, AbstractObject objectDefault){
        if(objectDefault==null) return true;
        if(object==null) return false;
        if(!isFieldMatch(object.getId(), objectDefault.getId())) return false;
        if(!isFieldMatch(object.getType(), objectDefault.getType())) return false;
        return true;
    }
    private boolean isMetaDataMatch(HashMap<String, String> metadata, HashMap<String, String> metadataDefault){
        String valueDefault, value;
        if(metadataDefault==null) return true;
        if(metadata==null) return false;
        for(String key:metadataDefault.keySet()){
            if(!metadata.containsKey(key)) return false;
            valueDefault=metadataDefault.get(key);
            value=metadata.get(key);
            if(!value.equals(valueDefault))return false;
        }
        return true;
    }
    private boolean isFieldMatch(String value, String valueDefault){
        if(valueDefault==null) return true;
        if(value==null) return false;
        if(value.equals(valueDefault)) return true;
        return false;
    }
    boolean isEqual(){
        if(app.getConfig()!=null && !FileManager.isExist(Constants.CONFIG_DIRECTORY_BASE+app.getPackage_name()+File.separator+app.getConfig()))
            return false;
        if(app.getDefault_config()==null) return true;
//        if(!(app.getPackage_name().equals("org.md2k.phonesensor") || app.getPackage_name().equals("org.md2k.autosense") || app.getPackage_name().equals("org.md2k.microsoftband") || app.getPackage_name().equals("org.md2k.plotter"))) {
//            //TODO: need to clear the code
//            return true;
//        }
        String outDir= Constants.CONFIG_DIRECTORY_BASE+ app.getPackage_name()+ File.separator;
        try {
            ArrayList<DataSource> dataSourcesDefault = FileManager.readJSONArray(outDir, app.getDefault_config(),DataSource.class);
            ArrayList<DataSource> dataSources = FileManager.readJSONArray(outDir, app.getConfig(),DataSource.class);
            if(dataSources.size()!=dataSourcesDefault.size()) return false;
            for(int i=0;i<dataSources.size();i++){
                if(!isDataSourceMatch(dataSources.get(i), dataSourcesDefault))
                    return false;
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }catch (IllegalStateException e){
            return true;
        }catch(Exception e){
            return true;
        }
    }

    public String getName() {
        return app.getName();
    }

    public String getPackage_name() {
        return app.getPackage_name();
    }

    public String getDefault_config() {
        return app.getDefault_config();
    }

    public String getConfig() {
        return app.getConfig();
    }

    public String getSettings() {
        return app.getSettings();
    }
}
