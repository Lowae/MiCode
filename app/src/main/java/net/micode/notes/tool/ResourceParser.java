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
import android.preference.PreferenceManager;

import net.micode.notes.R;
import net.micode.notes.ui.NotesPreferenceActivity;

public class ResourceParser {
//static修饰的属性强调它们只有一个，final修饰的属性表明是一个常数（创建后不能被修改）。
// static final修饰的属性表示一旦给值，就不可修改，并且可以通过类名访问。
    public static final int YELLOW           = 0;//定义静态常量YELLOW为0
    public static final int BLUE             = 1;//定义静态常量BLUE 为1
    public static final int WHITE            = 2;//定义静态常量WHITE 为2
    public static final int GREEN            = 3;//定义静态常量GREEN为3
    public static final int RED              = 4;//定义静态常量RED为4

    public static final int BG_DEFAULT_COLOR = YELLOW;//默认颜色为黄色

    public static final int TEXT_SMALL       = 0;//定义静态常量TEXT_SMALL 为0
    public static final int TEXT_MEDIUM      = 1;//定义静态常量TEXT_MEDIUM 为1
    public static final int TEXT_LARGE       = 2;//定义静态常量TEXT_LARGE为2
    public static final int TEXT_SUPER       = 3;//定义静态常量TEXT_SUPER为3

    public static final int BG_DEFAULT_FONT_SIZE = TEXT_MEDIUM;//默认字体大小为中号字体

    public static class NoteBgResources {//便签背景资源
        private final static int [] BG_EDIT_RESOURCES = new int [] {//背景编辑资源
            R.drawable.edit_yellow,//设置图片编辑黄色
            R.drawable.edit_blue,//设置图片编辑蓝色
            R.drawable.edit_white,//设置图片编辑白色
            R.drawable.edit_green,//设置图片编辑绿色
            R.drawable.edit_red//设置图片编辑红色
        };

        private final static int [] BG_EDIT_TITLE_RESOURCES = new int [] {//背景编辑标题资源
            R.drawable.edit_title_yellow,//设置图片编辑标题黄色
            R.drawable.edit_title_blue,//设置图片编辑标题蓝色
            R.drawable.edit_title_white,//设置图片编辑标题白色
            R.drawable.edit_title_green,//设置图片编辑标题绿色
            R.drawable.edit_title_red//设置图片编辑标题红色
        };

        public static int getNoteBgResource(int id) {
            return BG_EDIT_RESOURCES[id];
        }//获取便签背景资源

        public static int getNoteTitleBgResource(int id) {//获取便笺标题背景资源
            return BG_EDIT_TITLE_RESOURCES[id];
        }
    }

