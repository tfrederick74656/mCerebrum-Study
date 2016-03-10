package org.md2k.study.model.app_service;

import android.content.Context;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.study.Status;
import org.md2k.study.config.Application;
import org.md2k.study.config.ConfigManager;
import org.md2k.study.config.Operation;
import org.md2k.study.controller.ModelManager;
import org.md2k.study.controller.UserManager;
import org.md2k.study.model.Model;
import org.md2k.utilities.Report.Log;

import java.util.ArrayList;

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
public class AppServiceManager extends Model {
    public ArrayList<AppService> appServiceList;

    public AppServiceManager(Context context, ConfigManager configManager, DataKitAPI dataKitAPI, Operation operation) {
        super(context, configManager, dataKitAPI, operation);
        appServiceList = new ArrayList<>();
    }

    public void set() {
        ArrayList<Application> applications = configManager.getConfig().getApplication();
        for (int i = 0; i < applications.size(); i++) {
            if (applications.get(i).getService() != null) {
                AppService appService = new AppService(context, applications.get(i).getName(), applications.get(i).getPackage_name(), applications.get(i).getService());
                appServiceList.add(appService);
            }
        }
        lastStatus= new Status(Status.DATAKIT_NOT_AVAILABLE);
    }

    public void clear() {
        appServiceList.clear();
    }

    public void start() {
        update();
    }

    public void stop() {
        for (int i = 0; i < appServiceList.size(); i++)
            appServiceList.get(i).stop();
    }

    public Status getStatus() {
        lastStatus = new Status(Status.SUCCESS);
/*        for (int i = 0; i < appServiceList.size(); i++) {
            Status status = appServiceList.get(i).getStatus();
            if (status.getStatusCode() != Status.SUCCESS)
                lastStatus = status;
        }
*/
        return lastStatus;
    }
    public Status isActive(){
        for(int i=0;i<appServiceList.size();i++){
            if(!appServiceList.get(i).isActive()) return new Status(Status.APP_NOT_RUNNING);
            if(!appServiceList.get(i).isRunning()) return new Status(Status.APP_NOT_RUNNING);
        }
        return new Status(Status.SUCCESS);
    }
    public boolean isValid(){
        UserManager userManager=ModelManager.getInstance(context).getAdminManager();
        for(int i=0;i<userManager.getModel().size();i++){
            if(userManager.getModel().get(i).getOperation()==null) return false;
            if(userManager.getModel().get(i).getOperation().getId()==null) return false;
            if(!userManager.getModel().get(i).getOperation().getId().equals(ModelManager.MODEL_APP_SERVICE))
                if(userManager.getModel().get(i).getStatus().getStatusCode()!=Status.SUCCESS) return false;
        }
        return true;
    }

    public void update() {
        if(!isValid()) return;
        for (int i = 0; i < appServiceList.size(); i++)
            appServiceList.get(i).start();
        lastStatus = new Status(Status.SUCCESS);
    }
}