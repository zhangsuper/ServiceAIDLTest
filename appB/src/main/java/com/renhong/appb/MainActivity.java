package com.renhong.appb;

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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.renhong.gildetest.IMyService;
import com.renhong.gildetest.Person;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button addPerson;
    private Button queryPerson;
    private TextView personInfo;
    private IMyService mIMyService;
    private boolean mIsRemoteBound;
    private int index =0;

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

        addPerson = (Button) findViewById(R.id.add_person);
        queryPerson = (Button) findViewById(R.id.query_person);
        personInfo = (TextView) findViewById(R.id.person_info);
        addPerson.setOnClickListener(this);
        queryPerson.setOnClickListener(this);

        if (mIsRemoteBound) {
            unbindService(conn);
        }else{
            Intent serviceIntent = createExplicitFromImplicitIntent(this,new Intent("com.aidl.IMyService"));
            if (serviceIntent==null){
                Toast.makeText(this,"没有找到对应的service",Toast.LENGTH_SHORT).show();
                return;
            }
            bindService(serviceIntent,
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_person:
                Person person = new Person();
                index = index + 1;
                person.setName("Person" + index);
                person.setAge(20);
                person.setTelNumber("123456");
                try {
                    if(mIMyService!=null)
                    mIMyService.addPerson(person);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.query_person:
                try {
                    if(mIMyService==null){
                        return;
                    }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
