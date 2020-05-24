package com.example.notekeeper;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.example.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;
import com.example.notekeeper.NoteKeeperProviderContract.Courses;
import com.example.notekeeper.NoteKeeperProviderContract.Notes;

public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String NOTE_ID = "com.example.notekeeper.NOTE_POSITION";
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.example.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.notekeeper.ORIGINAL_NOTE_TEXT";
    public static final int ID_NOT_SET = -1;
    public static final int LOADER_NOTES = 0;
    public static final int LOADER_COURSES = 1;
    private NoteInfo mNote = new NoteInfo(DataManager.getInstance().getCourses().get(0), "","");
    private boolean isNewNote;
    private Spinner spinnerCourses;
    private EditText textNoteTitle;
    private EditText textNoteText;
    private boolean mIsCancelling;
    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;
    private int mNoteId;
    private NoteKeeperOpenHelper openHelper;
    private int courseIdPos;
    private int noteTitlePos;
    private int noteTextPos;
    private Cursor noteCursor;
    private SimpleCursorAdapter adapterCourses;
    private boolean courseQueryFinished;
    private boolean noteQueryFinished;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        spinnerCourses = findViewById(R.id.spinner_courses);

        openHelper = new NoteKeeperOpenHelper(this);

        adapterCourses = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
                new String[] {CourseInfoEntry.COURSE_TITLE_COLUMN},
                new int[] {android.R.id.text1}, 0);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapterCourses);

        getSupportLoaderManager().initLoader(LOADER_COURSES, null, this);
        readDisplayStateValues();

        if(savedInstanceState == null) {
            saveOriginalNoteValues();
        } else{
            restoreOriginalNoteValues(savedInstanceState);
        }

        textNoteTitle = findViewById(R.id.text_note_title);
        textNoteText = findViewById(R.id.text_note_text);

        if(!isNewNote)
            getSupportLoaderManager().initLoader(LOADER_NOTES, null, this);
    }

    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        mOriginalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    @Override
    protected void onDestroy() {
        openHelper.close();
        super.onDestroy();
    }

    private void saveOriginalNoteValues() {
        if(isNewNote)
            return;

        mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mOriginalNoteTitle = mNote.getTitle();
        mOriginalNoteText = mNote.getText();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling){
            if(isNewNote){
                deleteNoteFromDatabase();
            } else{
                storePreviousNoteValues();
            }
        }  else {
            saveNote();
        }
    }

    private void deleteNoteFromDatabase() {
        @SuppressLint("StaticFieldLeak")
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                getContentResolver().delete(mUri, null, null);
                return null;
            }
        };
        task.execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mOriginalNoteTitle);
        mNote.setText(mOriginalNoteText);
    }

    private void saveNote() {
        String courseId = selectedCourseId();
        String noteTitle = textNoteTitle.getText().toString();
        String noteText = textNoteText.getText().toString();
        saveNoteToDatabase(courseId, noteTitle, noteText);
    }

    private String selectedCourseId() {
        int selectedPosition = spinnerCourses.getSelectedItemPosition();
        Cursor cursor = adapterCourses.getCursor();
        cursor.moveToPosition(selectedPosition);
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COURSE_ID_COLUMN);
        String courseId = cursor.getString(courseIdPos);
        return courseId;
    }

    private void saveNoteToDatabase(String courseId, String noteTitle, String noteText){
        final ContentValues values = new ContentValues();
        values.put(Notes.COURSE_ID_COLUMN, courseId);
        values.put(Notes.NOTE_TITLE_COLUMN, noteTitle);
        values.put(Notes.NOTE_TEXT_COLUMN, noteText);

        @SuppressLint("StaticFieldLeak")
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                getContentResolver().update(mUri, values, null, null);
                return null;
            }
        };
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);

        return true;
    }

    private void displayNote() {
        String courseId = noteCursor.getString(courseIdPos);
        String noteTitle = noteCursor.getString(noteTitlePos);
        String noteText = noteCursor.getString(noteTextPos);
        int courseIndex = getIndexOfCourseId(courseId);
        spinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(noteTitle);
        textNoteText.setText(noteText);
    }

    private int getIndexOfCourseId(String courseId) {
        Cursor cursor = adapterCourses.getCursor();
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COURSE_ID_COLUMN);
        int courseRowIndex = 0;

        boolean more = cursor.moveToFirst();
        while(more){
            String cursorCourseId = cursor.getString(courseIdPos);
            if(courseId.equals(cursorCourseId))
                break;
            courseRowIndex++;
            more = cursor.moveToNext();
        }
        return courseRowIndex;
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        mNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET);
        isNewNote = mNoteId == ID_NOT_SET;
        if(isNewNote)
            createNewNote();
    }

    private void createNewNote() {
        final ContentValues values = new ContentValues();
        values.put(Notes.COURSE_ID_COLUMN, "");
        values.put(Notes.NOTE_TITLE_COLUMN, "");
        values.put(Notes.NOTE_TEXT_COLUMN, "");

        @SuppressLint("StaticFieldLeak")
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                mUri = getContentResolver().insert(Notes.CONTENT_URI, values);
                return null;
            }
        };
        task.execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_send_mail){
            sendEmail();
            return true;
        }else if (id == R.id.cancel){
            mIsCancelling = true;
            finish();
        }else if (id == R.id.action_next){
            moveNext();
        }
        else if (id == R.id.set_Reminder){
            showRemiderNotification();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showRemiderNotification() {
        String noteTitle = textNoteTitle.getText().toString();
        String noteText = textNoteText.getText().toString();
        int noteId = (int) ContentUris.parseId(mUri);
        NoteKeeperNotification.notifier(this, noteTitle, noteText, noteId);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;
        item.setEnabled(mNoteId < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();
        ++mNoteId;
        mNote = DataManager.getInstance().getNotes().get(mNoteId);
        saveOriginalNoteValues();
        displayNote();
        invalidateOptionsMenu();
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) spinnerCourses.getSelectedItem();
        String subject = textNoteTitle.getText().toString();
        String body = "Check out what I learnt in the PluralSight Course \"" + course.getTitle() + "\"\n" +
                textNoteText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_NOTES)
            loader = createLoaderNotes();
        else if(id == LOADER_COURSES)
            loader = createLoaderCourses();
        return loader;
    }

    private CursorLoader createLoaderCourses() {
        courseQueryFinished = false;
        Uri uri = Courses.CONTENT_URI;
        String [] courseColumns = {
                Courses.COURSE_TITLE_COLUMN,
                Courses.COURSE_ID_COLUMN,
                Courses._ID };
        return new CursorLoader(this, uri, courseColumns, null, null, Courses.COURSE_TITLE_COLUMN);
    }

    private CursorLoader createLoaderNotes() {
        noteQueryFinished = false;
        String [] noteColumns = {
                Notes.COURSE_ID_COLUMN,
                Notes.NOTE_TITLE_COLUMN,
                Notes.NOTE_TEXT_COLUMN };

        mUri = ContentUris.withAppendedId(Notes.CONTENT_URI, mNoteId);
        return new CursorLoader(this, mUri, noteColumns, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_NOTES)
            loadFinishedNotes(data);
        else if(loader.getId() == LOADER_COURSES){
            adapterCourses.changeCursor(data);
            courseQueryFinished = true;
            displayNoteWhenQueryFinishes();
        }
    }

    private void loadFinishedNotes(Cursor data) {
        noteCursor = data;
        courseIdPos = noteCursor.getColumnIndex(NoteInfoEntry.COURSE_ID_COLUMN);
        noteTitlePos = noteCursor.getColumnIndex(NoteInfoEntry.NOTE_TITLE_COLUMN);
        noteTextPos = noteCursor.getColumnIndex(NoteInfoEntry.NOTE_TEXT_COLUMN);
        noteCursor.moveToNext();
        noteQueryFinished = true;
        displayNoteWhenQueryFinishes();
    }

    private void displayNoteWhenQueryFinishes() {
        if(noteQueryFinished && courseQueryFinished){
            displayNote();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if(loader.getId() == LOADER_NOTES){
            if(noteCursor != null)
                noteCursor.close();
        }else if(loader.getId() == LOADER_COURSES)
            adapterCourses.changeCursor(null);
    }
}
