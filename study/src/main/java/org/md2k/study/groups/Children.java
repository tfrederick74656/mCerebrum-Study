package org.md2k.study.groups;

import android.content.Context;
import android.view.View;

import org.md2k.study.OnDataUpdated;
import org.md2k.study.R;

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
public class Children {
    public String name;
    public int status;
    public Context context;
    public OnDataUpdated onDataUpdated;
    public int buttonVisiblilty= View.VISIBLE;
    public int statusImage;
    public String buttonText;
    public int buttonBackground;
    public Children(Context context, String name, OnDataUpdated onDataUpdated){
        this.context=context;
        this.name=name;
        this.onDataUpdated=onDataUpdated;
        status=GroupManager.RED;
        statusImage= R.drawable.red;
        buttonText="Fix";
        buttonBackground=R.drawable.button_red;
    }
    public void updateStatus(){
        buttonText="Fix";
        statusImage = R.drawable.red;
        buttonVisiblilty=View.VISIBLE;
        buttonBackground=R.drawable.button_red;

        switch(status){
            case GroupManager.GREEN:
                statusImage = R.drawable.green;
                buttonVisiblilty=View.INVISIBLE;
                buttonBackground=R.drawable.button_green;
                break;
            case GroupManager.YELLOW:
                statusImage = R.drawable.yellow;
                buttonVisiblilty=View.VISIBLE;
                buttonBackground=R.drawable.button_yellow;
                break;
            case GroupManager.RED:
                statusImage = R.drawable.red;
                buttonVisiblilty=View.VISIBLE;
                buttonBackground=R.drawable.button_red;
                break;
        }
    }
    public String getName(){
        return name;
    }

    public View.OnClickListener onClickListener;
    public void refresh(){
    }
}
