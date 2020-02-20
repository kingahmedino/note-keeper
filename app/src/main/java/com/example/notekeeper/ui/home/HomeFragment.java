package com.example.notekeeper.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.notekeeper.R;

public class HomeFragment extends Fragment {

//    private HomeViewModel homeViewModel;
//    private NoteListAdapterClass noteListAdapter;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel =
//                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.content_main, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//               // textView.setText(s);
//            }
//        });initializeDisplayContent();
        return root;
    }

//   private void initializeDisplayContent(){
//        final RecyclerView recyclerNotes = (RecyclerView) root.findViewById(R.id.list_items);
//        final LinearLayoutManager notesLayoutManager = new LinearLayoutManager(getContext());
//        recyclerNotes.setLayoutManager(notesLayoutManager);
//
//        List<NoteInfo> notes = DataManager.getInstance().getNotes();
//        noteListAdapter = new NoteListAdapterClass(getContext(), notes);
//        recyclerNotes.setAdapter(noteListAdapter);
//    }
}