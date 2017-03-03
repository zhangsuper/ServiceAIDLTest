package com.renhong.gildetest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.opendanmaku.DanmakuItem;
import com.opendanmaku.DanmakuView;
import com.opendanmaku.IDanmakuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mGildeImg;
    private DanmakuView mDanmakuView;
    private String mBlueLight = "assets://apng/answer_light_blue.png";
    private String mRedLight = "assets://apng/answer_light_red.png";
    private String mFlower= "assets://apng/live_anim_flower.png";


    private Button switcherBtn;
    private Button sendBtn;
    private EditText textEditText;
    private Button addPerson;
    private Button queryPerson;
    private TextView personInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDanmakuView = (DanmakuView) findViewById(R.id.danmakuView);
        switcherBtn = (Button) findViewById(R.id.switcher);
        sendBtn = (Button) findViewById(R.id.send);
        textEditText = (EditText) findViewById(R.id.text);

        List<IDanmakuItem> list = initItems();
        Collections.shuffle(list);
        mDanmakuView.addItem(list, true);

        switcherBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

//        mGildeImg = (ImageView) findViewById(R.id.iv_gilde_img);
//        Glide.with(this).load("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg").into(mGildeImg);


    }


    private List<IDanmakuItem> initItems() {
        List<IDanmakuItem> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            IDanmakuItem item = new DanmakuItem(this, i + " : plain text danmuku", mDanmakuView.getWidth());
            list.add(item);
        }

        String msg = " : text with image   ";
        for (int i = 0; i < 100; i++) {
            ImageSpan imageSpan = new ImageSpan(this, R.mipmap.em);
            SpannableString spannableString = new SpannableString(i + msg);
            spannableString.setSpan(imageSpan, spannableString.length() - 2, spannableString.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            IDanmakuItem item = new DanmakuItem(this, spannableString, mDanmakuView.getWidth(), 0, 0, 0, 1.5f);
            list.add(item);
        }
        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDanmakuView.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDanmakuView.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDanmakuView.clear();
    }

    @Override
    public void onClick(View v) {
        int index = 0;
        switch (v.getId()) {
            case R.id.switcher:
                if (mDanmakuView.isPaused()) {
                    switcherBtn.setText(R.string.hide);
                    mDanmakuView.show();
                } else {
                    switcherBtn.setText(R.string.show);
                    mDanmakuView.hide();
                }
                break;
            case R.id.send:
                String input = textEditText.getText().toString();
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(MainActivity.this, R.string.empty_prompt, Toast.LENGTH_SHORT).show();
                } else {
                    IDanmakuItem item = new DanmakuItem(this, new SpannableString(input), mDanmakuView.getWidth(),0,R.color.my_item_color,0,1);
//                    IDanmakuItem item = new DanmakuItem(this, input, mDanmakuView.getWidth());
//                    item.setTextColor(getResources().getColor(R.color.my_item_color));
//                    item.setTextSize(14);
//                    item.setTextColor(textColor);
                    mDanmakuView.addItemToHead(item);
                }
                textEditText.setText("");
                break;
        }
    }
}
