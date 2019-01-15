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
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import net.micode.notes.data.Notes;
import net.micode.notes.data.Notes.CallNote;
import net.micode.notes.data.Notes.DataColumns;
import net.micode.notes.data.Notes.NoteColumns;
import net.micode.notes.data.Notes.TextNote;

import java.util.ArrayList;


public class Note {
    private ContentValues mNoteDiffValues;
    private NoteData mNoteData;
    private static final String TAG = "Note";
    /**
     * Create a new note id for adding a new note to databases：为向数据库添加新便笺创建新便笺ID
     */
    public static synchronized long getNewNoteId(Context context, long folderId) {
        // Create a new note in the database：
        // 在数据库中创建新便笺
        ContentValues values = new ContentValues();//往数据库中插入数据
        long createdTime = System.currentTimeMillis();//获取当前系统时间
        values.put(NoteColumns.CREATED_DATE, createdTime);//创造日期
        values.put(NoteColumns.MODIFIED_DATE, createdTime);//修改日期
        values.put(NoteColumns.TYPE, Notes.TYPE_NOTE);//两种类型：便签，文件夹
        values.put(NoteColumns.LOCAL_MODIFIED, 1);//本地是否修改的标识
        values.put(NoteColumns.PARENT_ID, folderId);//文件ID就是父ID
        Uri uri = context.getContentResolver().insert(Notes.CONTENT_NOTE_URI, values);
        long noteId = 0;
        try {
            noteId = Long.valueOf(uri.getPathSegments().get(1));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Get note id error :" + e.toString());
            noteId = 0;
        }//获取便笺ID错误
        if (noteId == -1) {
            throw new IllegalStateException("Wrong note id:" + noteId);
        }//错误便签ID
        return noteId;
    }

    public Note() {
        mNoteDiffValues = new ContentValues();
        mNoteData = new NoteData();
    }
    //记录文本值的键盘输入，局部修改以及修改日期
    public void setNoteValue(String key, String value) {
        mNoteDiffValues.put(key, value);
        mNoteDiffValues.put(NoteColumns.LOCAL_MODIFIED, 1);
        mNoteDiffValues.put(NoteColumns.MODIFIED_DATE, System.currentTimeMillis());
    }

    public void setTextData(String key, String value) {
        mNoteData.setTextData(key, value);
    }//设置文本数据

    public void setTextDataId(long id) {
        mNoteData.setTextDataId(id);
    }
    //设置文本数据ID

    public long getTextDataId() {
        return mNoteData.mTextDataId;
    }
    //获取文本数据

    public void setCallDataId(long id) {
        mNoteData.setCallDataId(id);
    }
    //设置调用数据ID
    public void setCallData(String key, String value) {
        mNoteData.setCallData(key, value);
    }
    //设置调用数据
    public boolean isLocalModified() {
        return mNoteDiffValues.size() > 0 || mNoteData.isLocalModified();
    }

    /**
     * 同步更新便签信息
     * @param context
     * @param noteId 需要被更新便签信息的id
     * @return
     */
    public boolean syncNote(Context context, long noteId) {
        //便签名长度小于0，显示“Wrong note id:”
        if (noteId <= 0) {
            throw new IllegalArgumentException("Wrong note id:" + noteId);
        }

        if (!isLocalModified()) {
            return true;
        }

        /**
         * In theory, once data changed, the note should be updated on {@link NoteColumns#LOCAL_MODIFIED} and
         *          * {@link NoteColumns#MODIFIED_DATE}. For data safety, though update note fails, we also update the
         *          * note data info
         */
        /**
         *         理论上，一旦数据发生变化，便签应在链接注释栏本地修改和
         *         链接注释列修改日期。为了数据安全，虽然更新说明失败，但我们也更新了
         *         备注数据信息
         */
        //Update用于修改表中的数据
        if (context.getContentResolver().update(
                //通过withAppendedId方法将noteId添加到uri中，返回带有id的uri
                ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, noteId)
                , mNoteDiffValues, null, null) == 0) {

            Log.e(TAG, "Update note error, should not happen");//更新说明错误，不应发生
            // Do not return, fall through//不要返回
        }
        //调用clear方法清除数据，避免数据叠加
        mNoteDiffValues.clear();

