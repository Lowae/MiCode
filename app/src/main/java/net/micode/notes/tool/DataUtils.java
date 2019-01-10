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

package net.micode.notes.tool;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import net.micode.notes.data.Notes;
import net.micode.notes.data.Notes.CallNote;
import net.micode.notes.data.Notes.NoteColumns;
import net.micode.notes.ui.NotesListAdapter.AppWidgetAttribute;

import java.util.ArrayList;
import java.util.HashSet;


public class DataUtils {
    public static final String TAG = "DataUtils";
    public static boolean batchDeleteNotes(ContentResolver resolver, HashSet<Long> ids) {//批量删除注释
        if (ids == null) {
            Log.d(TAG, "the ids is null");//id为空
            return true;
        }
        if (ids.size() == 0) {
            Log.d(TAG, "no id is in the hashset");//容器中没有id
            return true;
        }

        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();//给数组列表分配空间
        for (long id : ids) {
            if(id == Notes.ID_ROOT_FOLDER) {
                Log.e(TAG, "Don't delete system folder root");//不删除系统文件夹根目录
                continue;
            }
            ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newDelete(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, id));//创建复杂对象内容提供程序操作
            operationList.add(builder.build());
        }
        try {
            ContentProviderResult[] results = resolver.applyBatch(Notes.AUTHORITY, operationList);//批量操作
            if (results == null || results.length == 0 || results[0] == null) {
                Log.d(TAG, "delete notes failed, ids:" + ids.toString());//删除便签失败，ID:
                return false;
            }//删除便签
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, String.format("%s: %s", e.toString(), e.getMessage()));
        } catch (OperationApplicationException e) {
            Log.e(TAG, String.format("%s: %s", e.toString(), e.getMessage()));
        }//排除异常
        return false;
    }

    public static void moveNoteToFoler(ContentResolver resolver, long id, long srcFolderId, long desFolderId) {//将便签移到文件夹内
        ContentValues values = new ContentValues();
        values.put(NoteColumns.PARENT_ID, desFolderId);//输入des文件夹id
        values.put(NoteColumns.ORIGIN_PARENT_ID, srcFolderId);//输入src文件夹id
        values.put(NoteColumns.LOCAL_MODIFIED, 1);
        resolver.update(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, id), values, null, null);//更新ID
    }

    public static boolean batchMoveToFolder(ContentResolver resolver, HashSet<Long> ids,
            long folderId) {//批量移动到文件夹
        if (ids == null) {
            Log.d(TAG, "the ids is null");//id为空
            return true;
        }

        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();//给数组列表分配空间
        for (long id : ids) {
            ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newUpdate(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, id));//创建复杂对象内容提供程序操作
            builder.withValue(NoteColumns.PARENT_ID, folderId);
            builder.withValue(NoteColumns.LOCAL_MODIFIED, 1);
            operationList.add(builder.build());//添加操作表
        }

        try {
            ContentProviderResult[] results = resolver.applyBatch(Notes.AUTHORITY, operationList);//通过applyBatch()函数来应用批量操作
            if (results == null || results.length == 0 || results[0] == null) {
                Log.d(TAG, "delete notes failed, ids:" + ids.toString());//删除便签错误，id：
                return false;
            }
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, String.format("%s: %s", e.toString(), e.getMessage()));
        } catch (OperationApplicationException e) {
            Log.e(TAG, String.format("%s: %s", e.toString(), e.getMessage()));
        }//排除异常
        return false;
    }

    /**
     * Get the all folder count except system folders {@link Notes#TYPE_SYSTEM}}//获取除系统文件夹@link notes type系统以外的所有文件夹计数
     */
    public static int getUserFolderCount(ContentResolver resolver) {//获取用户文件夹计数
        Cursor cursor =resolver.query(Notes.CONTENT_NOTE_URI,
                new String[] { "COUNT(*)" },
                NoteColumns.TYPE + "=? AND " + NoteColumns.PARENT_ID + "<>?",
                new String[] { String.valueOf(Notes.TYPE_FOLDER), String.valueOf(Notes.ID_TRASH_FOLER)},
                null);//游标查询文件夹类型和父ID

        int count = 0;
        if(cursor != null) {//游标不为空
            if(cursor.moveToFirst()) {
                try {
                    count = cursor.getInt(0);//获取文件夹计数
                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, "get folder count failed:" + e.toString());//获取文件夹计数失败
                } finally {
                    cursor.close();//关闭游标
                }
            }
        }
        return count;//返回文件夹数量
    }

    public static boolean visibleInNoteDatabase(ContentResolver resolver, long noteId, int type) {//在便签数据库中可见
        Cursor cursor = resolver.query(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, noteId),
                null,
                NoteColumns.TYPE + "=? AND " + NoteColumns.PARENT_ID + "<>" + Notes.ID_TRASH_FOLER,
                new String [] {String.valueOf(type)},
                null);//查询便签id

        boolean exist = false;
        if (cursor != null) {//游标不为空
            if (cursor.getCount() > 0) {
                exist = true;
            }
            cursor.close();//关闭游标
        }
        return exist;
    }

    public static boolean existInNoteDatabase(ContentResolver resolver, long noteId) {//判断是否存在于便笺数据库中
        Cursor cursor = resolver.query(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, noteId),
                null, null, null, null);//查询便签的id

        boolean exist = false;
        if (cursor != null) {//游标不为空
            if (cursor.getCount() > 0) {
                exist = true;//存在
            }
            cursor.close();//关闭游标
        }
        return exist;
    }

    public static boolean existInDataDatabase(ContentResolver resolver, long dataId) {//判断数据库中的数据是否存在
        Cursor cursor = resolver.query(ContentUris.withAppendedId(Notes.CONTENT_DATA_URI, dataId),
                null, null, null, null);//查询

        boolean exist = false;
        if (cursor != null) {//游标不为空
            if (cursor.getCount() > 0) {
                exist = true;//存在
            }
            cursor.close();//关闭游标
        }
        return exist;
    }

    public static boolean checkVisibleFolderName(ContentResolver resolver, String name) {//检查可见文件夹名称
        Cursor cursor = resolver.query(Notes.CONTENT_NOTE_URI, null,
                NoteColumns.TYPE + "=" + Notes.TYPE_FOLDER +
                " AND " + NoteColumns.PARENT_ID + "<>" + Notes.ID_TRASH_FOLER +
                " AND " + NoteColumns.SNIPPET + "=?",
                new String[] { name }, null);//查询类型、文件夹类型、父ID、垃圾桶ID、片段
        boolean exist = false;
        if(cursor != null) {//游标不为空
            if(cursor.getCount() > 0) {
                exist = true;//存在
            }
            cursor.close();//关闭游标
        }
        return exist;
    }

    public static HashSet<AppWidgetAttribute> getFolderNoteWidget(ContentResolver resolver, long folderId) {//获取文件夹注释小部件
        Cursor c = resolver.query(Notes.CONTENT_NOTE_URI,
                new String[] { NoteColumns.WIDGET_ID, NoteColumns.WIDGET_TYPE },
                NoteColumns.PARENT_ID + "=?",
                new String[] { String.valueOf(folderId) },
                null);//查询小控件id和类型

        HashSet<AppWidgetAttribute> set = null;
        if (c != null) {
            if (c.moveToFirst()) {
                set = new HashSet<AppWidgetAttribute>();
                do {
                    try {
                        AppWidgetAttribute widget = new AppWidgetAttribute();
                        widget.widgetId = c.getInt(0);
                        widget.widgetType = c.getInt(1);
                        set.add(widget);
                    } catch (IndexOutOfBoundsException e) {
                        Log.e(TAG, e.toString());//显示错误参数
                    }
                } while (c.moveToNext());//排除异常
            }
            c.close();//关闭游标
        }
        return set;
    }

    public static String getCallNumberByNoteId(ContentResolver resolver, long noteId) {//按便笺ID获取电话号码
        Cursor cursor = resolver.query(Notes.CONTENT_DATA_URI,
                new String [] { CallNote.PHONE_NUMBER },
                CallNote.NOTE_ID + "=? AND " + CallNote.MIME_TYPE + "=?",
                new String [] { String.valueOf(noteId), CallNote.CONTENT_ITEM_TYPE },
                null);//根据便签的ID类型查询电话号码

        if (cursor != null && cursor.moveToFirst()) {
            try {
                return cursor.getString(0);
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "Get call number fails " + e.toString());//获取呼叫号码失败
            } finally {
                cursor.close();//关闭游标
            }
        }//排除异常
        return "";
    }

    public static long getNoteIdByPhoneNumberAndCallDate(ContentResolver resolver, String phoneNumber, long callDate) {
        //通过电话号码和通话日期获取便条ID
        Cursor cursor = resolver.query(Notes.CONTENT_DATA_URI,
                new String [] { CallNote.NOTE_ID },
                CallNote.CALL_DATE + "=? AND " + CallNote.MIME_TYPE + "=? AND PHONE_NUMBERS_EQUAL("
                + CallNote.PHONE_NUMBER + ",?)",
                new String [] { String.valueOf(callDate), CallNote.CONTENT_ITEM_TYPE, phoneNumber },
                null);//通过电话号码和通话日期获取便条ID

        if (cursor != null) {//游标不为空
            if (cursor.moveToFirst()) {
                try {
                    return cursor.getLong(0);
                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, "Get call note id fails " + e.toString());//获取通话记录ID失败
                }
            }
            cursor.close();//关闭游标
        }
        return 0;
    }

    public static String getSnippetById(ContentResolver resolver, long noteId) {
        Cursor cursor = resolver.query(Notes.CONTENT_NOTE_URI,
                new String [] { NoteColumns.SNIPPET },
                NoteColumns.ID + "=?",
                new String [] { String.valueOf(noteId)},
                null);//按ID获取代码段

        if (cursor != null) {//游标不为空
            String snippet = "";
            if (cursor.moveToFirst()) {
                snippet = cursor.getString(0);
            }
            cursor.close();
            return snippet;
        }
        throw new IllegalArgumentException("Note is not found with id: " + noteId);//找不到ID为便签ID的便笺
    }//排除异常

    public static String getFormattedSnippet(String snippet) {//获取格式化的代码段
        if (snippet != null) {
            snippet = snippet.trim();//片段修剪
            int index = snippet.indexOf('\n');//int indexOf(String str) 返回第一次出现的指定子字符串在此字符串中的索引位置。
            if (index != -1) {
                snippet = snippet.substring(0, index);//substring() 方法返回的子串包括 start 处的字符，但不包括 stop 处的字符
            }
        }
        return snippet;
    }
}
