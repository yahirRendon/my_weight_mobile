package com.example.rendonyahirmyweightapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Class for managing the items/objects in the RecyclerView
 */
public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ExampleViewHolder> {


    private ArrayList<EntryItem> mExampleList;          // ArrayList contain daily entry object
    private OnNoteListener mOnNoteListener;             // Custom click listener for items/objects in RecycleView
    private int unitInt;                                // determine the unit for displaying weight


    /**
     * Constructor for initializing the Adapter
     *
     * @param exampleList       the ArrayList of data the make sup RecycleView
     * @param onNoteListener    the click listener
     */
    public EntryAdapter(ArrayList<EntryItem> exampleList, OnNoteListener onNoteListener, int unit) {
        mExampleList = exampleList;
        this.mOnNoteListener = onNoteListener;
        unitInt = unit;
    }

    /**
     * Override the onCreate for the ViewHolder
     *
     * @param parent        the ViewGroup
     * @param viewType      the integer for the view
     * @return              the ExampleViewHolder
     */
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Declare and initialize the inflater for creating the View
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mOnNoteListener);
        return evh;
    }

    /**
     * Override the onBindViewHolder for modifying items in the RecyclerView
     *
     * @param holder        the holder
     * @param position      the index position of item in the View
     */
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        EntryItem currentItem = mExampleList.get(position);
        String tempUnitString = null;
        if(unitInt == 0) {
            tempUnitString = " lbs";
        } else {
            tempUnitString = " kg";
        }

        holder.mDateText.setText(currentItem.getDate());
        holder.mWeightText.setText(currentItem.getWeight() + tempUnitString);
    }

    /**
     * Set the size of the Adapter/Recycler view
     *
     * @return      the size of the ArrayList the populates RecyclerView
     */
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    /**
     * Class accessing the RecyclerView's individual View components
     */
    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView mDateText;              // the date TextView within an item
        public TextView mWeightText;            // the weight TextView within an item

        public OnNoteListener onNoteListener;   // custom click Listener for the item

        /**
         * Constructor for creating ViewHolder
         *
         * @param itemView          // the individual item view
         * @param onNoteListener    // the click listener
         */
        public ExampleViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            mDateText = itemView.findViewById(R.id.text_date);          // initialize the var to the view
            mWeightText = itemView.findViewById(R.id.text_weight);      // initialize the var to the view

            this.onNoteListener = onNoteListener;                       // initialize the var to the listener

            /**
             *  Create click listener for the delete button of the individual item in RecyclerView/Adapter
             *
             */
            itemView.findViewById(R.id.button_del_data).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // check that the listener did not detect a click on an invalid item (out of bounds)
                    if(onNoteListener != null) {
                        int position = getAdapterPosition();            // assign index of item clicked
                        if(position != RecyclerView.NO_POSITION) {
                            onNoteListener.onNoteClickDelete(position); // pass index position to method

                        }
                    }
                }
            });

            /**
             *  Create click listener for the edit button of the individual item in the RecyclerView/Adapter
             */
            itemView.findViewById(R.id.button_edit_data).setOnClickListener(new View.OnClickListener() {
                @Override
                // Check that the listener did not detect a click on an invalid item (out of bounds)
                public void onClick(View view) {
                    if(onNoteListener != null) {
                        int position = getAdapterPosition();            // assign index of item clicked
                        if(position != RecyclerView.NO_POSITION) {
//                            Dialog myDialog = new Dialog(itemView.getContext());
//                            myDialog.setContentView(R.layout.activity_dialog_add);


                            onNoteListener.onNoteClickEdit(position);   // pass index position to method

                        }
                    }
                }
            });
        }
    }

    /**
     * Custom interface for click listener
     */
    public interface OnNoteListener {
        void onNoteClickDelete(int position);       // method for passing delete index position out of class
        void onNoteClickEdit(int position);         // method for passing edit index position out of class
    }
}