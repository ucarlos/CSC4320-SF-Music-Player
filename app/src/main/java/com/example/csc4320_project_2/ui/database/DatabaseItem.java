package com.example.csc4320_project_2.ui.database;

/**
 *
 * Class that represents an item in the database.
 *
 */
public class DatabaseItem {

    private final int columnTrackNameId = 1;
    public String get_item_name() {
        return item_name;
    }

    public void set_item_name(String item_name) {
        this.item_name = item_name;
    }

    private String item_name = "[DUMMY_TRACK]";

    public final String[] get_column_container() {
        return column_container;
    }

    private String column_container[] = new String[0];


    public final String get_file_path() {
        return file_path;
    }

    private String file_path = "[DUMMY_TRACK]";

    public DatabaseItem(){ }


    public DatabaseItem(String container[]){
        this.column_container = container;
        this.item_name = container[columnTrackNameId];
        this.file_path = container[container.length - 1];
    }

    public DatabaseItem(String container[], String item_name, String file_path){
        this.column_container = container;
        this.item_name = item_name;
        this.file_path = file_path;
    }
}
