package com.example.mvvmarchitectureexample.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.google.gson.annotations.SerializedName;
//CASCADE propaga a las actualizaciones tanto a clases padres como hijas
@Entity(primaryKeys = {"repoName", "repoOwner", "login"},
        foreignKeys = {@ForeignKey(entity = Repo.class,
        parentColumns = {"name", "owner_login"},
        childColumns = {"repoName", "repoOwner"},
        onUpdate = ForeignKey.CASCADE)})
public class Contributor {
    @SerializedName("login")
    @NonNull
    private String login;
    @SerializedName("contributions")
    private String contributions;
    @SerializedName("avatar_url")
    private String avatarUrl;

    @NonNull
    private String repoName;
    @NonNull
    private String repoOwner;

    public Contributor(String login, String contributions, String avatarUrl) {
        this.login = login;
        this.contributions = contributions;
        this.avatarUrl = avatarUrl;
    }

    public String getLogin() {
        return login;
    }

    public String getContributions() {
        return contributions;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getRepoName() {
        return this.repoName;
    }

    public String getRepoOwner() {
        return this.repoOwner;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public void setRepoOwner(String repoOwner) {
        this.repoOwner = repoOwner;
    }
}
