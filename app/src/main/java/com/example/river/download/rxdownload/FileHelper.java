package com.example.river.download.rxdownload;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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

    public static void openDirect(Context context, DownloadRecord record) {
        File file = new File(Constant.DEFAULT_FILE_PATH );
        //获取父目录
        File parentFlie = new File(file.getParent());
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(Uri.fromFile(parentFlie), "*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        context.startActivity(intent);
    }


}
