package com.example.rendonyahirmyweightapp;

/**
 *  Class for getting and setting RecyclerView items/objects
 */
public class EntryItem {
    private int mId;                    // the id value that matches id in data table
    private String mDate;               // the date string
    private String mWeight;             // the weight string

    /**
     * Constructor for creating object item
     *
     * @param mId       the id value within the data table
     * @param mDate     the date the weight data is collected
     * @param mWeight   the actual weight data
     */
    public EntryItem(int mId, String mDate, String mWeight) {
        this.mId = mId;
        this.mDate = mDate;
        this.mWeight = mWeight;
    }

    /**
     * Method for getting the id value
     *
     * @return      the id value
     */
    public int getId() {
        return mId;
    }

    /**
     * Method for setting the id value
     *
     * @param mId   the id value
     */
    public void setId(int mId) {
        this.mId = mId;
    }

    /**
     * Method for changing the weight TextView in the RecyclerView
     * @param weight
     */
    public void changeWeight(String weight) {
        mWeight = weight;
    }

    /**
     * Get method for getting the date value
     *
     * @return      the date string
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Set method for setting the date value
     *
     * @param mDate     the date
     */
    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    /**
     * Get method for getting the weight value
     *
     * @return      the weight value
     */
    public String getWeight() {
        return mWeight;
    }

    /**
     * Set method for setting the weight value
     *
     * @param mWeight   the weight
     */
    public void setWeight(String mWeight) {
        this.mWeight = mWeight;
    }
}
