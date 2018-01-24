package com.github.amitkma.notepad.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.amitkma.notepad.R;
import com.github.amitkma.notepad.db.OpenHelper;
import com.github.amitkma.notepad.fragment.template.RecyclerFragment;
import com.github.amitkma.notepad.inner.Animator;

public class CategoryFragmentOwner extends Fragment implements RecyclerFragment.Callbacks {
    private Toolbar toolbar;
    private CategoryFragment fragment;

    public static final int REQUEST_CODE = 1;
    public static final int RESULT_CHANGE = 101;

    public CategoryFragmentOwner() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_category, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if(savedInstanceState == null){
            fragment = new CategoryFragment();
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
    }

    public void onBackPressed() {
        if (fragment.isFabOpen) {
            fragment.toggleFab(true);
            return;
        }

        if (fragment.selectionState) {
            fragment.toggleSelection(false);
            return;
        }

        Intent data = new Intent();
        data.putExtra("position", fragment.categoryPosition);
        data.putExtra(OpenHelper.COLUMN_COUNTER, fragment.items.size());
        setResult(RESULT_CHANGE, data);
        finish();
    }
}
