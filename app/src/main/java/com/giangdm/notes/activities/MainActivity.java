package com.giangdm.notes.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.giangdm.notes.R;
import com.giangdm.notes.adapters.NoteAdapter;
import com.giangdm.notes.databases.NotesManager;
import com.giangdm.notes.models.Notes;
import com.giangdm.notes.models.SwipeToDeleteCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String strUrl = "https://mp3.zing.vn/bai-hat/Thang-Dien-JustaTee-Phuong-Ly/ZW9DFW9A.html";

    private NotesManager notesManage;
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private Menu menu;

    private boolean isProductViewAsList = false;

    public static final String KEY_ID_NOTE = "key_id_note";
    public static final String KEY_TITLE_NOTE = "key_title_note";
    public static final String KEY_CONTENT_NOTE = "key_content_note";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notesManage = new NotesManager(this);

        new LoadData().execute(strUrl);


        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(yourmilliseconds);
        System.out.println(sdf.format(resultdate));

        FloatingActionButton button = findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        recyclerView = findViewById(R.id.main_rcl_view);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new NoteAdapter(this, notesManage.getAllNotes(), this);
        recyclerView.setAdapter(adapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        isProductViewAsList = false;
        recyclerView.setLayoutManager(manager);
        switch (resultCode) {
            case 100:
                adapter.updateList(notesManage.getAllNotes());
                adapter.notifyDataSetChanged();
                break;
            case 200:
                adapter.updateList(notesManage.getAllNotes());
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int pos = (int) v.getTag();
        switch (v.getId()) {
            case R.id.item_note_layout:
                Intent intent = new Intent(this, AddEditActivity.class);
                intent.putExtra(KEY_ID_NOTE, notesManage.getAllNotes().get(pos).getId() + "");
                intent.putExtra(KEY_TITLE_NOTE, notesManage.getAllNotes().get(pos).getTitle());
                intent.putExtra(KEY_CONTENT_NOTE, notesManage.getAllNotes().get(pos).getContent());
                startActivityForResult(intent, 200);
                break;
            default:
                break;
        }
    }

    private class LoadData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document document = Jsoup.connect(strings[0]).get();
                Elements element = document.select(".playing-song");
                for (Element e : element) {
                    Log.d("aaa", "\ndoInBackground: " + e.getElementsByTag("div").size());
                }
                String eAudio = element.last().getElementsByTag("audio").attr("src").toString();
                Log.d("aaa", "doInBackground Audio: " + eAudio);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_type) {
            isProductViewAsList = adapter.toggleItemViewType();
            supportInvalidateOptionsMenu();
            recyclerView.setLayoutManager(isProductViewAsList ? new GridLayoutManager(this, 2) : new LinearLayoutManager(this));
            adapter.notifyDataSetChanged();

            invalidateOptionsMenu();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_change_type);
        this.menu = menu;
        if (isProductViewAsList) {
            item.setIcon(getResources().getDrawable(R.drawable.ic_list));
        } else {
            item.setIcon(getResources().getDrawable(R.drawable.ic_grid));
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
