package com.mansha.memoryrecall;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThingsFragment extends Fragment {

    private AudioPlayer audioPlayer;
    private SQLiteDatabase db;
    private Cursor cursor;
    private RecyclerView thingsRecycler;
    private Context context;
    private String categoryName = "Things";

    public ThingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = inflater.getContext();
        View view = inflater.inflate(R.layout.people_recyclerview_fragment, container, false);
        thingsRecycler = (RecyclerView)view.findViewById(R.id.people_recyclerview_fragment);

        final DatabaseHelper dbHelper = DatabaseHelper.getsDBInstance(context);

        try {
            db = dbHelper.getReadableDatabase();
            cursor = dbHelper.getCursor(db, categoryName);
        } catch (SQLException e){
            Toast toast = Toast.makeText(context, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        CardViewAdapter cardViewAdapter = new CardViewAdapter(cursor);
        thingsRecycler.setAdapter(cardViewAdapter);

        /**
         * Get the orientation and set the gridlaout span based on orientation
         */
        //Orientation 1 = Potrait and 2 = Landscape
        int orientation = context.getResources().getConfiguration().orientation;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, orientation == 1 ? getResources().getInteger(R.integer.grid_layout_span_potrait) : getResources().getInteger(R.integer.grid_layout_span_landscape));
        thingsRecycler.setLayoutManager(gridLayoutManager);

        audioPlayer = new AudioPlayer(thingsRecycler);
        cardViewAdapter.setListener(new CardViewAdapter.Listener() {
            @Override
            public void onClick(int position){
                Cursor localCursor = dbHelper.getCursor(db, categoryName);
                localCursor.moveToPosition(position);
                String pathToAudioFile = localCursor.getString(2);
                if (pathToAudioFile != null) {
                    audioPlayer.setAudioFile(pathToAudioFile);
                    audioPlayer.playAudio();
                }
                localCursor.close();
            }

            @Override
            public void onLongClick(int position) {
                Log.d("PeopleFragment", "Long click listener pressed");
                Cursor localCursor = dbHelper.getCursor(db, categoryName);
                localCursor.moveToPosition(position);
                Entity entity = new Entity(localCursor.getString(0), localCursor.getString(1), localCursor.getString(2), localCursor.getString(4));
                Log.d("PeopleFragment", "Entity name " + entity.getEntityName());
                Activity addItemActivity  = getActivity();
                Intent intent = new Intent(addItemActivity, AddItem.class);
                intent.putExtra("Entity", entity);
                startActivityForResult(intent, 100);
            }
        });

        return thingsRecycler;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        CardViewAdapter cardViewAdapter = (CardViewAdapter) thingsRecycler.getAdapter();
        DatabaseHelper databaseHelper = DatabaseHelper.getsDBInstance(context);
        cursor = databaseHelper.getCursor(db, categoryName);
        cardViewAdapter.updateCursor(cursor);
        cardViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        audioPlayer.releaseAudioPlayer();
    }

}
