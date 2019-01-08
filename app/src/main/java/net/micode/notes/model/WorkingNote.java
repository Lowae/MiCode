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

package net.micode.notes.model;

import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import net.micode.notes.data.Notes;
import net.micode.notes.data.Notes.CallNote;
import net.micode.notes.data.Notes.DataColumns;
import net.micode.notes.data.Notes.DataConstants;
import net.micode.notes.data.Notes.NoteColumns;
import net.micode.notes.data.Notes.TextNote;
import net.micode.notes.tool.ResourceParser.NoteBgResources;


public class WorkingNote {
    // Note for the working note
    private Note mNote;
    // Note Id便签ID
    private long mNoteId;
    // Note content便签内容
    private String mContent;
    // Note mode便签模式
    private int mMode;
    //便签提醒日期
    private long mAlertDate;
    //便签修改日期
    private long mModifiedDate;
    //便签颜色ID
    private int mBgColorId;
    //便签控件ID
    private int mWidgetId;
    //便签控件类型
    private int mWidgetType;
    //便签文件夹ID
    private long mFolderId;
    //便签环境
    private Context mContext;
    //便签标签名
    private static final String TAG = "WorkingNote";
    //便签被删除
    private boolean mIsDeleted;
    //设置更改监听器以及状态监听器
    private NoteSettingChangedListener mNoteSettingStatusListener;
    //定义一系列数据投影
    public static final String[] DATA_PROJECTION = new String[] {
            DataColumns.ID,                         //数据列ID
            DataColumns.CONTENT,                   //内容
            DataColumns.MIME_TYPE,                 //类型
            DataColumns.DATA1,                      //数据1，2，3，4
            DataColumns.DATA2,
            DataColumns.DATA3,
            DataColumns.DATA4,
    };
    // 定义一系列标签投影
    public static final String[] NOTE_PROJECTION = new String[] {
            NoteColumns.PARENT_ID,                      //起始ID
            NoteColumns.ALERTED_DATE,                  //闹钟日期
            NoteColumns.BG_COLOR_ID,                   //颜色ID
            NoteColumns.WIDGET_ID,                      //小控件ID
            NoteColumns.WIDGET_TYPE,                     //小控件类型
            NoteColumns.MODIFIED_DATE                   //改进之后的日期
    };
    //定义一些变量的初始值
    private static final int DATA_ID_COLUMN = 0;

    private static final int DATA_CONTENT_COLUMN = 1;

    private static final int DATA_MIME_TYPE_COLUMN = 2;

    private static final int DATA_MODE_COLUMN = 3;

    private static final int NOTE_PARENT_ID_COLUMN = 0;

    private static final int NOTE_ALERTED_DATE_COLUMN = 1;

    private static final int NOTE_BG_COLOR_ID_COLUMN = 2;

    private static final int NOTE_WIDGET_ID_COLUMN = 3;

    private static final int NOTE_WIDGET_TYPE_COLUMN = 4;

    private static final int NOTE_MODIFIED_DATE_COLUMN = 5;

    // New note construct新建便签结构
    private WorkingNote(Context context, long folderId) {
        mContext = context;
        mAlertDate = 0;
        mModifiedDate = System.currentTimeMillis();   //获得当前系统时间
        mFolderId = folderId;
        mNote = new Note();
        mNoteId = 0;
        mIsDeleted = false;
        mMode = 0;
        mWidgetType = Notes.TYPE_WIDGET_INVALIDE;
    }

