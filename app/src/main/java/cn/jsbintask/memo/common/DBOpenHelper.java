package cn.jsbintask.memo.common;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import cn.jsbintask.memo.MemoApplication;


public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = DBOpenHelper.class.getSimpleName();
    private static final int VERSION = 1;
    private static final String DB_NAME = "ailv.db";

    public DBOpenHelper() {
        super(MemoApplication.getContext(), DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*第一次初始化app，创建表结构 */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ColumnContacts.EVENT_TABLE_NAME + "( "
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ColumnContacts.EVENT_TITLE_COLUMN + " text, "
                    + ColumnContacts.EVENT_CONTENT_COLUMN + " text, "
                    + ColumnContacts.EVENT_CREATED_TIME_COLUMN + " datetime, "
                    + ColumnContacts.EVENT_UPDATED_TIME_COLUMN + " datetime, "
                    + ColumnContacts.EVENT_REMIND_TIME_COLUMN + " datetime, "
                    + ColumnContacts.EVENT_IS_IMPORTANT_COLUMN + " INTEGER, "
                    + ColumnContacts.EVENT_IS_CLOCKED + " INTEGER"
        + ")");

        String sql = "INSERT INTO " + ColumnContacts.EVENT_TABLE_NAME + " VALUES(NULL, ?, ?, ?, ?, ?, ?, ?)";
        db.beginTransaction();
        db.execSQL(sql, new Object[]{"信息院入党积极分子",
                "（信息学院）3月21日13:00到榆中校区接至兰州八路军办事处至市区，下午送回榆中校区甘A40253（大巴）" +
                        "牛师傅13993153698，已安排好！请安排学生13:00在交流中心集合。",
                "2019-03-20 17:28:23",
                "2018-04-24 17:28",//上次编辑
                "2018-04-25 17:28",//提醒时间
                0, 0});
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /*版本更新时会执行该方法，如版本变更 => 2  */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Nothing to do
    }
}
