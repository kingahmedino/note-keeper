package com.example.notekeeper;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import static com.example.notekeeper.NoteActivity.LOADER_NOTES;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private NoteListAdapterClass noteListAdapter;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private RecyclerView mRecyclerItems;
    private LinearLayoutManager notesLayoutManager;
    private CourseListAdapterClass courseListAdapter;
    private GridLayoutManager courseLayoutManager;
    private NoteKeeperOpenHelper mDbOpenHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new NoteKeeperOpenHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_notes, R.id.nav_course, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        initializeDisplayContent();

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.nav_notes) {
                    displayNotes();
                }
                if (destination.getId() == R.id.nav_course) {
                    displayCourses();
                }
                if (destination.getId() == R.id.nav_share) {
                    handleShare();
                }
                if (destination.getId() == R.id.nav_send) {
                    handleSelection(R.string.nav_send_message);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    private void handleShare() {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, "Share To - " +
                PreferenceManager.getDefaultSharedPreferences(this).getString("user_social_network", ""),
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = mDbOpenHelper.getReadableDatabase();
        getLoaderManager().restartLoader(LOADER_NOTES, null, this);
        updateNavHeader();
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.close();
    }

    private void loadNotes() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        final String[] noteColumns = {NoteInfoEntry.NOTE_TITLE_COLUMN,
                NoteInfoEntry.COURSE_ID_COLUMN, NoteInfoEntry._ID};

        String noteOrderBy = NoteInfoEntry.COURSE_ID_COLUMN + ", " + NoteInfoEntry.NOTE_TITLE_COLUMN;
        final Cursor noteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns,
                null, null, null, null, noteOrderBy);
        noteListAdapter.changeCursor(noteCursor);
    }

    private void updateNavHeader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textUserName = (TextView) headerView.findViewById(R.id.text_user_name);
        TextView textUserEmail = (TextView) headerView.findViewById(R.id.text_user_email);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String username = pref.getString("user_display_name", " ");
        String email = pref.getString("user_email", " ");

        textUserName.setText(username);
        textUserEmail.setText(email);
    }

    private void initializeDisplayContent(){
        DataManager.loadFromDatataBase(mDbOpenHelper);

        mRecyclerItems = (RecyclerView) findViewById(R.id.list_items);
        notesLayoutManager = new LinearLayoutManager(this);
        courseLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.course_grid_span));

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        noteListAdapter = new NoteListAdapterClass(this, null);
        courseListAdapter = new CourseListAdapterClass(this, courses);
        displayNotes();
    }

    private void displayNotes() {
        mRecyclerItems.setAdapter(noteListAdapter);
        mRecyclerItems.setLayoutManager(notesLayoutManager);
    }

    private void displayCourses() {
        mRecyclerItems.setAdapter(courseListAdapter);
        mRecyclerItems.setLayoutManager(courseLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void handleSelection(int message_id) {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, message_id, Snackbar.LENGTH_LONG).show();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_NOTES){
            loader = new CursorLoader(this){
                @Override
                public Cursor loadInBackground() {
                    final String[] noteColumns = {NoteInfoEntry.NOTE_TITLE_COLUMN,
                            NoteInfoEntry.getQName(NoteInfoEntry._ID),
                            CourseInfoEntry.COURSE_TITLE_COLUMN};

                    String noteOrderBy = CourseInfoEntry.COURSE_TITLE_COLUMN + ", " + NoteInfoEntry.NOTE_TITLE_COLUMN;
                    String tablesWithJoin = NoteInfoEntry.TABLE_NAME + " JOIN " + CourseInfoEntry.TABLE_NAME +
                            " ON " + NoteInfoEntry.getQName(NoteInfoEntry.COURSE_ID_COLUMN) +
                            " = " + CourseInfoEntry.getQName(CourseInfoEntry.COURSE_ID_COLUMN);

                    return db.query(tablesWithJoin, noteColumns,
                            null, null, null, null, noteOrderBy);
                }
            };
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_NOTES){
            noteListAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_NOTES){
            noteListAdapter.changeCursor(null);
        }
    }
}
