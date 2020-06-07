package ba.unsa.etf.rma.rma2020_16570.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class TransactionDBOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "RMADataBase.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TRANSACTION_TABLE ="transactions";
    public static final String TRANSACTION_ID ="id";
    public static final String TRANSACTION_INTERNAL_ID ="_id";
    public static final String TRANSACTION_DATE ="date";
    public static final String TRANSACTION_TITLE ="title";
    public static final String TRANSACTION_AMOUNT ="amount";
    public static final String TRANSACTION_ITEMDESCRIPTION ="itemDescription";
    public static final String TRANSACTION_TRANSACTIONINTERVAL ="transactionInterval";
    public static final String TRANSACTION_ENDDATE ="endDate";
    public static final String TRANSACTION_TYPE_ID ="type";

    public static final String TRANSACTION_TYPE_TABLE="types";
    public static final String TYPE_ID ="id";
    public static final String TRANSACTION_TYPE_TABLE_CREATE=
            "CREATE TABLE IF NOT EXISTS "+TRANSACTION_TYPE_TABLE+" ("+TYPE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)";
    public static final String TRANSACTION_TABLE_CREATE=
            "CREATE TABLE IF NOT EXISTS "+TRANSACTION_TABLE+" ("+TRANSACTION_INTERNAL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            TRANSACTION_ID+" INTEGER UNIQUE, "+
            TRANSACTION_DATE+" TEXT, "+
            TRANSACTION_TITLE+" TEXT NOT NULL, "+
            TRANSACTION_AMOUNT+" DOUBLE, "+
            TRANSACTION_ITEMDESCRIPTION+" TEXT, "+
            TRANSACTION_TRANSACTIONINTERVAL+" INTEGER, "+
            TRANSACTION_ENDDATE+" TEXT, "+
            TRANSACTION_TYPE_ID+" INTEGER NOT NULL, FOREIGN KEY("+TRANSACTION_TYPE_ID+") REFERENCES "+TRANSACTION_TYPE_TABLE+"("+TYPE_ID+"))";

    public static final String DELETE_TABLE ="deletes";
    public static final String DELETE_ID ="_id";
    public static final String DELETE_TRANSACTION_ID ="id";
    public static final String DELETE_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS "+DELETE_TABLE+" ("+DELETE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            DELETE_TRANSACTION_ID+" INTEGER UNIQUE )";

    private static final String TRANSACTION_DROP = "DROP TABLE IF EXISTS " + TRANSACTION_TABLE;
    private static final String TYPE_DROP = "DROP TABLE IF EXISTS " + TRANSACTION_TYPE_TABLE;
    private static final String DELETE_DROP = "DROP TABLE IF EXISTS " + DELETE_TABLE;

    public TransactionDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TRANSACTION_TYPE_TABLE_CREATE);
        db.execSQL(TRANSACTION_TABLE_CREATE);
        db.execSQL(DELETE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TRANSACTION_DROP);
        db.execSQL(TYPE_DROP);
        db.execSQL(DELETE_DROP);
        onCreate(db);
    }
}
