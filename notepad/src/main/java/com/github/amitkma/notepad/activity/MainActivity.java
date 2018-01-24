package com.github.amitkma.notepad.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.amitkma.notepad.Notepad;
import com.github.amitkma.notepad.R;
import com.github.amitkma.notepad.db.Controller;
import com.github.amitkma.notepad.dialog.ImportDialog;
import com.github.amitkma.notepad.dialog.SaveDialog;
import com.github.amitkma.notepad.fragment.MainFragment;
import com.github.amitkma.notepad.fragment.template.RecyclerFragment;
import com.github.amitkma.notepad.inner.Animator;

import org.json.JSONArray;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity implements RecyclerFragment.Callbacks {
	private boolean exitStatus = false;

	private MainFragment fragment;
	private Toolbar toolbar;
	private View selectionEdit;
	public Handler handler = new Handler();
	public Runnable runnable = new Runnable() {
		@Override
		public void run() {
			exitStatus = false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		try {
			//noinspection ConstantConditions
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		} catch (Exception ignored) {
		}



		selectionEdit = findViewById(R.id.selection_edit);
		selectionEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				fragment.onEditSelected();
			}
		});

		if (savedInstanceState == null) {
			fragment = new MainFragment();

			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, fragment)
				.commit();
		}
	}

	@Override
	public void onChangeSelection(boolean state) {
		if (state) {
			Animator.create(getApplicationContext())
				.on(toolbar)
				.setEndVisibility(View.INVISIBLE)
				.animate(R.anim.fade_out);
		} else {
			Animator.create(getApplicationContext())
				.on(toolbar)
				.setStartVisibility(View.VISIBLE)
				.animate(R.anim.fade_in);
		}
	}

	@Override
	public void toggleOneSelection(boolean state) {
		selectionEdit.setVisibility(state ? View.VISIBLE : View.GONE);
	}

/*	private void restoreData() {
		ImportDialog.newInstance(
			R.string.restore,
			new String[]{Notepad.BACKUP_EXTENSION},
			new ImportDialog.ImportListener() {
				@Override
				public void onSelect(final String path) {
					new Thread() {
						@Override
						public void run() {
							try {
								readBackupFile(path);

								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										fragment.loadItems();

										Snackbar.make(fragment.fab != null ? fragment.fab : toolbar, R.string.data_restored, Snackbar.LENGTH_LONG).show();
									}
								});
							} catch (final Exception e){
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										new MaterialDialog.Builder(MainActivity.this)
											.title(R.string.restore_error)
											.positiveText(R.string.ok)
											.content(e.getMessage())
											.onPositive(new MaterialDialog.SingleButtonCallback() {
												@Override
												public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
													dialog.dismiss();
												}
											})
											.show();
									}
								});
							} finally {
								interrupt();
							}
						}
					}.start();
				}

				@Override
				public void onError(String msg) {
					new MaterialDialog.Builder(MainActivity.this)
						.title(R.string.restore_error)
						.positiveText(R.string.ok)
						.content(msg)
						.onPositive(new MaterialDialog.SingleButtonCallback() {
							@Override
							public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
								dialog.dismiss();
							}
						})
						.show();
				}
			}
		).show(getSupportFragmentManager(), "");
	}

	private void backupData() {
		SaveDialog.newInstance(
			R.string.backup,
			"memento",
			Notepad.BACKUP_EXTENSION,
			new SaveDialog.SaveListener() {
				@Override
				public void onSelect(final String path) {
					new Thread() {
						@Override
						public void run() {
							try {
								saveBackupFile(path);

								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										new MaterialDialog.Builder(MainActivity.this)
											.title(R.string.backup)
											.positiveText(R.string.ok)
											.content(getString(R.string.backup_saved, path))
											.onPositive(new MaterialDialog.SingleButtonCallback() {
												@Override
												public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
													dialog.dismiss();
												}
											})
											.show();
									}
								});
							} catch (final Exception e) {
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										new MaterialDialog.Builder(MainActivity.this)
											.title(R.string.backup_error)
											.positiveText(R.string.ok)
											.content(e.getMessage())
											.onPositive(new MaterialDialog.SingleButtonCallback() {
												@Override
												public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
													dialog.dismiss();
												}
											})
											.show();
									}
								});
							} finally {
								interrupt();
							}
						}
					}.start();
				}

				@Override
				public void onError() {

				}

				@Override
				public void onCancel() {

				}
			}
		).show(getSupportFragmentManager(), "");
	}*/

	/*private void readBackupFile(String path) throws Exception {
		DataInputStream dis = new DataInputStream(new FileInputStream(path));
		byte[] backup_data = new byte[dis.available()];
		dis.readFully(backup_data);
		JSONArray json = new JSONArray(new String(backup_data));
		dis.close();

		Controller.instance.readBackup(json);
	}

	private void saveBackupFile(String path) throws Exception {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
			fos.write("[".getBytes("UTF-8"));
			Controller.instance.writeBackup(fos);
			fos.write("]".getBytes("UTF-8"));
			fos.flush();
		} finally {
			if (fos != null) fos.close();
		}
	}*/
}
