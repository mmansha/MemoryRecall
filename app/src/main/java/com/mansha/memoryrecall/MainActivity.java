package com.mansha.memoryrecall;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionPageAdapter sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(sectionPageAdapter);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
        databaseHelper = DatabaseHelper.getsDBInstance(this);
        db = databaseHelper.getWritableDatabase();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseHelper databaseHelper = DatabaseHelper.getsDBInstance(this);
        databaseHelper.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(this, AddItem.class);
                startActivityForResult(intent, 100);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String currentTabSelected;
        Entity entityCreated = null;


        //Get data from Add item activity call
        super.onActivityResult(requestCode, resultCode, data);

        currentTabSelected = tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString();

        if (requestCode == 100){
            if(resultCode == RESULT_OK){
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    entityCreated = bundle.getParcelable("Entity");
                }

                //Insert data into database
                DatabaseHelper databaseHelper = DatabaseHelper.getsDBInstance(this);
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                if (db != null && entityCreated != null) {
                    if(entityCreated.entityStatus.compareTo("NEW") == 0) {
                        databaseHelper.insertData(db, currentTabSelected, entityCreated.entityGuid, entityCreated.entityName, entityCreated.entityImageFile, entityCreated.entitySoundFile, 0);
                    } else {
                        databaseHelper.updateData(db, currentTabSelected, entityCreated.entityGuid, entityCreated.entityName, entityCreated.entityImageFile, entityCreated.entitySoundFile, 0);
                    }
                }
//                db.close();
//                databaseHelper.close();
            }


            //For deleting existing entry
            if(resultCode == RESULT_FIRST_USER){
                Log.d("MainActivity", "Get result code " + resultCode);
                String guidForDeleteEntity;
                Bundle bundle = data.getExtras();
                Log.d("MainActivity", "Is bundle null " + bundle.isEmpty());
                if(bundle != null){
                    guidForDeleteEntity = bundle.getString("GUID");
                    Log.d("MainActivity", "Guid for deleted entity " + guidForDeleteEntity);
                    DatabaseHelper databaseHelper = DatabaseHelper.getsDBInstance(this);
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    if (db != null && guidForDeleteEntity != null) {
                        databaseHelper.deleteRow(db, guidForDeleteEntity);
                    }
//                    db.close();
//                    databaseHelper.close();
                }

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private class SectionPageAdapter extends FragmentPagerAdapter {
        public SectionPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PeopleFragment();
                case 1:
                    return new FoodFragment();
                case 2:
                    return new PlacesFragment();
                case 3:
                    return new ThingsFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getText(R.string.peopleTab);
                case 1:
                    return getResources().getText(R.string.foodTab);
                case 2:
                    return getResources().getText(R.string.placesTab);
                case 3:
                    return getResources().getText(R.string.thingsTab);
            }

            return null;
        }

    }
}
