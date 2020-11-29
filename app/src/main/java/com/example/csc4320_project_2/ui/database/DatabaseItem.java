package com.example.csc4320_project_2.ui.database;

public class DatabaseItem {

    private final int column_track_name_id = 1;
    public String get_item_name() {
        return item_name;
    }

    public void set_item_name(String item_name) {
        this.item_name = item_name;
    }

    private String item_name;

    public final String[] get_column_container() {
        return column_container;
    }

    private String column_container[];


    public final String get_file_path() {
        return file_path;
    }

    private String file_path;

    public DatabaseItem(){
        this.item_name = "[DUMMY_TRACK]";
        this.column_container = null;
        this.file_path = "[DUMMY_TRACK]";
    }


    public DatabaseItem(String container[]){
        this.column_container = container;
        this.item_name = container[column_track_name_id];
        this.file_path = container[container.length - 1];
    }

    public DatabaseItem(String container[], String item_name, String file_path){
        this.column_container = container;
        this.item_name = item_name;
        this.file_path = file_path;
    }
}
