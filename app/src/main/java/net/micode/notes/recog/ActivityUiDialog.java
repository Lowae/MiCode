package net.micode.notes.recog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.baidu.aip.asrwakeup3.core.recog.listener.ChainRecogListener;
import com.baidu.aip.asrwakeup3.core.recog.listener.MessageStatusRecogListener;

import net.micode.notes.R;


/**
 * UI 界面调用
 * <p>
 * 本类仅仅初始化及释放MyRecognizer，具体识别逻辑在BaiduASRDialog。对话框UI在BaiduASRDigitalDialog
 * 依赖SimpleTransApplication 在两个activity中传递输入参数
 * <p>
 * Created by fujiayi on 2017/10/17.
 */

public class ActivityUiDialog extends ActivityAbstractRecog {

    /**
     * 对话框界面的输入参数
     */
    private ChainRecogListener chainRecogListener;

    public ActivityUiDialog() {
        super(R.raw.uidialog_recog, false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 有2个listner，一个是用户自己的业务逻辑，如MessageStatusRecogListener。另一个是UI对话框的。
         * 使用这个ChainRecogListener把两个listener和并在一起
         */
        chainRecogListener = new ChainRecogListener();
        // DigitalDialogInput 输入 ，MessageStatusRecogListener可替换为用户自己业务逻辑的listener
        chainRecogListener.addListener(new MessageStatusRecogListener(handler));
        myRecognizer.setEventListener(chainRecogListener); // 替换掉原来的listener


    }

}