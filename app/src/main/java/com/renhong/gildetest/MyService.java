package com.renhong.gildetest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.LinkedList;
import java.util.List;

public class MyService extends Service {

    private LinkedList<Person> personList = new LinkedList<Person>();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private final IMyService.Stub mBinder = new IMyService.Stub() {

        @Override
        public List<Person> getPersonList() throws RemoteException {
            return personList;
        }

        @Override
        public void addPerson(Person person) throws RemoteException {
            if(person!=null){
                personList.add(person);
            }
        }
    };
}
