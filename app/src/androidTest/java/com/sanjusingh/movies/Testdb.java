package com.sanjusingh.movies;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.sanjusingh.movies.db.MovieDbHelper;
import com.sanjusingh.movies.db.MoviesContract;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by sanju singh on 1/11/2016.
 */
public class Testdb extends AndroidTestCase{



    public void testDb(){

        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MoviesContract.MovieEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: Your database was created without Movies entry tables",
                tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.MovieEntry.TABLE_NAME + ")", null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(MoviesContract.MovieEntry._ID);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_TITLE);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_OVERVIEW);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_POSTER_PATH);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                locationColumnHashSet.isEmpty());
        c.close();
        db.close();

    }

    public void testMovieTable(){
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MovieEntry._ID, 22);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, "Spectre");
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "2015");
        contentValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, 9.4);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "this is awesome movie and i really like it");
        contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "http://www.myposter.com");
        contentValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, "http://www.image.com");


        long movieRowId;
        movieRowId = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, contentValues);

        assertTrue(movieRowId != -1);

        Cursor cursor = db.query(MoviesContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertTrue( "Error: No Records returned from movie query", cursor.moveToFirst() );

        Set<Map.Entry<String, Object>> valueSet = contentValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = cursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + "Error: Movie Query Validation Failed", idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + "Error: Movie Query Validation Failed", expectedValue, cursor.getString(idx));
        }

        assertFalse( "Error: More than one record returned from Movie query",
                cursor.moveToNext() );

        cursor.close();
        db.close();
    }
}
