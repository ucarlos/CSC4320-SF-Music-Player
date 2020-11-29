package com.example.csc4320_project_2.ui.database;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;


/**
 * FileParcel Class - Used to send strings representing absolute file paths to and from
 * Different fragments.
 */
public class FileParcel implements Parcelable {

    protected FileParcel(Parcel in) {
        file_path = in.readString();
    }

    public static final Creator<FileParcel> CREATOR = new Creator<FileParcel>() {
        @Override
        public FileParcel createFromParcel(Parcel in) {
            return new FileParcel(in);
        }

        @Override
        public FileParcel[] newArray(int size) {
            return new FileParcel[size];
        }
    };

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    private String file_path;

    public FileParcel(String file){
        this.file_path = file;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NotNull Parcel dest, int i) {
        dest.writeString(getFile_path());
    }

    private void readFromParcel(@NotNull Parcel in){
        this.file_path = in.readString();
    }
}
