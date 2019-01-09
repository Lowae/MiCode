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

package net.micode.notes.data;

import android.net.Uri;
//定义note类
public class Notes {
    public static final String AUTHORITY = "micode_notes";
    public static final String TAG = "Notes";
    public static final int TYPE_NOTE     = 0;
    public static final int TYPE_FOLDER   = 1;
    public static final int TYPE_SYSTEM   = 2;

    /**
     * Following IDs are system folders' identifiers
     * {@link Notes#ID_ROOT_FOLDER } is default folder
     * {@link Notes#ID_TEMPARAY_FOLDER } is for notes belonging no folder
     * {@link Notes#ID_CALL_RECORD_FOLDER} is to store call records
     */
    //默认根文件夹ID
    public static final int ID_ROOT_FOLDER = 0;
    //临时文件夹ID
    public static final int ID_TEMPARAY_FOLDER = -1;
    //存储通话记录文件夹的ID
    public static final int ID_CALL_RECORD_FOLDER = -2;
    //回收站ID
    public static final int ID_TRASH_FOLER = -3;

    //可选择的日期
    public static final String INTENT_EXTRA_ALERT_DATE = "net.micode.notes.alert_date";
    //背景颜色
    public static final String INTENT_EXTRA_BACKGROUND_ID = "net.micode.notes.background_color_id";
    //控件ID
    public static final String INTENT_EXTRA_WIDGET_ID = "net.micode.notes.widget_id";
    //控件类型
    public static final String INTENT_EXTRA_WIDGET_TYPE = "net.micode.notes.widget_type";
    //文件夹ID
    public static final String INTENT_EXTRA_FOLDER_ID = "net.micode.notes.folder_id";
    //通话信息
    public static final String INTENT_EXTRA_CALL_DATE = "net.micode.notes.call_date";

    //控件类型的无效标识
    public static final int TYPE_WIDGET_INVALIDE      = -1;
    //两倍控件类型
    public static final int TYPE_WIDGET_2X            = 0;
    //四倍控件类型
    public static final int TYPE_WIDGET_4X            = 1;

    //数据存储器类
    public static class DataConstants {
        public static final String NOTE = TextNote.CONTENT_ITEM_TYPE;
        public static final String CALL_NOTE = CallNote.CONTENT_ITEM_TYPE;
    }

    /**
     * 用于查询所有笔记和文件夹的URI
     */
    public static final Uri CONTENT_NOTE_URI = Uri.parse("content://" + AUTHORITY + "/note");

    /**
     * 用于查询数据的URI
     */
    public static final Uri CONTENT_DATA_URI = Uri.parse("content://" + AUTHORITY + "/data");

    public interface NoteColumns {
        /**
         * 唯一行ID
         * <P> 数据类型: INTEGER (long) </P>
         */
        public static final String ID = "_id";

        /**
         * 便笺或文件夹的父级ID
         * <P> 数据类型: INTEGER (long) </P>
         */
        public static final String PARENT_ID = "parent_id";

        /**
         * 给便签或文件夹设置日期
         * <P> 数据类型: INTEGER (long) </P>
         */
        public static final String CREATED_DATE = "created_date";

        /**
         * 最新修改日期
         * <P> 数据类型: INTEGER (long) </P>
         */
        public static final String MODIFIED_DATE = "modified_date";


        /**
         * //闹钟提醒的日期
         * <P> 数据类型: INTEGER (long) </P>
         */
        public static final String ALERTED_DATE = "alert_date";

        /**
         * 便签的文本信息或文件夹名字
         * <P> 数据类型: TEXT </P>
         */
        public static final String SNIPPET = "snippet";

        /**
         * 便签的控件ID
         * <P> 数据类型: INTEGER (long) </P>
         */
        public static final String WIDGET_ID = "widget_id";

        /**
         * 便签的控件类型
         * <P> 数据类型: INTEGER (long) </P>
         */
        public static final String WIDGET_TYPE = "widget_type";

        /**
         * 便签的背景颜色ID
         * <P> 数据类型: INTEGER (long) </P>
         */
        public static final String BG_COLOR_ID = "bg_color_id";

