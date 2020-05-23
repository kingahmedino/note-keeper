package com.example.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;
import com.example.notekeeper.NoteKeeperProviderContract.Courses;
import com.example.notekeeper.NoteKeeperProviderContract.Notes;

import java.util.List;

public class NoteListAdapterClass extends RecyclerView.Adapter<NoteListAdapterClass.ViewHolder> {
    private final Context mContext;
    private Cursor mCursor;
    private final LayoutInflater layoutInflater;
    private int coursePos;
    private int noteTitlePos;
    private int iDPos;

    public NoteListAdapterClass(Context mContext, Cursor mCursor) {
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
        this.mCursor = mCursor;
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if(mCursor == null)
            return;
        coursePos = mCursor.getColumnIndex(Courses.COURSE_TITLE_COLUMN);
        noteTitlePos = mCursor.getColumnIndex(Notes.NOTE_TITLE_COLUMN);
        iDPos = mCursor.getColumnIndex(Notes._ID);
    }

    public void changeCursor(Cursor cursor) {
        if(mCursor != null)
            mCursor.close();
        mCursor = cursor;
        populateColumnPositions();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String courseId = mCursor.getString(coursePos);
        String noteTitle = mCursor.getString(noteTitlePos);
        int Id = mCursor.getInt(iDPos);

        holder.mtextCourse.setText(courseId);
        holder.mtextTitle.setText(noteTitle);
        holder.mId = Id;
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mtextCourse;
        public final TextView mtextTitle;
        public int mId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mtextCourse = itemView.findViewById(R.id.textCourse);
            mtextTitle = itemView.findViewById(R.id.textTitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, NoteActivity.class);
                    intent.putExtra(NoteActivity.NOTE_ID, mId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
