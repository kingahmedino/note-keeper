package com.example.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteListAdapterClass extends RecyclerView.Adapter<NoteListAdapterClass.ViewHolder> {
    private final Context mContext;
    private final List<NoteInfo> mNotes;
    private final LayoutInflater layoutInflater;

    public NoteListAdapterClass(Context mContext, List<NoteInfo> mNotes) {
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
        this.mNotes = mNotes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoteInfo note = mNotes.get(position);
        holder.mtextCourse.setText(note.getCourse().getTitle());
        holder.mtextTitle.setText(note.getTitle());
        holder.mId = note.getId();
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mtextCourse;
        public final TextView mtextTitle;
        public int mId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mtextCourse = (TextView) itemView.findViewById(R.id.textCourse);
            mtextTitle = (TextView) itemView.findViewById(R.id.textTitle);

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
