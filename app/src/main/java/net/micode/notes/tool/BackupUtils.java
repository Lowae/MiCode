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

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import net.micode.notes.R;
import net.micode.notes.data.Notes;
import net.micode.notes.data.Notes.DataColumns;
import net.micode.notes.data.Notes.DataConstants;
import net.micode.notes.data.Notes.NoteColumns;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

//备份
public class BackupUtils {
    private static final String TAG = "BackupUtils";
    // Singleton stuff
    private static BackupUtils sInstance;

    public static synchronized BackupUtils getInstance(Context context) {//同步更新
        if (sInstance == null) {//如果存档为空
            sInstance = new BackupUtils(context);//则存档
        }
        return sInstance;//返回存档
    }

    /**
     * Following states are signs to represents backup or restore //以下状态表示备份或恢复
     * status
     */
    //五种存档时遇到的情况
    // Currently, the sdcard is not mounted //目前，未安装SD卡
    public static final int STATE_SD_CARD_UNMOUONTED           = 0;
    // The backup file not exist //备份文件不存在
    public static final int STATE_BACKUP_FILE_NOT_EXIST        = 1;
    // The data is not well formated, may be changed by other programs
    //数据格式不正确，可能会被其他程序更改
    public static final int STATE_DATA_DESTROIED               = 2;
    // Some run-time exception which causes restore or backup fails
    //导致还原或备份的某些运行时异常失败
    public static final int STATE_SYSTEM_ERROR                 = 3;
    // Backup or restore success
    //备份或恢复成功
    public static final int STATE_SUCCESS                      = 4;

    private TextExport mTextExport;//文本导出

    private BackupUtils(Context context) {
        mTextExport = new TextExport(context);
    }
//将存档的文本导出来
    private static boolean externalStorageAvailable() {//外部储存可用
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }//获取外部存储状态

    public int exportToText() {
        return mTextExport.exportToText();
    }//导出到文本

    public String getExportedTextFileName() {
        return mTextExport.mFileName;
    }
    //获取导出的文本文件名

    public String getExportedTextFileDir() {
        return mTextExport.mFileDirectory;
    }
   //获取导出的文本文件目录
    private static class TextExport {//文本导出
        private static final String[] NOTE_PROJECTION = {
                NoteColumns.ID,
                NoteColumns.MODIFIED_DATE,
                NoteColumns.SNIPPET,
                NoteColumns.TYPE
        };

        private static final int NOTE_COLUMN_ID = 0;//注释列ID为0

        private static final int NOTE_COLUMN_MODIFIED_DATE = 1;//备注栏修改日期为1

        private static final int NOTE_COLUMN_SNIPPET = 2;//备注列代码段为2

        private static final String[] DATA_PROJECTION = {//数据投影
                DataColumns.CONTENT,
                DataColumns.MIME_TYPE,
                DataColumns.DATA1,
                DataColumns.DATA2,
                DataColumns.DATA3,
                DataColumns.DATA4,
        };

        private static final int DATA_COLUMN_CONTENT = 0;
       //数据栏内容为0

        private static final int DATA_COLUMN_MIME_TYPE = 1;
        //数据列的协议类型为1
        private static final int DATA_COLUMN_CALL_DATE = 2;
        //数据列调用日期为2
        private static final int DATA_COLUMN_PHONE_NUMBER = 4;
        //数据列电话号码为4
        private final String [] TEXT_FORMAT;//文本格式

        private static final int FORMAT_FOLDER_NAME          = 0;//设置文件夹名称格式为0
        private static final int FORMAT_NOTE_DATE            = 1;//设置便笺日期格式为1
        private static final int FORMAT_NOTE_CONTENT         = 2;//设置便笺内容格式为2

        private Context mContext;
        private String mFileName;
        private String mFileDirectory;

        public TextExport(Context context) {
            TEXT_FORMAT = context.getResources().getStringArray(R.array.format_for_exported_note);//获取资源获取字符串数组
            mContext = context;
            mFileName = "";
            mFileDirectory = "";
        }

        private String getFormat(int id) {
            return TEXT_FORMAT[id];
        }//获取格式

