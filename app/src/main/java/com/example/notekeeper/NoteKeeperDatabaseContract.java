package com.example.notekeeper;

import android.provider.BaseColumns;

public final class NoteKeeperDatabaseContract {
    private NoteKeeperDatabaseContract(){}

    public static final class CourseInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "course_info";
        public static final String COURSE_ID_COLUMN = "course_id";
        public static final String COURSE_TITLE_COLUMN = "course_title";
        public static final String INDEX1 = TABLE_NAME + "_index1";

        //CREATE INDEX course_info_index1 ON course_info (course_title);
        public static final String SQL_CREATE_INDEX1 = "CREATE INDEX " + INDEX1 + " ON " +
                TABLE_NAME + " (" + COURSE_TITLE_COLUMN + ")";

        public static final String getQName(String columName){
            return TABLE_NAME + "." + columName;
        }

        //CREATE TABLE course_info (course_id, course_title)
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COURSE_ID_COLUMN + " TEXT UNIQUE NOT NULL, " +
                        COURSE_TITLE_COLUMN + " TEXT NOT NULL)";
    }

    public static final class NoteInfoEntry implements BaseColumns{
        public static final String TABLE_NAME = "note_info";
        public static final String COURSE_ID_COLUMN = "course_id";
        public static final String NOTE_TITLE_COLUMN = "note_title";
        public static final String NOTE_TEXT_COLUMN = "note_text";
        public static final String INDEX1 = TABLE_NAME + "_index1";

        //CREATE INDEX note_info_index1 ON note_info (note_title);
        public static final String SQL_CREATE_INDEX1 = "CREATE INDEX " + INDEX1 + " ON " +
                TABLE_NAME + " (" + NOTE_TITLE_COLUMN + ")";

        public static final String getQName(String columName){
            return TABLE_NAME + "." + columName;
        }

        //CREATE TABLE note_info (course_id, note_title, note_text)
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COURSE_ID_COLUMN + " TEXT NOT NULL, " +
                        NOTE_TITLE_COLUMN + " TEXT NOT NULL, " +
                        NOTE_TEXT_COLUMN + " TEXT)";
    }
}
