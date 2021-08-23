package com.mamunsproject.status_onwallpaper_api.Api;

public class UrlModel {

    private String portrait;

    public UrlModel(String portrait) {
        this.portrait = portrait;
    }

    public UrlModel() {
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