        /**
         * Export the folder identified by folder id to text//将文件夹ID标识的文件夹导出到文本
         */
        private void exportFolderToText(String folderId, PrintStream ps) {//将文件夹导出到文本
            // Query notes belong to this folder//查询注释属于此文件夹
            Cursor notesCursor = mContext.getContentResolver().query(Notes.CONTENT_NOTE_URI,
                    NOTE_PROJECTION, NoteColumns.PARENT_ID + "=?", new String[] {
                        folderId
                    }, null);//获得ContentResolver对象的值赋给注释光标

            if (notesCursor != null) {//如果注释光标的值不为空
                if (notesCursor.moveToFirst()) {
                    do {
                        // Print note's last modified date//打印便笺的上次修改日期
                        ps.println(String.format(getFormat(FORMAT_NOTE_DATE), DateFormat.format(
                                mContext.getString(R.string.format_datetime_mdhm),
                                notesCursor.getLong(NOTE_COLUMN_MODIFIED_DATE))));
                        // Query data belong to this note//查询数据属于此说明
                        String noteId = notesCursor.getString(NOTE_COLUMN_ID);
                        exportNoteToText(noteId, ps);//将便笺导出到文本
                    } while (notesCursor.moveToNext());
                }
                notesCursor.close();//关闭注释光标
            }
        }

