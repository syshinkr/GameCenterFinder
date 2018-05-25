package com.project.admin.gamecenterfinder.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GCListModel {
    @SerializedName("address")
    public String address;

    @SerializedName("city")
    public String city;

    @SerializedName("id")
    public String id;

    @SerializedName("kind")
    public String kind;

    @SerializedName("location")
    public List<Float> location;

    @SerializedName("name")
    public String name;

    @SerializedName("opening")
    public String opening;
}