    public static int getDefaultBgId(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                NotesPreferenceActivity.PREFERENCE_SET_BG_COLOR_KEY, false))//首选项集背景颜色键
             {
            return (int) (Math.random() * NoteBgResources.BG_EDIT_RESOURCES.length);//返回背景编辑资源的长度
        } else {
            return BG_DEFAULT_COLOR;//返回默认背景颜色
        }
    }//获取默认背景ID

    public static class NoteItemBgResources {//备注项目背景资源
        private final static int [] BG_FIRST_RESOURCES = new int [] {
            R.drawable.list_yellow_up,//设置背景上层为黄色
            R.drawable.list_blue_up,//设置背景上层为蓝色
            R.drawable.list_white_up,//设置背景上层为白色
            R.drawable.list_green_up,//设置背景上层为绿色
            R.drawable.list_red_up//设置背景上层为红色
        };

        private final static int [] BG_NORMAL_RESOURCES = new int [] {//背景标准资源
            R.drawable.list_yellow_middle,//设置背景中层为黄色
            R.drawable.list_blue_middle,//设置背景中层为蓝色
            R.drawable.list_white_middle,//设置背景中层为白色
            R.drawable.list_green_middle,//设置背景中层为绿色
            R.drawable.list_red_middle//设置背景中层为红色
        };

        private final static int [] BG_LAST_RESOURCES = new int [] {//背景底部资源
            R.drawable.list_yellow_down,//设置背景底层为黄色
            R.drawable.list_blue_down,//设置背景底层为蓝色
            R.drawable.list_white_down,//设置背景底层为白色
            R.drawable.list_green_down,//设置背景底层为绿色
            R.drawable.list_red_down,//设置背景底层为红色
        };

        private final static int [] BG_SINGLE_RESOURCES = new int [] {//背景单一资源
            R.drawable.list_yellow_single,//设置背景单一为黄色
            R.drawable.list_blue_single,//设置背景单一为蓝色
            R.drawable.list_white_single,//设置背景单一为白色
            R.drawable.list_green_single,//设置背景单一为绿色
            R.drawable.list_red_single//设置背景单一为红色
        };

        public static int getNoteBgFirstRes(int id) {
            return BG_FIRST_RESOURCES[id];
        }//获取便签背景顶部资源

        public static int getNoteBgLastRes(int id) {
            return BG_LAST_RESOURCES[id];
        }//获取便签背景底部资源

        public static int getNoteBgSingleRes(int id) {
            return BG_SINGLE_RESOURCES[id];
        }//获取便签单一背景资源

        public static int getNoteBgNormalRes(int id) {
            return BG_NORMAL_RESOURCES[id];
        }//获取便签背景中部资源

        public static int getFolderBgRes() {
            return R.drawable.list_folder;
        }
    }//获取文件夹背景资源

    public static class WidgetBgResources {//小部件背景资源
        private final static int [] BG_2X_RESOURCES = new int [] {
            R.drawable.widget_2x_yellow,//设置小部件两倍黄
            R.drawable.widget_2x_blue,//设置小部件两倍蓝
            R.drawable.widget_2x_white,//设置小部件两倍白
            R.drawable.widget_2x_green,//设置小部件两倍绿
            R.drawable.widget_2x_red,//设置小部件两倍红
        };

        public static int getWidget2xBgResource(int id) {
            return BG_2X_RESOURCES[id];
        }
        //获取小部件两倍背景资源
        private final static int [] BG_4X_RESOURCES = new int [] {//背景四倍资源
            R.drawable.widget_4x_yellow,//设置小部件四倍黄
            R.drawable.widget_4x_blue,//设置小部件四倍蓝
            R.drawable.widget_4x_white,//设置小部件四倍白
            R.drawable.widget_4x_green,//设置小部件四倍绿
            R.drawable.widget_4x_red//设置小部件四倍红
        };

        public static int getWidget4xBgResource(int id) {
            return BG_4X_RESOURCES[id];
        }
    }//获取小部件四倍背景资源

    public static class TextAppearanceResources {//文本外观资源
        private final static int [] TEXTAPPEARANCE_RESOURCES = new int [] {
            R.style.TextAppearanceNormal,//设置文本外观为正常
            R.style.TextAppearanceMedium,//设置文本外观为中等
            R.style.TextAppearanceLarge,//设置文本外观为大号
            R.style.TextAppearanceSuper//设置文本外观为超大号
        };

        public static int getTexAppearanceResource(int id) {
            /**
             * HACKME: Fix bug of store the resource id in shared preference.
             * The id may larger than the length of resources, in this case,
             * return the {@link ResourceParser#BG_DEFAULT_FONT_SIZE}
             */
            //hackme：修复共享首选项中存储资源ID的错误。
            //*ID可能大于资源长度，在这种情况下，
            //*返回@link resourceparser bg_default_font_size_
            if (id >= TEXTAPPEARANCE_RESOURCES.length) {//如果ID>文本外观资源长度
                return BG_DEFAULT_FONT_SIZE;//返回背景默认字体大小
            }
            return TEXTAPPEARANCE_RESOURCES[id];//返回文本外观资源ID
        }

        public static int getResourcesSize() {
            return TEXTAPPEARANCE_RESOURCES.length;
        }//获取文本外观尺寸
    }
}
