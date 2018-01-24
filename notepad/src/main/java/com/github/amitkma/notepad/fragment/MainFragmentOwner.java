package com.github.amitkma.notepad.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.amitkma.notepad.R;
import com.github.amitkma.notepad.activity.CategoryActivity;
import com.github.amitkma.notepad.adapter.CategoryAdapter;
import com.github.amitkma.notepad.adapter.template.ModelAdapter;
import com.github.amitkma.notepad.db.OpenHelper;
import com.github.amitkma.notepad.fragment.template.RecyclerFragment;
import com.github.amitkma.notepad.inner.Animator;
import com.github.amitkma.notepad.model.Category;
import com.github.amitkma.notepad.model.DatabaseModel;

public class MainFragmentOwner extends Fragment implements RecyclerFragment.Callbacks {
    private Toolbar toolbar;
    private View selectionEdit;
    private MainFragment fragment;

    public MainFragmentOwner() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        selectionEdit = view.findViewById(R.id.selection_edit);
        selectionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.onEditSelected();
            }
        });

        if(savedInstanceState == null){
            fragment = new MainFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();

        }
    }

    @Override
    public void onChangeSelection(boolean state) {
        if (state) {
            Animator.create(getActivity().getApplicationContext())
                    .on(toolbar)
                    .setEndVisibility(View.INVISIBLE)
                    .animate(R.anim.fade_out);
        } else {
            Animator.create(getActivity().getApplicationContext())
                    .on(toolbar)
                    .setStartVisibility(View.VISIBLE)
                    .animate(R.anim.fade_in);
        }
    }

    @Override
    public void toggleOneSelection(boolean state) {
        selectionEdit.setVisibility(state ? View.VISIBLE : View.GONE);
    }
}
