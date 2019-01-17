package com.mansha.memoryrecall;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {

    private String[] captions;
    private int[] imageIds;
    private Listener listener;
    private Cursor cursor;

    interface Listener{
        void onClick(int position);
        void onLongClick(int position);
    }


    public void setListener(Listener listener){
        this.listener = listener;
    }



    public CardViewAdapter(String[] captions, int[] imageIds){
        this.captions = captions;
        this.imageIds = imageIds;
    }

    public CardViewAdapter(Cursor cursor){
        this.cursor = cursor;
    }

    public void updateCursor(Cursor newCursor){
        this.cursor = newCursor;
    }

    @Override
    public int getItemCount() {
        if (cursor != null){
            return cursor.getCount();
        } else {
            return captions.length;
        }
    }

    @NonNull
    @Override
    public CardViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv = (CardView)LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        ImageView imageView = (ImageView)cardView.findViewById(R.id.info_image);
        TextView textView = (TextView)cardView.findViewById(R.id.info_text);
        ImageView soundFlag = (ImageView)cardView.findViewById(R.id.soundFlag);

        if (cursor == null) {

            Drawable drawable = ContextCompat.getDrawable(cardView.getContext(), imageIds[position]);
            imageView.setImageDrawable(drawable);
            imageView.setContentDescription(captions[position]);
            textView.setText(captions[position]);
        } else {

            cursor.moveToPosition(position);
            textView.setText(cursor.getString(0));
            String imagePath = cursor.getString(1);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
            imageView.setContentDescription(cursor.getString(0));
            if (cursor.getString(2)== null){
                soundFlag.setVisibility(View.INVISIBLE);
            }


//            byte[] byteArray = cursor.getBlob(1);
//            if (byteArray != null && byteArray.length > 0 ) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                imageView.setImageBitmap(bitmap);
//                imageView.setContentDescription(cursor.getString(0));
//            }

        }


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onClick(position);
                }
            }
        });



        cardView.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                if(listener != null){
                    listener.onLongClick(position);
                    return true;
                }

                return false;
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;

        public ViewHolder(CardView v){
            super(v);
            cardView = v;
        }


    }
}