        /**
         *
         * 便签是否含有附件,普通便签没有附件,多媒体便签至少含一个.
         * <P> 数据类型: INTEGER </P>
         */
        public static final String HAS_ATTACHMENT = "has_attachment";

        /**
         * 文件夹的数量
         * <P> 数据类型: INTEGER (long) </P>
         */
        public static final String NOTES_COUNT = "notes_count";

        /**
         * 文件的类型:文件夹或便签
         * <P> 数据类型: INTEGER </P>
         */
        public static final String TYPE = "type";

        /**
         * 最新的同步ID
         * <P> 数据类型: INTEGER (long) </P>
         */
        public static final String SYNC_ID = "sync_id";

        /**
         * 是否本地修改的标志
         * <P> 数据类型: INTEGER </P>
         */
        public static final String LOCAL_MODIFIED = "local_modified";

        /**
         * 移动到临时文件夹前的原始父ID
         * <P> 数据类型 : INTEGER </P>
         */
        public static final String ORIGIN_PARENT_ID = "origin_parent_id";

        /**
         * 谷歌账号ID
         * <P> 数据类型 : TEXT </P>
         */
        public static final String GTASK_ID = "gtask_id";

        /**
         * 版本号
         * <P> 数据类型 : INTEGER (long) </P>
         */
        public static final String VERSION = "version";
    }

    public interface DataColumns {
        /**
         * 任意行对应的唯一ID
         * <P> 数据类型: INTEGER (long) </P>
         */
        public static final String ID = "_id";

        /**
         * 此行表示的项的媒体类型
         *
         * <P> 数据类型: Text </P>
         */
        public static final String MIME_TYPE = "mime_type";

        /**
         * 这个数据归属的便签的引用ID
         * <P> 数据类型: INTEGER (long) </P>
         */
        public static final String NOTE_ID = "note_id";

        /**
         * 给便签或文件夹创建数据
         * <P> 数据类型: INTEGER (long) </P>
         */
        public static final String CREATED_DATE = "created_date";

        /**
         * 最新修改的日期
         * <P> 数据类型: INTEGER (long) </P>
         */
        public static final String MODIFIED_DATE = "modified_date";

        /**
         * 数据的内容
         * <P> 数据类型: TEXT </P>
         */
        public static final String CONTENT = "content";


        /**
         * 通用数据列，含义为@link mimetype特定，用作整数数据类型
         * <P> 数据类型: INTEGER </P>
         */
        public static final String DATA1 = "data1";

        /**
         * 通用数据列，含义为@link mimetype特定，用作整数数据类型
         * <P> 数据类型: INTEGER </P>
         */
        public static final String DATA2 = "data2";

        /**
         * 通用数据列，含义为@link mimetype特定，用作文本数据类型
         * <P> 数据类型: TEXT </P>
         */
        public static final String DATA3 = "data3";

        /**
         * 通用数据列，含义为@link mimetype特定，用作文本数据类型
         * <P> 数据类型: TEXT </P>
         */
        public static final String DATA4 = "data4";

        /**
         * 通用数据列，含义为@link mimetype特定，用作文本数据类型
         * <P> 数据类型: TEXT </P>
         */
        public static final String DATA5 = "data5";
    }

    public static final class TextNote implements DataColumns {
        /**
         * 是否显示检查列表模式中的文本的模式
         * <P> 数据类型: Integer 1:check list mode 0: normal mode </P>
         */
        public static final String MODE = DATA1;

        public static final int MODE_CHECK_LIST = 1;

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/text_note";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/text_note";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/text_note");
    }

    public static final class CallNote implements DataColumns {
        /**
         * 此记录的调用日期
         * <P> 数据类型: INTEGER (long) </P>
         */
        public static final String CALL_DATE = DATA1;

        /**
         * 此记录中的电话号码
         * <P> 数据类型: TEXT </P>
         */
        public static final String PHONE_NUMBER = DATA3;

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/call_note";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/call_note";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/call_note");
    }
}
