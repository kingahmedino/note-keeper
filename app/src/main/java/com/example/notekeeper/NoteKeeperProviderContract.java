package com.example.notekeeper;

import android.net.Uri;
import android.provider.BaseColumns;

public final class NoteKeeperProviderContract {
    private NoteKeeperProviderContract(){}
    public static final String AUTHORITY = "com.example.notekeeper.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    protected interface CoursesIdColumns{
        public static final String COURSE_ID_COLUMN = "course_id";
    }

    protected interface CoursesColumns{
        public static final String COURSE_TITLE_COLUMN = "course_title";
    }

    protected interface NotesColumns{
        public static final String NOTE_TITLE_COLUMN = "note_title";
        public static final String NOTE_TEXT_COLUMN = "note_text";
    }

    public static final class Courses implements CoursesColumns, BaseColumns, CoursesIdColumns {
        public static final String PATH = "courses";
        // content://com.example.notekeeper/courses
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Notes implements NotesColumns, BaseColumns, CoursesIdColumns{
        public static final String PATH = "notes";
        // content://com.example.notekeeper/notes
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }
}
