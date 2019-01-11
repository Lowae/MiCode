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

public class GTaskStringUtils {//gtask字符串实用程序
    // static修饰的属性强调它们只有一个，final修饰的属性表明是一个常数（创建后不能被修改）。
    // static final修饰的属性表示一旦给值，就不可修改，并且可以通过类名访问。
    public final static String GTASK_JSON_ACTION_ID = "action_id";//操作ID

    public final static String GTASK_JSON_ACTION_LIST = "action_list";//操作列表

    public final static String GTASK_JSON_ACTION_TYPE = "action_type";//操作类型

    public final static String GTASK_JSON_ACTION_TYPE_CREATE = "create";//创造操作类型

    public final static String GTASK_JSON_ACTION_TYPE_GETALL = "get_all";//获取全部操作类型

    public final static String GTASK_JSON_ACTION_TYPE_MOVE = "move";//移动操作类型

    public final static String GTASK_JSON_ACTION_TYPE_UPDATE = "update";//更新操作类型

    public final static String GTASK_JSON_CREATOR_ID = "creator_id";//创造者ID

    public final static String GTASK_JSON_CHILD_ENTITY = "child_entity";//子实体

    public final static String GTASK_JSON_CLIENT_VERSION = "client_version";//客户端版本

    public final static String GTASK_JSON_COMPLETED = "completed";//已完成

    public final static String GTASK_JSON_CURRENT_LIST_ID = "current_list_id";//当前列表ID

    public final static String GTASK_JSON_DEFAULT_LIST_ID = "default_list_id";//缺省列表ID

    public final static String GTASK_JSON_DELETED = "deleted";//删除

    public final static String GTASK_JSON_DEST_LIST = "dest_list";//目的表

    public final static String GTASK_JSON_DEST_PARENT = "dest_parent";//目的父类

    public final static String GTASK_JSON_DEST_PARENT_TYPE = "dest_parent_type";//目标父类类型

    public final static String GTASK_JSON_ENTITY_DELTA = "entity_delta";//实体增量

    public final static String GTASK_JSON_ENTITY_TYPE = "entity_type";//实体类型

    public final static String GTASK_JSON_GET_DELETED = "get_deleted";//已经删除

    public final static String GTASK_JSON_ID = "id";//id

    public final static String GTASK_JSON_INDEX = "index";//索引

    public final static String GTASK_JSON_LAST_MODIFIED = "last_modified";//最后修改

    public final static String GTASK_JSON_LATEST_SYNC_POINT = "latest_sync_point";//最新同步点

    public final static String GTASK_JSON_LIST_ID = "list_id";//清单id

    public final static String GTASK_JSON_LISTS = "lists";//清单

    public final static String GTASK_JSON_NAME = "name";//名字

    public final static String GTASK_JSON_NEW_ID = "new_id";//新id

    public final static String GTASK_JSON_NOTES = "notes";//便签

    public final static String GTASK_JSON_PARENT_ID = "parent_id";//父ID

    public final static String GTASK_JSON_PRIOR_SIBLING_ID = "prior_sibling_id";//上一个同级ID

    public final static String GTASK_JSON_RESULTS = "results";//结果

    public final static String GTASK_JSON_SOURCE_LIST = "source_list";//清单资源

    public final static String GTASK_JSON_TASKS = "tasks";//任务

    public final static String GTASK_JSON_TYPE = "type";//类型

    public final static String GTASK_JSON_TYPE_GROUP = "GROUP";//分组

    public final static String GTASK_JSON_TYPE_TASK = "TASK";//任务

    public final static String GTASK_JSON_USER = "user";//用户

    public final static String MIUI_FOLDER_PREFFIX = "[MIUI_Notes]";//Miui文件夹预修复

    public final static String FOLDER_DEFAULT = "Default";//文件夹默认值

    public final static String FOLDER_CALL_NOTE = "Call_Note";//文件夹呼叫说明

    public final static String FOLDER_META = "METADATA";//文件夹元

    public final static String META_HEAD_GTASK_ID = "meta_gid";//元头gtask id

    public final static String META_HEAD_NOTE = "meta_note";//元便签

    public final static String META_HEAD_DATA = "meta_data";//元数据

    public final static String META_NOTE_NAME = "[META INFO] DON'T UPDATE AND DELETE";
    //[元信息]不要更新和删除
}
