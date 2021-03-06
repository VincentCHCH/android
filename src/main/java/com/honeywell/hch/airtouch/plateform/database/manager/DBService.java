package com.honeywell.hch.airtouch.plateform.database.manager;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.honeywell.hch.airtouch.library.LibApplication;
import com.honeywell.hch.airtouch.library.util.LogUtil;
import com.honeywell.hch.airtouch.plateform.R;
import com.honeywell.hch.airtouch.plateform.appmanager.AppManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by nan.liu on 2/2/15.
 */
public class DBService {
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"
            + LibApplication.getContext().getPackageName() + "/databases";


    public static final String DB_NAME = "hplus.db";
    private static int BUFFER_SIZE = 512;


    public DBService(Context context) {
    }

    public SQLiteDatabase getSqliteDatabase() {
        SQLiteDatabase sqLiteDatabase = openDatabase(DB_PATH + "/" + DB_NAME);
        return sqLiteDatabase;
    }


    public void insertOrUpdate(String tableName, String[] volumn,
                               List<HashMap<String, Object>> value) {
        StringBuilder volumnBuffer = new StringBuilder();
        volumnBuffer.append("replace into " + tableName + " (");
        for (int i = 0; i < volumn.length; i++) {
            volumnBuffer.append(volumn[i] + ",");
        }

        volumnBuffer.deleteCharAt(volumnBuffer.length() - 1);
        volumnBuffer.append(") values(");
        for (int i = 0; i < volumn.length; i++) {
            volumnBuffer.append("?,");
        }

        volumnBuffer.deleteCharAt(volumnBuffer.length() - 1);
        volumnBuffer.append(");");

        SQLiteDatabase sqLiteDatabase = getSqliteDatabase();

        if (sqLiteDatabase != null) {
            for (int i = 0; i < value.size(); i++) {
                String[] valueString = new String[volumn.length];
                for (int j = 0; j < volumn.length; j++) {
                    valueString[j] = value.get(i).get(volumn[j]) == null ? ""
                            : value.get(i).get(volumn[j]).toString();
                }
                sqLiteDatabase.execSQL(volumnBuffer.toString(), valueString);
            }
            sqLiteDatabase.close();
        }

    }

    public void insertOrUpdate(String tableName, String[] volumn,
                               Object[] value) {
        StringBuilder volumnBuffer = new StringBuilder();
        volumnBuffer.append("replace into " + tableName + " (");
        for (int i = 0; i < volumn.length; i++) {
            volumnBuffer.append(volumn[i] + ",");
        }

        volumnBuffer.deleteCharAt(volumnBuffer.length() - 1);
        volumnBuffer.append(") values(");
        for (int i = 0; i < volumn.length; i++) {
            volumnBuffer.append("?,");
        }

        volumnBuffer.deleteCharAt(volumnBuffer.length() - 1);
        volumnBuffer.append(");");

        SQLiteDatabase sqLiteDatabase = getSqliteDatabase();
        if (sqLiteDatabase != null) {
            sqLiteDatabase.execSQL(volumnBuffer.toString(), value);
            sqLiteDatabase.close();
        }

    }

    public void insert(String tableName, String[] volumn, Object[] value) {
        SQLiteDatabase sqLiteDatabase = getSqliteDatabase();

        if (sqLiteDatabase != null) {
            StringBuilder volumnBuffer = new StringBuilder();
            volumnBuffer.append("insert into " + tableName + " (");
            for (int i = 0; i < volumn.length; i++) {
                volumnBuffer.append(volumn[i] + ",");
            }
            volumnBuffer.deleteCharAt(volumnBuffer.length() - 1);
            volumnBuffer.append(") values(");
            for (int i = 0; i < volumn.length; i++) {
                volumnBuffer.append("?,");
            }
            volumnBuffer.deleteCharAt(volumnBuffer.length() - 1);
            volumnBuffer.append(");");
            sqLiteDatabase.execSQL(volumnBuffer.toString(), value);
            sqLiteDatabase.close();
        }

    }

    public void delete(String tableName) {
        SQLiteDatabase sqLiteDatabase = getSqliteDatabase();
        if (sqLiteDatabase != null) {
            StringBuilder deleteSQL = new StringBuilder();
            deleteSQL.append("delete from " + tableName + ";");
            sqLiteDatabase.execSQL(deleteSQL.toString(), new String[]{});
            sqLiteDatabase.close();
        }
    }

    public ArrayList<HashMap<String, String>> findAll(String tableName,
                                                      String[] volumn) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        SQLiteDatabase sqLiteDatabase = getSqliteDatabase();
        if (sqLiteDatabase != null) {
            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + tableName,
                    null);
            while (cursor.moveToNext()) {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                for (int i = 0; i < volumn.length; i++) {
                    hashMap.put(volumn[i], cursor.getString(i));
                }
                list.add(hashMap);
            }
            cursor.close();
            sqLiteDatabase.close();
        }


        return list;
    }


//    public SQLiteDatabase openDatabase(String path) {
//        return SQLiteDatabase.openOrCreateDatabase(path, null);
//    }

    public SQLiteDatabase openDatabase(String path) {
        FileOutputStream fos = null;
        try {
            if (!(new File(path).exists())) {
                InputStream is = AppManager.getInstance().getApplication().getApplicationContext().getResources().openRawResource(R.raw.hplus);//导入数据库
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count;

                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();

            }
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(path, null);
            return db;

        } catch (Resources.NotFoundException e) {
            // TODO Auto-generated catch block
            LogUtil.error("DBService", "Resources.NotFoundException", e);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            LogUtil.error("DBService", "FileNotFoundException", e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LogUtil.error("DBService", "IOException", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LogUtil.error("DBService", "IOException", e);
                }
            }
        }
        return null;
    }

}