        if (mNoteData.isLocalModified()
                && (mNoteData.pushIntoContentResolver(context, noteId) == null)) {
            return false;
        }
        return true;
    }

    private class NoteData {
        private long mTextDataId;//声明文本数据ID

        private ContentValues mTextDataValues;//文本数据值

        private long mCallDataId;//调用数据ID

        private ContentValues mCallDataValues;//调用数据值

        private static final String TAG = "NoteData";

        public NoteData() {
            mTextDataValues = new ContentValues();
            mCallDataValues = new ContentValues();
            mTextDataId = 0;
            mCallDataId = 0;//设置初始ID长度为0
        }

        boolean isLocalModified() {
            return mTextDataValues.size() > 0 || mCallDataValues.size() > 0;
        }//文本数据值大于0

        void setTextDataId(long id) {
            if(id <= 0) {
                throw new IllegalArgumentException("Text data id should larger than 0");
            }//如果小于0则显示文本数据ID长度应大于0
            mTextDataId = id;
        }

        void setCallDataId(long id) {
            if (id <= 0) {
                throw new IllegalArgumentException("Call data id should larger than 0");
            }//如果小于0则显示调用数据ID应大于0
            mCallDataId = id;
        }

        void setCallData(String key, String value) {
            mCallDataValues.put(key, value);
            mNoteDiffValues.put(NoteColumns.LOCAL_MODIFIED, 1);
            mNoteDiffValues.put(NoteColumns.MODIFIED_DATE, System.currentTimeMillis());
        }//记录调用数据的键盘输入，局部修改以及修改日期

        void setTextData(String key, String value) {
            mTextDataValues.put(key, value);
            mNoteDiffValues.put(NoteColumns.LOCAL_MODIFIED, 1);
            mNoteDiffValues.put(NoteColumns.MODIFIED_DATE, System.currentTimeMillis());
        }//记录文本数据的键盘输入，局部修改以及修改日期

        Uri pushIntoContentResolver(Context context, long noteId) {
            /**
             * Check for safety//检查安全性
             */
            if (noteId <= 0) {
                throw new IllegalArgumentException("Wrong note id:" + noteId);
            }//如果ID小于0则显示错误文本ID为：

            ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder builder = null;
            //Builder设计模式，链式编程生成ContentProviderOperation对象
            //*ContentProviderOperation类：批量更新、插入、删除数据
            if(mTextDataValues.size() > 0) {
                mTextDataValues.put(DataColumns.NOTE_ID, noteId);
                if (mTextDataId == 0) {
                    mTextDataValues.put(DataColumns.MIME_TYPE, TextNote.CONTENT_ITEM_TYPE);
                    Uri uri = context.getContentResolver().insert(Notes.CONTENT_DATA_URI,
                            mTextDataValues);
                    //增加记录：调用ContentResolver.insert()方法，该方法接受一个要增加的记录的目标URI，调用后的返回值是新记录的URI，包含记录号。
                    try {
                        setTextDataId(Long.valueOf(uri.getPathSegments().get(1)));
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Insert new text data fail with noteId" + noteId);//插入新文本数据失败，注释ID为
                        mTextDataValues.clear();
                        return null;}
                    //                try{
                    //                      代码区
                    //              }catch(Exception e){
                    //                      异常处理}
                    //            代码区如果有错误，就会返回所写异常的处理。
                } else {
                    builder = ContentProviderOperation.newUpdate(ContentUris.withAppendedId(
                            Notes.CONTENT_DATA_URI, mTextDataId));
                    builder.withValues(mTextDataValues);
                    operationList.add(builder.build());
                }//创建一个用于执行更新操作的Builder
                mTextDataValues.clear();//清除
            }

            if(mCallDataValues.size() > 0) {
                mCallDataValues.put(DataColumns.NOTE_ID, noteId);
                if (mCallDataId == 0) {
                    mCallDataValues.put(DataColumns.MIME_TYPE, CallNote.CONTENT_ITEM_TYPE);
                    Uri uri = context.getContentResolver().insert(Notes.CONTENT_DATA_URI,
                            mCallDataValues);//增加记录：调用ContentResolver.insert()方法，该方法接受一个要增加的记录的目标URI，调用后的返回值是新记录的URI，包含记录号。
                    try {
                        setCallDataId(Long.valueOf(uri.getPathSegments().get(1)));
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Insert new call data fail with noteId" + noteId);//插入新调用数据失败，注释ID为
                        mCallDataValues.clear();
                        return null;}
                    //                try{
                    //                      代码区
                    //                  } catch(Exception e){
                    //                      异常处理}
                    //            代码区如果有错误，就会返回所写异常的处理。
                } else {
                    builder = ContentProviderOperation.newUpdate(ContentUris.withAppendedId(
                            Notes.CONTENT_DATA_URI, mCallDataId));
                    builder.withValues(mCallDataValues);
                    operationList.add(builder.build());
                }//创建一个用于执行更新操作的Builder
                mCallDataValues.clear();//清除
            }

            if (operationList.size() > 0) {
                try {
                    ContentProviderResult[] results = context.getContentResolver().applyBatch(
                            Notes.AUTHORITY, operationList);//通过applyBatch()函数来应用批量操作
                    return (results == null || results.length == 0 || results[0] == null) ? null
                            : ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, noteId);
                } catch (RemoteException e) {
                    Log.e(TAG, String.format("%s: %s", e.toString(), e.getMessage()));
                    return null;//如果远程异常则进行格式化操作
                } catch (OperationApplicationException e) {
                    Log.e(TAG, String.format("%s: %s", e.toString(), e.getMessage()));
                    return null;//如果操作应用程序异常则进行格式化操作
                }
            }
            return null;
        }
    }
}
