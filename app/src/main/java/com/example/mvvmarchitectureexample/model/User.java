package com.example.mvvmarchitectureexample.model;

import com.google.gson.annotations.SerializedName;

public class User {
    //Para Retrofit-JsonConverter se usa el SerializedName
    @SerializedName("login")
    private String login;
    @SerializedName("avatarUrl")
    private String avatarUrl;
    @SerializedName("name")
    private String name;
    @SerializedName("company")
    private String company;
    @SerializedName("repoUrl")
    private String reposUrl;
    @SerializedName("blog")
    private String blog;

    public User(String login, String avatarUrl, String name, String company, String reposUrl,
                String blog) {
        this.login = login;
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.company = company;
        this.reposUrl = reposUrl;
        this.blog = blog;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompany() {
        return company;
    }

    public void setReposUrl(String reposUrl) {
        this.reposUrl = reposUrl;
    }

    public String getReposUrl() {
        return reposUrl;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getBlog() {
        return blog;
    }
}
