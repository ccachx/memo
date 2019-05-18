package cn.jsbintask.memo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import cn.jsbintask.memo.R;
import cn.jsbintask.memo.base.BaseActivity;
import cn.jsbintask.memo.manager.EventManager;
import cn.jsbintask.memo.manager.WeixinMessageManager;

public class WelcomeActivity extends BaseActivity {

    private final int SPLASH_DISPLAY_LENGHT = 2000; // 两秒后进入系统
    private EventManager mEventManager = EventManager.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initView() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        getSupportActionBar().hide();//隐藏标题栏

        WeixinMessageManager weixinMessageManager=new WeixinMessageManager();
        weixinMessageManager.initWeixinMessage();


        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                Intent mainIntent = new Intent(WelcomeActivity.this,
                        LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }

        }, SPLASH_DISPLAY_LENGHT);
    }

    @Override
    protected void initData() {

    }

    @Override
    public int getContentView() {
        return R.layout.activity_welcome;
    }
}