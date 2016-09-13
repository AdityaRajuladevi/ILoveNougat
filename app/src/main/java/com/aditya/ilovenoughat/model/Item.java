package com.aditya.ilovenoughat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aditya on 9/8/16.
 */
public class Item implements Parcelable {

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getStyleId() {
        return styleId;
    }

    public void setStyleId(int styleId) {
        this.styleId = styleId;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPercentOff() {
        return percentOff;
    }

    public void setPercentOff(String percentOff) {
        this.percentOff = percentOff;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    private String brandName;
    private String thumbnailImageUrl;
    private int productId;
    private String originalPrice;
    private int styleId;
    private int colorId;
    private String price;
    private String percentOff;
    private String productUrl;
    private String productName;

    public Item()
    {
        // default constructor..
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(brandName);
        out.writeString(thumbnailImageUrl);
        out.writeInt(productId);
        out.writeString(originalPrice);
        out.writeInt(styleId);
        out.writeInt(colorId);
        out.writeString(price);
        out.writeString(percentOff);
        out.writeString(productUrl);
        out.writeString(productName);
    }

    // Parcel Constructor
    private Item(Parcel in) {
        brandName = in.readString();
        thumbnailImageUrl=in.readString();
         productId=in.readInt();
        originalPrice=in.readString();
        styleId=in.readInt();
        colorId=in.readInt();
        price=in.readString();
        percentOff=in.readString();
        productUrl=in.readString();
        productName=in.readString();
    }

    // Creator
    public static final Creator<Item> CREATOR
            = new Creator<Item>()
    {

        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
