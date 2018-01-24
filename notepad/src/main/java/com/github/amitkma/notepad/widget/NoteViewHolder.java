package com.github.amitkma.notepad.widget;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.amitkma.notepad.R;
import com.github.amitkma.notepad.inner.Formatter;
import com.github.amitkma.notepad.model.DatabaseModel;
import com.github.amitkma.notepad.model.Note;
import com.github.amitkma.notepad.widget.template.ModelViewHolder;

public class NoteViewHolder extends ModelViewHolder<Note> {
	public ImageView badge;
	public TextView title;
	public TextView date;

	public NoteViewHolder(View itemView) {
		super(itemView);
		badge = (ImageView) itemView.findViewById(R.id.badge_icon);
		title = (TextView) itemView.findViewById(R.id.title_txt);
		date = (TextView) itemView.findViewById(R.id.date_txt);
	}

	@Override
	public void populate(Note item) {
		if (item.type == DatabaseModel.TYPE_NOTE_DRAWING) {
			badge.setImageResource(R.drawable.fab_drawing);
		} else {
			badge.setImageResource(R.drawable.fab_type);
		}
		title.setText(item.title);
		date.setText(Formatter.formatShortDate(item.createdAt));
	}
}
