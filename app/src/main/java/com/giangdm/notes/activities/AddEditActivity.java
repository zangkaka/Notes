package com.giangdm.notes.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.giangdm.notes.R;
import com.giangdm.notes.databases.NotesManager;
import com.giangdm.notes.models.Notes;

public class AddEditActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mCancelBtn;
    private Button mSaveBtn;
    private EditText mTitleEdt;
    private EditText mContentEdt;
    private NotesManager notesManager;
    private String mId = null, mTitle = null, mContent = null;
    private boolean isAdd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initViews();

        Intent intent = getIntent();
        mId = intent.getStringExtra(MainActivity.KEY_ID_NOTE);
        mTitle = intent.getStringExtra(MainActivity.KEY_TITLE_NOTE);
        mContent = intent.getStringExtra(MainActivity.KEY_CONTENT_NOTE);

        if (!TextUtils.isEmpty(mId) && !TextUtils.isEmpty(mTitle)) {
            mTitleEdt.setText(mTitle);
            mContentEdt.setText(mContent);
            isAdd = true;
        }
    }

    private void initViews() {
        notesManager = new NotesManager(this);

        mCancelBtn = findViewById(R.id.add_edit_cancel_btn);
        mSaveBtn = findViewById(R.id.add_edit_save_btn);
        mTitleEdt = findViewById(R.id.add_edit_title_edt);
        mContentEdt = findViewById(R.id.add_edit_content_edt);

        mCancelBtn.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(mTitleEdt.getText()) || !TextUtils.isEmpty(mContentEdt.getText())) {
            showAlertDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_save) {
            clickSaveButton();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_edit_cancel_btn:
                onBackPressed();
                break;
            case R.id.add_edit_save_btn:
                clickSaveButton();
                break;
            default:
                break;
        }
    }

    private void clickSaveButton() {

        String title = mTitleEdt.getText().toString();
        String content = mContentEdt.getText().toString();
        String strTime = String.valueOf(System.currentTimeMillis());

        if (isAdd) {
            if (!TextUtils.isEmpty(title)) {
                notesManager.updateNote(mId, title, content, strTime);
                Intent intent = new Intent();
                setResult(200, intent);
                finish();
            } else {
                Toast.makeText(this, "Bạn chưa có tiêu đề", Toast.LENGTH_SHORT).show();
            }

        } else {
            if (!TextUtils.isEmpty(title)) {
                Notes notes = new Notes(title, content, strTime);

                notesManager.addNote(notes);
                Intent intent = new Intent();
                setResult(100, intent);
                finish();
            } else {
                Toast.makeText(this, "Bạn chưa có tiêu đề", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ghi chú");
        builder.setMessage("Bạn có muốn lưu ghi chú không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
