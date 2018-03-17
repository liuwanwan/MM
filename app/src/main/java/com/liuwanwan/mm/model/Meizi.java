package com.liuwanwan.mm.model;

public class Meizi {
    private String titleOfMeizi;
    private String urlOfMeizi;
    private String detailUrlOfMeizi;

    public Meizi(String titleOfMeizi, String urlOfMeizi, String detailUrlOfMeizi)
    {
        this.titleOfMeizi = titleOfMeizi;
        this.urlOfMeizi = urlOfMeizi;
        this.detailUrlOfMeizi = detailUrlOfMeizi;
    }
    public String getTitleOfMeizi()
    {
        return titleOfMeizi;
    }
    public String getUrlOfMeizi()
    {
        return urlOfMeizi;
    }
    public String getDetailUrlOfMeizi()
    {
        return detailUrlOfMeizi;
    }
}