/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.notes.gtask.data;

import android.database.Cursor;

import org.json.JSONObject;
public abstract class Node {
    public static final int SYNC_ACTION_NONE = 0;//没有同步操作

    public static final int SYNC_ACTION_ADD_REMOTE = 1;//从本地上传

    public static final int SYNC_ACTION_ADD_LOCAL = 2;//从网上同步

    public static final int SYNC_ACTION_DEL_REMOTE = 3;//删除远程数据

    public static final int SYNC_ACTION_DEL_LOCAL = 4;//删除本地数据

    public static final int SYNC_ACTION_UPDATE_REMOTE = 5;//更新远程数据

    public static final int SYNC_ACTION_UPDATE_LOCAL = 6;//更新本地数据

    public static final int SYNC_ACTION_UPDATE_CONFLICT = 7;//更新有数据冲突

    public static final int SYNC_ACTION_ERROR = 8;//同步操作错误

    private String mGid;

    private String mName;

    private long mLastModified;

    private boolean mDeleted;

    //定义一个数据节点类
    public Node() {
        mGid = null;
        mName = "";
        mLastModified = 0;
        mDeleted = false;
    }

    //在Task中对方法有详细定义
    public abstract JSONObject getCreateAction(int actionId);

    public abstract JSONObject getUpdateAction(int actionId);

    public abstract void setContentByRemoteJSON(JSONObject js);

    public abstract void setContentByLocalJSON(JSONObject js);

    public abstract JSONObject getLocalJSONFromContent();

    public abstract int getSyncAction(Cursor c);

    //set设定数据操作
    public void setGid(String gid) {
        this.mGid = gid;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setLastModified(long lastModified) {
        this.mLastModified = lastModified;
    }

    public void setDeleted(boolean deleted) {
        this.mDeleted = deleted;
    }

    //get获取数据操作
    public String getGid() {
        return this.mGid;
    }

    public String getName() {
        return this.mName;
    }

    public long getLastModified() {
        return this.mLastModified;
    }

    public boolean getDeleted() {
        return this.mDeleted;
    }

}
