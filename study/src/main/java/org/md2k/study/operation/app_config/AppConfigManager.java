package org.md2k.study.operation.app_config;

import android.content.Context;

import org.md2k.study.Constants;
import org.md2k.study.Status;
import org.md2k.study.config.ConfigInfo;
import org.md2k.study.config.StudyConfigManager;
import org.md2k.utilities.Files;

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
public class AppConfigManager {
    String id;
    String name;
    String version;
    ArrayList<String> required_files;

    public AppConfigManager(Context context) {
        ConfigInfo configInfo= StudyConfigManager.getInstance(context).getStudyConfig().getConfig_info();
        this.id = configInfo.getId();
        this.name =configInfo.getName();
        this.version = configInfo.getVersion();
        this.required_files = configInfo.getRequired_files();
    }
    public Status getStatus(){
        String directory= Constants.CONFIG_DIRECTORY;
        if(required_files ==null || required_files.size()==0) return new Status(Status.SUCCESS);
        for(int i=0;i< required_files.size();i++){
            if(!Files.isExist(directory+ required_files.get(i)))
                return new Status(Status.CONFIG_FILE_NOT_EXIST);
        }
        return new Status(Status.SUCCESS);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public ArrayList<String> getRequired_files() {
        return required_files;
    }
}