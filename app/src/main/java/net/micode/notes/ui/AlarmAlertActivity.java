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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import net.micode.notes.R;
import net.micode.notes.data.Notes;
import net.micode.notes.tool.DataUtils;

import java.io.IOException;

//实现闹钟提醒界面
public class AlarmAlertActivity extends Activity implements OnClickListener, OnDismissListener {
    private long mNoteId;
    private String mSnippet;
    //设置的最大显示文本长度
    private static final int SNIPPET_PREW_MAX_LEN = 60;
    MediaPlayer mPlayer;

    /**
     * 每个Activity的创建方法，在界面创建前调用
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //它的功能是启用窗体的扩展特性。参数是Window类中定义的常量。该处为设置标题为“无标题”
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        final Window win = getWindow();
        //设置此标识可以在锁屏唤醒屏幕时优先于密码输入界面展示该界面（闹钟界面）
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        //如果屏幕未亮起，将唤醒屏幕并亮起
        if (!isScreenOn()) {
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
        }

        Intent intent = getIntent();

        //获取便签id，并根据便签id获取便签的文本信息，若便签文本信息长度超过最大长度，则显示“。。。”省略后面的文本
        try {
            mNoteId = Long.valueOf(intent.getData().getPathSegments().get(1));
            mSnippet = DataUtils.getSnippetById(this.getContentResolver(), mNoteId);
            mSnippet = mSnippet.length() > SNIPPET_PREW_MAX_LEN ? mSnippet.substring(0,
                    SNIPPET_PREW_MAX_LEN) + getResources().getString(R.string.notelist_string_info)
                    : mSnippet;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }

        //调用媒体播放器播放闹钟铃声
        mPlayer = new MediaPlayer();
        if (DataUtils.visibleInNoteDatabase(getContentResolver(), mNoteId, Notes.TYPE_NOTE)) {
            showActionDialog();
            playAlarmSound();
        } else {
            finish();
        }
    }

    /**
     *  判断屏幕是否亮起
     */
    private boolean isScreenOn() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    /**
     * 播放闹钟铃声
     */
    private void playAlarmSound() {
        //获取当前的默认铃声的Uri
        Uri url = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
        //当未勿扰模式（静音模式）时，设置为不播放音频
        int silentModeStreams = Settings.System.getInt(getContentResolver(),
                Settings.System.MODE_RINGER_STREAMS_AFFECTED, 0);

        if ((silentModeStreams & (1 << AudioManager.STREAM_ALARM)) != 0) {
            //不播放音频
            mPlayer.setAudioStreamType(silentModeStreams);
        } else {
            //播放音频
            mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        }
        try {
            //设置音频的资源路径
            mPlayer.setDataSource(this, url);
            mPlayer.prepare();
            //设置循环播放
            mPlayer.setLooping(true);
            mPlayer.start();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 显示便签内容的对话框
     */
    private void showActionDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        //设置标题
        dialog.setTitle(R.string.app_name);
        //设置内容
        dialog.setMessage(mSnippet);
        //设置确定及取消按钮
        dialog.setPositiveButton(R.string.notealert_ok, this);
        if (isScreenOn()) {
            dialog.setNegativeButton(R.string.notealert_enter, this);
        }
        dialog.show().setOnDismissListener(this);
    }

    /**
     * 当点击事件发生时的回调
     * @param dialog
     * @param which
     */
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                Intent intent = new Intent(this, NoteEditActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra(Intent.EXTRA_UID, mNoteId);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 销毁对话框，并停止播放音频
     * @param dialog
     */
    public void onDismiss(DialogInterface dialog) {
        stopAlarmSound();
        finish();
    }

    /**
     * 实现停止播放音频功能
     */
    private void stopAlarmSound() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }
}
