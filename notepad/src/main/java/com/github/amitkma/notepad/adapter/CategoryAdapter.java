package com.github.amitkma.notepad.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.amitkma.notepad.R;
import com.github.amitkma.notepad.adapter.template.ModelAdapter;
import com.github.amitkma.notepad.model.Category;
import com.github.amitkma.notepad.widget.CategoryViewHolder;

import java.util.ArrayList;

public class CategoryAdapter extends ModelAdapter<Category, CategoryViewHolder> {
	public CategoryAdapter(ArrayList<Category> items, ArrayList<Category> selected, ClickListener<Category> listener) {
		super(items, selected, listener);
	}

	@Override
	public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false));
	}
}