        /**
         * Export note identified by id to a print stream//将ID标识的便笺导出到打印流
         */
        private void exportNoteToText(String noteId, PrintStream ps) {//将便笺导出到文本
            Cursor dataCursor = mContext.getContentResolver().query(Notes.CONTENT_DATA_URI,
                    DATA_PROJECTION, DataColumns.NOTE_ID + "=?", new String[] {
                        noteId
                    }, null);

            if (dataCursor != null) {//如果数据光标不为空
                if (dataCursor.moveToFirst()) {
                    do {
                        String mimeType = dataCursor.getString(DATA_COLUMN_MIME_TYPE);//获取数据列的协议类型
                        if (DataConstants.CALL_NOTE.equals(mimeType)) {
                            // Print phone number//打印电话号码
                            String phoneNumber = dataCursor.getString(DATA_COLUMN_PHONE_NUMBER);//获取数据列电话号码
                            long callDate = dataCursor.getLong(DATA_COLUMN_CALL_DATE);//获取数据列调用日期
                            String location = dataCursor.getString(DATA_COLUMN_CONTENT);//获取数据列内容

                            if (!TextUtils.isEmpty(phoneNumber)) {
                                ps.println(String.format(getFormat(FORMAT_NOTE_CONTENT),
                                        phoneNumber));
                            }
                            // Print call date//打印通话日期
                            ps.println(String.format(getFormat(FORMAT_NOTE_CONTENT), DateFormat
                                    .format(mContext.getString(R.string.format_datetime_mdhm),
                                            callDate)));
                            // Print call attachment location//打印呼叫附件位置
                            if (!TextUtils.isEmpty(location)) {
                                ps.println(String.format(getFormat(FORMAT_NOTE_CONTENT),
                                        location));//打印呼叫附件位置
                            }
                        } else if (DataConstants.NOTE.equals(mimeType)) {
                            String content = dataCursor.getString(DATA_COLUMN_CONTENT);//获取数据列内容
                            if (!TextUtils.isEmpty(content)) {
                                ps.println(String.format(getFormat(FORMAT_NOTE_CONTENT),
                                        content));//打印呼叫附件位置
                            }
                        }
                    } while (dataCursor.moveToNext());
                }
                dataCursor.close();//关闭数据光标
            }
            // print a line separator between note//在便笺之间打印行分隔符
            try {
                ps.write(new byte[] {
                        Character.LINE_SEPARATOR, Character.LETTER_NUMBER
                });
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }

        /**
         * Note will be exported as text which is user readable//注释将导出为用户可读的文本
         */
        public int exportToText() {//导出到文本
            if (!externalStorageAvailable()) {//如果外部储存不可用
                Log.d(TAG, "Media was not mounted");//显示未装入媒体
                return STATE_SD_CARD_UNMOUONTED;//未安装SD卡
            }

            PrintStream ps = getExportToTextPrintStream();//获取导出到文本打印流
            if (ps == null) {
                Log.e(TAG, "get print stream error");//获取打印流错误
                return STATE_SYSTEM_ERROR;//状态系统错误
            }
            // First export folder and its notes//第一个导出文件夹及其注释
            Cursor folderCursor = mContext.getContentResolver().query(
                    Notes.CONTENT_NOTE_URI,
                    NOTE_PROJECTION,
                    "(" + NoteColumns.TYPE + "=" + Notes.TYPE_FOLDER + " AND "
                            + NoteColumns.PARENT_ID + "<>" + Notes.ID_TRASH_FOLER + ") OR "
                            + NoteColumns.ID + "=" + Notes.ID_CALL_RECORD_FOLDER, null, null);

            if (folderCursor != null) {
                if (folderCursor.moveToFirst()) {
                    do {
                        // Print folder's name//打印文件夹的名字
                        String folderName = "";
                        if(folderCursor.getLong(NOTE_COLUMN_ID) == Notes.ID_CALL_RECORD_FOLDER) {//如果注释列ID和ID呼叫记录文件夹相同
                            folderName = mContext.getString(R.string.call_record_folder_name);//文件夹名为呼叫记录文件夹名称
                        } else {
                            folderName = folderCursor.getString(NOTE_COLUMN_SNIPPET);//否则为备注列代码段
                        }
                        if (!TextUtils.isEmpty(folderName)) {
                            ps.println(String.format(getFormat(FORMAT_FOLDER_NAME), folderName));
                            //如果文件名不为空则设置文件夹名称格式和文件夹名字
                        }
                        String folderId = folderCursor.getString(NOTE_COLUMN_ID);//设置注释列ID
                        exportFolderToText(folderId, ps);//将文件夹导出到文本
                    } while (folderCursor.moveToNext());
                }
                folderCursor.close();//关闭文件夹游标
            }

            // Export notes in root's folder//导出根文件夹中的注释
            Cursor noteCursor = mContext.getContentResolver().query(
                    Notes.CONTENT_NOTE_URI,
                    NOTE_PROJECTION,
                    NoteColumns.TYPE + "=" + +Notes.TYPE_NOTE + " AND " + NoteColumns.PARENT_ID
                            + "=0", null, null);

            if (noteCursor != null) {
                if (noteCursor.moveToFirst()) {
                    do {
                        ps.println(String.format(getFormat(FORMAT_NOTE_DATE), DateFormat.format(
                                mContext.getString(R.string.format_datetime_mdhm),
                                noteCursor.getLong(NOTE_COLUMN_MODIFIED_DATE))));//设置便笺日期格式
                        // Query data belong to this note//导出根文件夹中的注释
                        String noteId = noteCursor.getString(NOTE_COLUMN_ID);//设置注释列ID
                        exportNoteToText(noteId, ps);//将便笺导出到文本
                    } while (noteCursor.moveToNext());
                }
                noteCursor.close();//关闭便签游标
            }
            ps.close();

            return STATE_SUCCESS;
        }

        /**
         * Get a print stream pointed to the file {@generateExportedTextFile}//获取指向文件@GenerateExportedTextFile的打印流
         */
        private PrintStream getExportToTextPrintStream() {//获取导出到文本打印流
            File file = generateFileMountedOnSDcard(mContext, R.string.file_path,
                    R.string.file_name_txt_format);//生成安装在SD卡上的文件
            if (file == null) {
                Log.e(TAG, "create file to exported failed");//如果文件为空则创建要导出的文件失败
                return null;
            }
            mFileName = file.getName();//给文件命名
            mFileDirectory = mContext.getString(R.string.file_path);//设置文件目录
            PrintStream ps = null;
            try {
                FileOutputStream fos = new FileOutputStream(file);
                ps = new PrintStream(fos);
            } catch (FileNotFoundException e) {//找不到文件异常
                e.printStackTrace();//打印堆栈跟踪
                return null;
            } catch (NullPointerException e) {//空指针异常
                e.printStackTrace();//打印堆栈跟踪
                return null;
            }//排除异常
            return ps;
        }
    }

    /**
     * Generate the text file to store imported data//生成文本文件以存储导入的数据
     */
    private static File generateFileMountedOnSDcard(Context context, int filePathResId, int fileNameFormatResId) {//生成安装在SD卡上的文件
        StringBuilder sb = new StringBuilder();//字符串生成器
        sb.append(Environment.getExternalStorageDirectory());//获取外部存储目录
        sb.append(context.getString(filePathResId));//获取文件路径资源ID
        File filedir = new File(sb.toString());//创建新文件列表
        sb.append(context.getString(
                fileNameFormatResId,
                DateFormat.format(context.getString(R.string.format_date_ymd),
                        System.currentTimeMillis())));//添加文件格式和日期
        File file = new File(sb.toString());//创建新文件

        try {
            if (!filedir.exists()) {//文件列表不存在
                filedir.mkdir();
            }
            if (!file.exists()) {//文件不存在
                file.createNewFile();
            }
            return file;
        } catch (SecurityException e) {
            e.printStackTrace();//打印堆栈跟踪
        } catch (IOException e) {
            e.printStackTrace();//打印堆栈跟踪
        }//排除异常

        return null;
    }
}


