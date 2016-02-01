package com.example.larry.myapplication.utils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

public class FileUtils {
    private String SDPath;

    public FileUtils() {
        //得到当前外部存储设备的目录
        SDPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 在SD卡上创建文件
     *
     * @param fileName
     * @return
     */
    public File createSDFile(String fileName) {
        File file = new File(SDPath + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     * @return
     */
    public File createSDDir(String dirName) {
        File file = new File(SDPath + dirName);
        if (!file.exists())
            file.mkdir();
        return file;
    }

    /**
     * 判断SD卡上文件是否存在
     *
     * @param fileName
     * @return
     */
    public boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 将一个inputStream里面的数据写到SD卡中
     *
     * @param path
     * @param fileName
     * @param inputStream
     * @return
     */
    public File writeToSDfromInput(String path, String fileName, InputStream inputStream) {
        //createSDDir(path);
        File file = createSDFile(path + fileName);
        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024];
            while (inputStream.read(buffer) != -1) {
                outStream.write(buffer);
            }
            outStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private void deleteFile(File file) {
        if (file.isFile()) {
            deleteFileSafely(file);
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                deleteFileSafely(file);
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteFile(childFiles[i]);
            }
            deleteFileSafely(file);
        }
    }

    /**
     * 安全删除文件.
     * @param file
     * @return
     */
    public static boolean deleteFileSafely(File file) {
        if (file != null) {
            String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
            File tmp = new File(tmpPath);
            file.renameTo(tmp);
            return tmp.delete();
        }
        return false;
    }
}