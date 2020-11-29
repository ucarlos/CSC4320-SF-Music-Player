package com.example.csc4320_project_2.ui.browsefs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FileSystemViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FileSystemViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}