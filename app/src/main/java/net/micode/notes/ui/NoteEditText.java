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

package net.micode.notes.ui;

import android.content.Context;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.widget.EditText;

import net.micode.notes.R;

import java.util.HashMap;
import java.util.Map;

public class NoteEditText extends android.support.v7.widget.AppCompatEditText {
    private static final String TAG = "NoteEditText";
    private int mIndex;
    private int mSelectionStartBeforeDelete;
    //此处定义的静态常量用于URI中
    private static final String SCHEME_TEL = "tel:" ;
    private static final String SCHEME_HTTP = "http:" ;
    private static final String SCHEME_EMAIL = "mailto:" ;
    //此处定义的静态常量用于打开URI链接时的提示信息
    private static final Map<String, Integer> sSchemaActionResMap = new HashMap<String, Integer>();
    static {
        //呼叫电话
        sSchemaActionResMap.put(SCHEME_TEL, R.string.note_link_tel);
        //浏览网页
        sSchemaActionResMap.put(SCHEME_HTTP, R.string.note_link_web);
        //发送邮件
        sSchemaActionResMap.put(SCHEME_EMAIL, R.string.note_link_email);
    }

    /**
     * Call by the {@link NoteEditActivity} to delete or add edit text
     */
    /**
     * 用于监听文本视图变化的监听器，通过被NoteEditActivity调用以删除或添加编辑文本，对应于清单模式的文本编辑事件
     */
    public interface OnTextViewChangeListener {
        /**
         * Delete current edit text when {@link KeyEvent#KEYCODE_DEL} happens
         * and the text is null
         */
        /**
         * 当被调用触发事件为KEYCODE_DEL，且被删除的当前文本为空时，则删除当前编辑文本的视图
         * @param index
         * @param text
         */
        void onEditTextDelete(int index, String text);

        /**
         * Add edit text after current edit text when {@link KeyEvent#KEYCODE_ENTER}
         * happen
         */
        /**
         * 当触发事件为KEYCODE_ENTER，换行并添加当前编辑文本后的文本
         * @param index
         * @param text
         */
        void onEditTextEnter(int index, String text);

        /**
         * Hide or show item option when text change
         */
        /**
         * 文本更改时隐藏或显示项目选项
         * @param index
         * @param hasText
         */
        void onTextChange(int index, boolean hasText);
    }

    private OnTextViewChangeListener mOnTextViewChangeListener;

    public NoteEditText(Context context) {
        super(context, null);
        mIndex = 0;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public void setOnTextViewChangeListener(OnTextViewChangeListener listener) {
        mOnTextViewChangeListener = listener;
    }

    public NoteEditText(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.editTextStyle);
    }

    public NoteEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    /**
     * 触摸事件监听器，当检测到触摸事件时，响应对应事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                int x = (int) event.getX();
                int y = (int) event.getY();
                x -= getTotalPaddingLeft();
                y -= getTotalPaddingTop();
                x += getScrollX();
                y += getScrollY();

                Layout layout = getLayout();
                //获取与指定垂直位置对应的行号。如果y<0则返回0
                int line = layout.getLineForVertical(y);
                //获取位置最接近指定水平位置的指定行上的字符偏移量。
                int off = layout.getOffsetForHorizontal(line, x);
                //移动cursor到off处
                Selection.setSelection(getText(), off);
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 当按键被按下时调用
     * @param keyCode 表示按下按钮的键码
     * @param event KeyEvent对象，用于定义按钮操作
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            //按下回车键
            case KeyEvent.KEYCODE_ENTER:
                if (mOnTextViewChangeListener != null) {
                    return false;
                }
                break;
            //按下删除键
            case KeyEvent.KEYCODE_DEL:
                //此方法用于返回选择锚点（selection anchor）或光标（cursor）的偏移量，如果没有选择或光标，则返回-1。
                mSelectionStartBeforeDelete = getSelectionStart();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 当按键被松开时调用
     * @param keyCode 表示所按下按钮的键码
     * @param event KeyEvent对象，用于定义按钮操作
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch(keyCode) {
            //松开删除键（退格键）
            case KeyEvent.KEYCODE_DEL:
                //此处功能用于实现当按下删除键后，如果当前光标位置是起始位置而且该视图不是第一个视图，按下删除键后则删除该行对应的视图。
                //视图为清单模式中，每串文本对应一个EditText视图,当按下回车键后，将会新增加一个EditText,
                //整个便签由EditText列表构成，清单模式下每个复选框对应一个ExitText视图
                if (mOnTextViewChangeListener != null) {
                    if (0 == mSelectionStartBeforeDelete && mIndex != 0) {
                        mOnTextViewChangeListener.onEditTextDelete(mIndex, getText().toString());
                        return true;
                    }
                } else {
                    Log.d(TAG, "OnTextViewChangeListener was not seted");
                }
                break;
            //松开回车键
            case KeyEvent.KEYCODE_ENTER:
                //此处功能用于实现当按下回车键后，将会新加一个ExitText视图，光标后的文本内容将会被添加到新建的ExitText中，
                //光标前的文本保持在当前的ExitText中
                if (mOnTextViewChangeListener != null) {
                    //获取当前光标的位置
                    int selectionStart = getSelectionStart();
                    //截取从光标当前位置后开始到ExitText末尾的整个Text内容
                    String text = getText().subSequence(selectionStart, length()).toString();
                    //当前ExitText中的内容为0开始到光标处
                    setText(getText().subSequence(0, selectionStart));
                    //新建ExitText,并添加text(光标当前位置后开始到ExitText末尾的整个Text内容)
                    mOnTextViewChangeListener.onEditTextEnter(mIndex + 1, text);
                } else {
                    Log.d(TAG, "OnTextViewChangeListener was not seted");
                }
                break;
            default:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (mOnTextViewChangeListener != null) {
            //如果当前ExitText无焦点且文本内容为空
            if (!focused && TextUtils.isEmpty(getText())) {
                mOnTextViewChangeListener.onTextChange(mIndex, false);
            } else {
                //如果当前EditText有焦点且文本不为空
                mOnTextViewChangeListener.onTextChange(mIndex, true);
            }
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    /**
     * 在显示上下文菜单时调用，上下文菜单在该便签中指的是含有超链接的URL，用户长按该链接后，弹出对话框打开该链接指向URL(网址，电话，邮件)
     * @param menu 正在构建的上下文菜单
     */
    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        if (getText() instanceof Spanned) {
            int selStart = getSelectionStart();
            int selEnd = getSelectionEnd();

            int min = Math.min(selStart, selEnd);
            int max = Math.max(selStart, selEnd);
            //获取整串URL
            final URLSpan[] urls = ((Spanned) getText()).getSpans(min, max, URLSpan.class);
            if (urls.length == 1) {
                int defaultResId = 0;
                //如果URL属于定义的3种之中的一种，则获取其ID
                for(String schema: sSchemaActionResMap.keySet()) {
                    if(urls[0].getURL().indexOf(schema) >= 0) {
                        defaultResId = sSchemaActionResMap.get(schema);
                        break;
                    }
                }
                //不属于则默认ID为0
                if (defaultResId == 0) {
                    defaultResId = R.string.note_link_other;
                }
                //添加菜单的点击事件监听器，实现界面的跳转
                menu.add(0, 0, 0, defaultResId).setOnMenuItemClickListener(
                        new OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                // goto a new intent
                                urls[0].onClick(NoteEditText.this);
                                return true;
                            }
                        });
            }
        }
        super.onCreateContextMenu(menu);
    }
}
