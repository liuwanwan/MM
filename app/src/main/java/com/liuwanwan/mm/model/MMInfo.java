package com.liuwanwan.mm.model;

import org.litepal.crud.DataSupport;

public class MMInfo extends DataSupport{
    private String imageTitle;
    private String imageUrl;
	private int collectState;
    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
	
	public int getCollectState() {
        return collectState;
    }

    public void setCollectState(int collectState) {
        this.collectState = collectState;
    }

}
