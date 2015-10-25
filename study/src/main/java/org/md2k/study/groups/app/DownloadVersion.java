package org.md2k.study.groups.app;

import android.os.AsyncTask;

import org.md2k.study.OnTaskCompleted;
import org.md2k.utilities.Report.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
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
public class DownloadVersion extends AsyncTask<String, Void, String> {
    private static final String TAG = DownloadVersion.class.getSimpleName();
    private OnTaskCompleted onTaskCompleted;
    public DownloadVersion(OnTaskCompleted onTaskCompleted) {
        this.onTaskCompleted=onTaskCompleted;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        String versionName=null;
        try {
            URL url = new URL(sUrl[0]);

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str,str1="";
            while ((str = in.readLine()) != null) {
                str1+=str;
            }
            if(str1.contains("<title>") && str1.contains("</title>")){
                int start_id=str1.indexOf("<title>")+7;
                int end_id=str1.indexOf("</title>");

                str=str1.substring(start_id,end_id);
                String[] s=str.split(" ");
                if(s.length>=2)
                    versionName=s[1];
            }
            in.close();
        } catch (Exception e) {
            Log.e(TAG,"error="+e.toString());
        }
        Log.d(TAG,"version="+versionName);
        return versionName;
    }
    @Override
    protected void onPostExecute(String versionNumber) {
        onTaskCompleted.onTaskCompleted(versionNumber);

    }
}
