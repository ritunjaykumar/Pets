package com.softgyan.pets.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PetsModels implements Parcelable {
    private long id;
    private String name;
    private String bread;
    private int gender;
    private int weight;

    public PetsModels() {
    }

    public PetsModels(String name, String bread, int gender, int weight) {
        this.name = name;
        this.bread = bread;
        this.gender = gender;
        this.weight = weight;
    }

    protected PetsModels(Parcel in) {
        id = in.readLong();
        name = in.readString();
        bread = in.readString();
        gender = in.readInt();
        weight = in.readInt();
    }

    public static final Creator<PetsModels> CREATOR = new Creator<PetsModels>() {
        @Override
        public PetsModels createFromParcel(Parcel in) {
            return new PetsModels(in);
        }

        @Override
        public PetsModels[] newArray(int size) {
            return new PetsModels[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBread() {
        return bread;
    }

    public void setBread(String bread) {
        this.bread = bread;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "PetsModels{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", bread='" + bread + '\'' +
                ", gender=" + gender +
                ", weight=" + weight +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(bread);
        dest.writeInt(gender);
        dest.writeInt(weight);
    }
}
