/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zeowls.noteowl.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.zeowls.noteowl.provider.Contract;
import com.zeowls.noteowl.provider.DBHelper;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

//    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(DBHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the list
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the list table has the correct columns, since we
        give you the code for the Task table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(Contract.TaskEntry.TABLE_NAME);
        tableNameHashSet.add(Contract.ListEntry.TABLE_NAME);

        mContext.deleteDatabase(DBHelper.DATABASE_NAME);
        SQLiteDatabase db = new DBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the list entry
        // and Task entry tables
        assertTrue("Error: Your database was created without both the list entry and Task entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + Contract.ListEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> listColumnHashSet = new HashSet<>();
        listColumnHashSet.add(Contract.ListEntry._ID);
        listColumnHashSet.add(Contract.ListEntry.COLUMN_NAME);
        listColumnHashSet.add(Contract.ListEntry.COLUMN_TASKS_COUNT);
        listColumnHashSet.add(Contract.ListEntry.COLUMN_IS_USER_DEFINED);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            listColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required list
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required list entry columns",
                listColumnHashSet.isEmpty());
        db.close();
        c.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        list database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPolelistValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testlistTable() {
        // First step: Get reference to writable database
        SQLiteDatabase db = new DBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        // Create ContentValues of what you want to insert
        // (you can use the createNorthPolelistValues if you wish)
        ContentValues valuesData = TestUtilities.createListValues();
        // Insert ContentValues into database and get a row ID back
        long rowId = db.insert(Contract.ListEntry.TABLE_NAME, null, valuesData);
        assertTrue("Error in inserting data", rowId != -1);
        // Query the database and receive a Cursor back
        Cursor c = db.query(Contract.ListEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        // Move the cursor to a valid database row
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Not The same Data", c, valuesData);
        assertFalse("Error: This means that we were unable to query the database for table information.",
                c.moveToNext());
        // Finally, close the cursor and database
        c.close();
        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createTaskValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testTaskTable() {
        // First insert the list, and then use the listRowId to insert
        // the Task. Make sure to cover as many failure cases as you can.
        long listRowId = insertlist();
        // Instead of rewriting all of the code we've already written in testlistTable
        // we can move this code to insertlist and then call insertlist from both
        // tests. Why move it? We need the code to return the ID of the inserted list
        // and our testListTable can only return void because it's a test.

        // First step: Get reference to writable database
        SQLiteDatabase db = new DBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        // Create ContentValues of what you want to insert
        // (you can use the createTaskValues TestUtilities function if you wish)
        ContentValues valuesData = TestUtilities.createTaskValues(listRowId);
        // Insert ContentValues into database and get a row ID back
        db.insert(Contract.TaskEntry.TABLE_NAME, null, valuesData);
        // Query the database and receive a Cursor back
        Cursor c = db.query(Contract.TaskEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        // Move the cursor to a valid database row
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Not The same Data", c, valuesData);
        assertFalse("Error: This means that we were unable to query the database for table information.",
                c.moveToNext());
        // Finally, close the cursor and database
        c.close();
        db.close();
    }


    /*
        Students: This is a helper method for the testTaskTable quiz. You can move your
        code from testListTable to here so that you can call this code from both
        testTaskTable and testListTable.
     */
    public long insertlist() {
        // First step: Get reference to writable database
        SQLiteDatabase db = new DBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        // Create ContentValues of what you want to insert
        // (you can use the createNorthPolelistValues if you wish)
        ContentValues valuesData = TestUtilities.createListValues();
        // Insert ContentValues into database and get a row ID back
        long rowId = db.insert(Contract.ListEntry.TABLE_NAME, null, valuesData);
        assertTrue("Error in inserting data", rowId != -1);
        // Query the database and receive a Cursor back
        Cursor c = db.query(Contract.ListEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        // Move the cursor to a valid database row
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Not The same Data", c, valuesData);

        return rowId;
    }
}
