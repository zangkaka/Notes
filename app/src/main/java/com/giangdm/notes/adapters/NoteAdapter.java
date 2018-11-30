package com.giangdm.notes.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.giangdm.notes.R;
import com.giangdm.notes.activities.MainActivity;
import com.giangdm.notes.databases.NotesManager;
import com.giangdm.notes.models.Notes;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter {

    public Context context;
    private List<Notes> list;
    private View.OnClickListener mOnClickListener;
    private boolean isProductViewAsList = false;
    private static final int LIST_ITEM = 0;
    private static final int GRID_ITEM = 1;
    private int mRecentlyDeletedItemPosition;
    private Notes recentNotes;
    private MainActivity activity;
    private NotesManager notesManager;


    public NoteAdapter(Context context, List<Notes> list, View.OnClickListener mOnClickListener) {
        this.context = context;
        this.list = list;
        this.mOnClickListener = mOnClickListener;
        activity = (MainActivity) context;
        this.notesManager = new NotesManager(context);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        if (viewType == LIST_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_list, parent, false);
        } else if (viewType == GRID_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_grid, parent, false);
        }
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Notes notes = list.get(position);
        ((NoteViewHolder) holder).titleTxt.setText(notes.getTitle());
        ((NoteViewHolder) holder).contentTxt.setText(notes.getContent());
        ((NoteViewHolder) holder).itemLayout.setTag(position);
        ((NoteViewHolder) holder).itemLayout.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isProductViewAsList) {
            return GRID_ITEM;
        } else {
            return LIST_ITEM;
        }
    }

    public boolean toggleItemViewType() {
        isProductViewAsList = !isProductViewAsList;
        return isProductViewAsList;
    }

    public void deleteItem(int position) {
        notesManager.deleteNote(list.get(position).getId() + "");
        recentNotes = list.get(position);
        mRecentlyDeletedItemPosition = position;
        list.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
        showSnackBar();
    }

    private void showSnackBar() {

        View view = activity.findViewById(R.id.main_layout);
        Snackbar snackbar = Snackbar.make(view, "Bạn có muốn quay lại?", Snackbar.LENGTH_LONG);
        snackbar.setAction("Quay lại", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoDelete();
            }
        });
        snackbar.show();

    }

    private void undoDelete() {
        list.add(mRecentlyDeletedItemPosition, recentNotes);
        notifyItemInserted(mRecentlyDeletedItemPosition);
        notesManager.addNote(recentNotes);
        notifyDataSetChanged();
    }

    public void showAlertDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xoá ghi chú!");
        builder.setMessage("Bạn có muốn  ghi chú xoá này không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // xoa trong db truoc -> xoa tren list sau
                notesManager.deleteNote(list.get(position).getId() + "");
                notifyItemRemoved(position);
                list.remove(position);

                notifyDataSetChanged();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                notifyDataSetChanged();
                dialogInterface.dismiss();

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Log.d("", "showAlertDialog: " + list.size());
    }

    public void updateList(List<Notes> allNotes) {
        this.list = allNotes;
    }


    class NoteViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTxt, contentTxt;
        public RelativeLayout itemLayout;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.item_note_list_title);
            contentTxt = itemView.findViewById(R.id.item_note_list_content);
            itemLayout = itemView.findViewById(R.id.item_note_layout);
        }
    }
}
