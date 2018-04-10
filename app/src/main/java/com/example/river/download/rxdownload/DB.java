package com.example.river.download.rxdownload;

/**
 * Created by river on 2018/3/31.
 */

public class DB {
    public static String FILE_ID = "file_id";
    public static String FILE_NAME = "file_name";
    public static String FILE_URL = "url";
    public static String FILE_SAVE_PATH = "file_save_pth";
    public static String FILE_TOTAL_SIZE = "file_total_size";
    public static String FILE_DOWNLOAD_SIZE = "file_download_size";
    public static String FILE_STATE = "file_state";
    public static String TABLE_NAME = "download_db";
    public static String CREATE = "CREATE TABLE" + TABLE_NAME + "(" + FILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT"
            + FILE_NAME + " VARCHAR,"
            + FILE_URL + " VARCHAR NOT NULL,"
            + FILE_SAVE_PATH + " VARCHAR,"
            + FILE_TOTAL_SIZE + " INTEGER,"
            + FILE_DOWNLOAD_SIZE + " INTEGER,"
            + FILE_STATE + " INTEGER," +
            ")";

}
