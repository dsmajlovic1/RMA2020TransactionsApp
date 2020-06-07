package ba.unsa.etf.rma.rma2020_16570.Util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TransactionContentProvider extends ContentProvider {

    public static  final int ALL_ROWS = 1;
    public static  final int ONE_ROW = 2;
    private static  final UriMatcher uM;

    static {
        uM =new UriMatcher(UriMatcher.NO_MATCH);
        uM.addURI("rma.provider.transactions", "elements", ALL_ROWS);
        uM.addURI("rma.provider.transactions", "elements/#", ONE_ROW);
    }

    private TransactionDBOpenHelper transactionHelper;

    @Override
    public boolean onCreate() {
        transactionHelper = new TransactionDBOpenHelper(getContext(), TransactionDBOpenHelper.DATABASE_NAME, null, TransactionDBOpenHelper.DATABASE_VERSION);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database;
        try{
            database=transactionHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database=transactionHelper.getReadableDatabase();
        }
        String groupby=null;
        String having=null;
        SQLiteQueryBuilder squery = new SQLiteQueryBuilder();

        switch (uM.match(uri)){
            case ONE_ROW:
                String idRow = uri.getPathSegments().get(1);
                squery.appendWhere(TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID+"="+idRow);
            default:break;
        }
        squery.setTables(TransactionDBOpenHelper.TRANSACTION_TABLE);
        Cursor cursor = squery.query(database,projection,selection,selectionArgs,groupby,having,sortOrder);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uM.match(uri)){
            case ALL_ROWS:
                return "vnd.android.cursor.dir/vnd.rma.elemental";
            case ONE_ROW:
                return "vnd.android.cursor.item/vnd.rma.elemental";
            default:
                throw new IllegalArgumentException("Unsuported uri: "+uri.toString());
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database;
        try{
            database=transactionHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database=transactionHelper.getReadableDatabase();
        }
        long id = database.insert(TransactionDBOpenHelper.TRANSACTION_TABLE, null, values);
        return uri.buildUpon().appendPath(String.valueOf(id)).build();
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database;
        int count = 0;
        try{
            database=transactionHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database=transactionHelper.getReadableDatabase();
        }
        switch (uM.match(uri)){
            case ALL_ROWS:
                count = database.delete(TransactionDBOpenHelper.TRANSACTION_TABLE,selection, selectionArgs);
                break;
            case ONE_ROW:
                String segment = uri.getPathSegments().get(1);
                count = database.delete(TransactionDBOpenHelper.TRANSACTION_TABLE, "id = "+segment.trim(), null);
            default:break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        SQLiteDatabase database;
        try{
            database=transactionHelper.getWritableDatabase();
        }catch (SQLiteException e){
            database=transactionHelper.getReadableDatabase();
        }
        //String segment = uri.getPathSegments().get(0);
        count = database.update(TransactionDBOpenHelper.TRANSACTION_TABLE,values, selection, selectionArgs);
        return count;
    }

}
