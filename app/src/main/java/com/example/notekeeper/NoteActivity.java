package com.example.notekeeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_ID = "com.example.notekeeper.NOTE_POSITION";
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.example.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.notekeeper.ORIGINAL_NOTE_TEXT";
    public static final int ID_NOT_SET = -1;
    private NoteInfo mNote = new NoteInfo(DataManager.getInstance().getCourses().get(0), "","");
    private boolean isNewNote;
    private Spinner spinnerCourses;
    private EditText textNoteTitle;
    private EditText textNoteText;
    private int notePosition;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        spinnerCourses = (Spinner) findViewById(R.id.spinner_courses);

        openHelper = new NoteKeeperOpenHelper(this);

        adapterCourses = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
                new String[] {CourseInfoEntry.COURSE_TITLE_COLUMN},
                new int[] {android.R.id.text1}, 0);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapterCourses);

        loadCourseData();
        readDisplayStateValues();

        if(savedInstanceState == null) {
            saveOriginalNoteValues();
        } else{
            restoreOriginalNoteValues(savedInstanceState);
        }

        textNoteTitle = (EditText) findViewById(R.id.text_note_title);
        textNoteText = (EditText) findViewById(R.id.text_note_text);

        if(!isNewNote)
            loadNoteData();
    }

    private void loadCourseData() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String [] courseColumns = {
                CourseInfoEntry.COURSE_TITLE_COLUMN,
                CourseInfoEntry.COURSE_ID_COLUMN,
                CourseInfoEntry._ID };
        Cursor cursor = db.query( CourseInfoEntry.TABLE_NAME, courseColumns,
                null, null, null, null,
                CourseInfoEntry.COURSE_TITLE_COLUMN);
        adapterCourses.changeCursor(cursor);
    }

    private void loadNoteData() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String courseId = "android_intents";
        String titleStart = "dynamic";

        String selection = NoteInfoEntry._ID + " = ?";
        String [] selectionArgs = {Integer.toString(mNoteId)};
        String [] noteColumns = { NoteInfoEntry.COURSE_ID_COLUMN, NoteInfoEntry.NOTE_TITLE_COLUMN, NoteInfoEntry.NOTE_TEXT_COLUMN };

        noteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns, selection, selectionArgs, null, null, null);
        courseIdPos = noteCursor.getColumnIndex(NoteInfoEntry.COURSE_ID_COLUMN);
        noteTitlePos = noteCursor.getColumnIndex(NoteInfoEntry.NOTE_TITLE_COLUMN);
        noteTextPos = noteCursor.getColumnIndex(NoteInfoEntry.NOTE_TEXT_COLUMN);
        noteCursor.moveToNext();
        displayNote();
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
                DataManager.getInstance().removeNote(notePosition);
            } else{
                storePreviousNoteValues();
            }
        }  else {
            saveNote();
        }
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
        mNote.setCourse((CourseInfo) spinnerCourses.getSelectedItem());
        mNote.setTitle(textNoteTitle.getText().toString());
        mNote.setText(textNoteText.getText().toString());
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
        if(isNewNote) {
            createNewNote();
        }
           // mNote = DataManager.getInstance().getNotes().get(mNoteId);
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        notePosition = dm.createNewNote();
        mNote = dm.getNotes().get(notePosition);
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

        return super.onOptionsItemSelected(item);
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
}
