package com.github.amitkma.notepad.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.amitkma.notepad.R;
import com.github.amitkma.notepad.adapter.template.ModelAdapter;
import com.github.amitkma.notepad.model.Note;
import com.github.amitkma.notepad.widget.NoteViewHolder;

import java.util.ArrayList;

public class NoteAdapter extends ModelAdapter<Note, NoteViewHolder> {
	public NoteAdapter(ArrayList<Note> items, ArrayList<Note> selected, ClickListener<Note> listener) {
		super(items, selected, listener);
	}

	@Override
	public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
	}
}