    // Existing note construct现有便签构造
    private WorkingNote(Context context, long noteId, long folderId) {
        mContext = context;
        mNoteId = noteId;
        mFolderId = folderId;
        mIsDeleted = false;
        mNote = new Note();
        loadNote();
    }
    //Context.getContentResolver().query获取后面的一些信息：文件名ID，颜色ID，小控件ID，小控件类型，闹钟提醒日期，修改日期
    private void loadNote() {
        Cursor cursor = mContext.getContentResolver().query(
                ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, mNoteId), NOTE_PROJECTION, null,
                null, null);
      //第一个参数uri用来唯一的标识ID；第二个参数projection这个参数告诉ID要返回的内容（列Column）；第三个参数selection设置条件null表示不进行筛选
        //第四个参数selectionArgs这个参数是要配合第三个参数使用的，如果你在第三个参数里面有，那么你在selectionArgs写的数据就会替换掉
        //第五个参数，sortOrder，按照什么进行排序，null表示不排序
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                mFolderId = cursor.getLong(NOTE_PARENT_ID_COLUMN);
                mBgColorId = cursor.getInt(NOTE_BG_COLOR_ID_COLUMN);
                mWidgetId = cursor.getInt(NOTE_WIDGET_ID_COLUMN);
                mWidgetType = cursor.getInt(NOTE_WIDGET_TYPE_COLUMN);
                mAlertDate = cursor.getLong(NOTE_ALERTED_DATE_COLUMN);
                mModifiedDate = cursor.getLong(NOTE_MODIFIED_DATE_COLUMN);
            }
            cursor.close();
        } else {
            Log.e(TAG, "No note with id:" + mNoteId);//没有ID的便签为：
            throw new IllegalArgumentException("Unable to find note with id " + mNoteId);//找不到ID为**的便签
        }
        loadNoteData();
    }
      //类似于上面的Context.getContentResolver().query方法
    private void loadNoteData() {
        Cursor cursor = mContext.getContentResolver().query(Notes.CONTENT_DATA_URI, DATA_PROJECTION,
                DataColumns.NOTE_ID + "=?", new String[] {
                        String.valueOf(mNoteId)
                }, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String type = cursor.getString(DATA_MIME_TYPE_COLUMN);
                    if (DataConstants.NOTE.equals(type)) {
                        mContent = cursor.getString(DATA_CONTENT_COLUMN);
                        mMode = cursor.getInt(DATA_MODE_COLUMN);
                        mNote.setTextDataId(cursor.getLong(DATA_ID_COLUMN));
                    } else if (DataConstants.CALL_NOTE.equals(type)) {
                        mNote.setCallDataId(cursor.getLong(DATA_ID_COLUMN));
                    } else {
                        Log.d(TAG, "Wrong note type with type:" + type);//类型为的标签类型错误：
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            Log.e(TAG, "No data with id:" + mNoteId);//没有ID为的数据：
            throw new IllegalArgumentException("Unable to find note's data with id " + mNoteId);//找不到ID为的便笺数据
        }
    }
   //创造新标签
    public static WorkingNote createEmptyNote(Context context, long folderId, int widgetId,
                                              int widgetType, int defaultBgColorId) {
        WorkingNote note = new WorkingNote(context, folderId);
        note.setBgColorId(defaultBgColorId);
        note.setWidgetId(widgetId);
        note.setWidgetType(widgetType);
        return note;
    }
    //标签入口
    public static WorkingNote load(Context context, long id) {
        return new WorkingNote(context, id, 0);
    }

    public synchronized boolean saveNote() {
        if (isWorthSaving()) {
            if (!existInDatabase()) {
                if ((mNoteId = Note.getNewNoteId(mContext, mFolderId)) == 0) {
                    Log.e(TAG, "Create new note fail with id:" + mNoteId);//创建新便笺的ID失败
                    return false;
                }
            }

            mNote.syncNote(mContext, mNoteId);

            /**
             * Update widget content if there exist any widget of this note如果此标签中存在任何小部件，则更新小部件内容
             */
            if (mWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID
                    && mWidgetType != Notes.TYPE_WIDGET_INVALIDE
                    && mNoteSettingStatusListener != null) {
                mNoteSettingStatusListener.onWidgetChanged();//给便签设置监听器
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean existInDatabase() {
        return mNoteId > 0;
    }    //存在于数据库中

    private boolean isWorthSaving() {                              //满足下面条件即可存储
        if (mIsDeleted || (!existInDatabase() && TextUtils.isEmpty(mContent))
                || (existInDatabase() && !mNote.isLocalModified())) {
            return false;
        } else {
            return true;
        }
    }
      //设置状态改变监听器
    public void setOnSettingStatusChangedListener(NoteSettingChangedListener l) {
        mNoteSettingStatusListener = l;
    }
      //设置闹钟提醒日期
    public void setAlertDate(long date, boolean set) {
        if (date != mAlertDate) {
            mAlertDate = date;
            mNote.setNoteValue(NoteColumns.ALERTED_DATE, String.valueOf(mAlertDate));
        }
        if (mNoteSettingStatusListener != null) {
            mNoteSettingStatusListener.onClockAlertChanged(date, set);
        }
    }
    //删除标记
    public void markDeleted(boolean mark) {
        mIsDeleted = mark;
        if (mWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID
                && mWidgetType != Notes.TYPE_WIDGET_INVALIDE && mNoteSettingStatusListener != null) {
            mNoteSettingStatusListener.onWidgetChanged();
        }
    }
     //设置颜色ID
    public void setBgColorId(int id) {
        if (id != mBgColorId) {
            mBgColorId = id;
            if (mNoteSettingStatusListener != null) {
                mNoteSettingStatusListener.onBackgroundColorChanged();
            }
            mNote.setNoteValue(NoteColumns.BG_COLOR_ID, String.valueOf(id));
        }
    }
    //设置检查列表模式
    public void setCheckListMode(int mode) {
        if (mMode != mode) {
            if (mNoteSettingStatusListener != null) {
                mNoteSettingStatusListener.onCheckListModeChanged(mMode, mode);
            }
            mMode = mode;
            mNote.setTextData(TextNote.MODE, String.valueOf(mMode));
        }
    }
     //设置控件类型
    public void setWidgetType(int type) {
        if (type != mWidgetType) {
            mWidgetType = type;
            mNote.setNoteValue(NoteColumns.WIDGET_TYPE, String.valueOf(mWidgetType));
        }
    }
     //设置控件ID
    public void setWidgetId(int id) {
        if (id != mWidgetId) {
            mWidgetId = id;
            mNote.setNoteValue(NoteColumns.WIDGET_ID, String.valueOf(mWidgetId));
        }
    }
    //设置工作文本
    public void setWorkingText(String text) {
        if (!TextUtils.equals(mContent, text)) {
            mContent = text;
            mNote.setTextData(DataColumns.CONTENT, mContent);
        }
    }
    //转换调用便签
    public void convertToCallNote(String phoneNumber, long callDate) {
        mNote.setCallData(CallNote.CALL_DATE, String.valueOf(callDate));
        mNote.setCallData(CallNote.PHONE_NUMBER, phoneNumber);
        mNote.setNoteValue(NoteColumns.PARENT_ID, String.valueOf(Notes.ID_CALL_RECORD_FOLDER));
    }
   //返回一些数值
    public boolean hasClockAlert() {
        return (mAlertDate > 0 ? true : false);
    }

    public String getContent() {
        return mContent;
    }

    public long getAlertDate() {
        return mAlertDate;
    }

    public long getModifiedDate() {
        return mModifiedDate;
    }

    public int getBgColorResId() {
        return NoteBgResources.getNoteBgResource(mBgColorId);
    }

    public int getBgColorId() {
        return mBgColorId;
    }

    public int getTitleBgResId() {
        return NoteBgResources.getNoteTitleBgResource(mBgColorId);
    }

    public int getCheckListMode() {
        return mMode;
    }

    public long getNoteId() {
        return mNoteId;
    }

    public long getFolderId() {
        return mFolderId;
    }

    public int getWidgetId() {
        return mWidgetId;
    }

    public int getWidgetType() {
        return mWidgetType;
    }

    public interface NoteSettingChangedListener {
        /**
         * Called when the background color of current note has just changed当当前便笺的背景色刚刚更改时调用
         */
        void onBackgroundColorChanged();

        /**
         * Called when user set clock当用户设置时钟时调用
         */
        void onClockAlertChanged(long date, boolean set);

        /**
         * Call when user create note from widget当用户从小部件创建便签时调用
         */
        void onWidgetChanged();

        /**
         * Call when switch between check list mode and normal mode在检查列表模式和正常模式之间切换时调用
         * @param oldMode is previous mode before change旧模式是更改前的模式
         * @param newMode is new mode新模式是新模式
         */
        void onCheckListModeChanged(int oldMode, int newMode);
    }
}
