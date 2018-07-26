package com.aspirecn.exam;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableField;

/**
 * Created by ding on 11/21/17.
 */

public class MyUser extends BaseObservable {

    private String title = "123";

    private String description;

    public ObservableField<String> className = new ObservableField<>();

    public MyUser() {

    }

    public MyUser(String title, String des, String className) {
        this.title = title;
        this.description = des;
        this.className.set(className);
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void reset() {

        AppLogger.i("reset");

        title = "";
        description = "";
        notifyPropertyChanged(BR._all);
    }
}
