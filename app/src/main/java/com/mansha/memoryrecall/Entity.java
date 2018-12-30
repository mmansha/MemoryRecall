package com.mansha.memoryrecall;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Entity implements Parcelable {
    public String entityName;
    public String entityImageFile;
    public String entitySoundFile;
    public String entityGuid;
    public String entityStatus;

    public Entity(String entityName){
        this.entityName =  entityName;
    }

    public Entity(String entityName, String entityImage){
        this.entityImageFile = entityImage;
        this.entityName = entityName;
    }

    public Entity(String entityName, String entityImage, String entitySoundFile){
        this.entityImageFile = entityImage;
        this.entityName = entityName;
        this.entitySoundFile = entitySoundFile;
    }

    public Entity(String entityName, String entityImage, String entitySoundFile, String entityGuid){
        this.entityImageFile = entityImage;
        this.entityName = entityName;
        this.entitySoundFile = entitySoundFile;
        this.entityGuid = entityGuid;
    }

    public Entity(String entityName, String entityImage, String entitySoundFile, String entityGuid, String entityStatus){
        this.entityImageFile = entityImage;
        this.entityName = entityName;
        this.entitySoundFile = entitySoundFile;
        this.entityGuid = entityGuid;
        this.entityStatus = entityStatus;
    }


    public Entity(Parcel in){
        this.entityName = in.readString();
        this.entityImageFile = in.readString();
        this.entitySoundFile = in.readString();
        this.entityGuid = in.readString();
        this.entityStatus = in.readString();
    }

    public String getEntityName(){
        return entityName;
    }

    public String getEntityImage(){
        return entityImageFile;
    }

    public String getEntitySoundFile() { return entitySoundFile; }

    public String getEntityGuid(){return entityGuid;}

    public String getEntityStatus(){return entityStatus;}

    public void setEntitySoundFile(String entitySoundFile){
        this.entitySoundFile = entitySoundFile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.entityName);
        dest.writeString(this.entityImageFile);
        dest.writeString(this.entitySoundFile);
        dest.writeString(this.entityGuid);
        dest.writeString(this.entityStatus);

    }

    public static final Parcelable.Creator CREATOR =  new Parcelable.Creator<Entity>(){
        public Entity createFromParcel(Parcel in){
            return new Entity(in);
        }

        public Entity[] newArray(int size){
            return new Entity[size];
        }
    };


}
