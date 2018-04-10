package com.example.river.download.rxdownload;

import java.io.File;

/**
 * Created by river on 2018/4/10.
 */

public class FileHelper {

    public static boolean isExists(DownloadRecord record) {
        String name = record.getFileName();
        File file = new File(Constant.DEFAULT_FILE_PATH, name);
        if (file.exists()) {
            return true;
        }
        return false;

    }

}
