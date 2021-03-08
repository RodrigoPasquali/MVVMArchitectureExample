package com.example.mvvmarchitectureexample.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.google.gson.annotations.SerializedName;
//CASCADE propaga a las actualizaciones tanto a clases padres como hijas
@Entity(primaryKeys = {"repoName", "reponOwner", "login"},
        foreignKeys = {@ForeignKey(entity = Repo.class,
        parentColumns = {"name", "owner_login"},
        childColumns = {"repoName", "repoOwner"},
        onUpdate = ForeignKey.CASCADE)})
public class Contributor {
    @SerializedName("login")
    private String login;
    @SerializedName("contributions")
    private String contributions;
    @SerializedName("avatar_url")
    private String avatarUrl;

    private String repoName;
    private String repoOwner;

    public Contributor(String login, String contributions, String avatarUrl) {
        this.login = login;
        this.contributions = contributions;
        this.avatarUrl = avatarUrl;
    }
}
