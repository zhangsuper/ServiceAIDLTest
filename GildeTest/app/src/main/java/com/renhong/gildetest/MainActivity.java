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

    private IMyService mIMyService;
    private boolean mIsRemoteBound;

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIMyService = IMyService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mIMyService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDanmakuView = (DanmakuView) findViewById(R.id.danmakuView);
        switcherBtn = (Button) findViewById(R.id.switcher);
        sendBtn = (Button) findViewById(R.id.send);
        textEditText = (EditText) findViewById(R.id.text);
        addPerson = (Button) findViewById(R.id.add_person);
        queryPerson = (Button) findViewById(R.id.query_person);
        personInfo = (TextView) findViewById(R.id.person_info);
        addPerson.setOnClickListener(this);
        queryPerson.setOnClickListener(this);

        List<IDanmakuItem> list = initItems();
        Collections.shuffle(list);
        mDanmakuView.addItem(list, true);

        switcherBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

//        mGildeImg = (ImageView) findViewById(R.id.iv_gilde_img);
//        Glide.with(this).load("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg").into(mGildeImg);

        if (mIsRemoteBound) {
            unbindService(conn);
        }else{
            bindService(createExplicitFromImplicitIntent(this,new Intent("com.aidl.IMyService")),
                    conn, Context.BIND_AUTO_CREATE);
//            bindService(new Intent("com.aidl.IMyService"),
//                    conn, Context.BIND_AUTO_CREATE);
        }
        mIsRemoteBound = !mIsRemoteBound;

    }

    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
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
        unbindService(conn);
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
            case R.id.add_person:
                Person person = new Person();
                index = index + 1;
                person.setName("Person" + index);
                person.setAge(20);
                person.setTelNumber("123456");
                try {
                    mIMyService.addPerson(person);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.query_person:
                try {
                    List<Person> personList = mIMyService.getPersonList();
                    StringBuffer mString = new StringBuffer();
                    for (int i=0;i<personList.size();i++){
                        mString.append(personList.get(i).getName()+","+personList.get(i).getAge()+","+personList.get(i).getTelNumber()+";");
                    }
                    personInfo.setText(mString.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
