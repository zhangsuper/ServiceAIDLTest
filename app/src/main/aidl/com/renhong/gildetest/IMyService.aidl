// IMyService.aidl
package com.renhong.gildetest;
import com.renhong.gildetest.Person;
// Declare any non-default types here with import statements

interface IMyService {
    List<Person> getPersonList();
    void addPerson(in Person person);
}
