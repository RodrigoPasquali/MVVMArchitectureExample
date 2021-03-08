package com.example.mvvmarchitectureexample.model;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;
//indices --> se utilizan para realizar consultas de seleccion mas rapidas, en contra de las de
//            insercion y actualizacion
@Entity(indices = {@Index("id"), @Index("owner_login")},
        primaryKeys = {"name", "owner_login"})
public class Repo {
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("description")
    private String description;
    @SerializedName("login")
    private String login;
    @SerializedName("stargazer_count")
    private String startCount;
    @SerializedName("owner")
    //Para tomar la clase interna owner en columnas para la tabla general
    @Embedded(prefix = "owner_")
    private Owner owner;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getStartCount() {
        return startCount;
    }

    public void setStartCount(String startCount) {
        this.startCount = startCount;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public static class Owner {
        @SerializedName("login")
        private String login;

        @SerializedName("url")
        private String url;

        public Owner(String login, String url) {
            this.login = login;
            this.url = url;
        }
    }
}
