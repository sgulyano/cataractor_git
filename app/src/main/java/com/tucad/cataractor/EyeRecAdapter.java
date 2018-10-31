package com.tucad.cataractor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class EyeRecAdapter extends RecyclerView.Adapter<EyeRecAdapter.EyeRecViewHolder> {
    private static List<EyeRecord> mDataset;
    private Context mContext;

    private final static String TAG = "EyeRecAdapter";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class EyeRecViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mTextViewName;
        TextView mTextViewPhotoDate;
        ImageView imageViewEye;
        EyeRecViewHolder(View v) {
            super(v);
            mTextViewName = v.findViewById(R.id.textViewName);
            mTextViewPhotoDate = v.findViewById(R.id.textViewPhotoDate);
            imageViewEye = v.findViewById(R.id.imageViewEye);
            v.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         EyeRecord eyerec = mDataset.get(getAdapterPosition());

                                         Intent intent = new Intent(mContext, DetailActivity.class);
                                         Bundle extras = new Bundle();
                                         extras.putString(FormActivity.EXTRA_FIRSTNAME, eyerec.getFirstname());
                                         extras.putString(FormActivity.EXTRA_LASTNAME, eyerec.getLastname());
                                         extras.putString(FormActivity.EXTRA_AGE, eyerec.getAge());
                                         extras.putString(FormActivity.EXTRA_SEX, eyerec.getSex());
                                         extras.putString(FormActivity.EXTRA_IMAGEPATH, eyerec.getImagepath());
                                         intent.putExtras(extras);
                                         mContext.startActivity(intent);
                                     }
                                 }
            );
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    EyeRecAdapter(Context myContext, List<EyeRecord> myDataset) {
        mContext = myContext;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EyeRecAdapter.EyeRecViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.eyerecord_view, parent, false);
        return new EyeRecViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(EyeRecViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        EyeRecord rec = mDataset.get(position);
        holder.mTextViewName.setText(rec.getFirstname() + " " + rec.getLastname());

        String imgpath = rec.getImagepath();
        String filename = imgpath.substring(imgpath.lastIndexOf("/")+1);
        holder.mTextViewPhotoDate.setText(filename.substring(4,filename.length()-4));

        try {
            //TODO: show question mark when image not available
            Bitmap thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imgpath), 128, 128);
            holder.imageViewEye.setImageBitmap(thumb);
            holder.imageViewEye.setBackgroundResource(android.R.color.transparent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Fail load thumbnail");
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
