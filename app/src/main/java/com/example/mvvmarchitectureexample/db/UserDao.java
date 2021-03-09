package com.example.mvvmarchitectureexample.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mvvmarchitectureexample.model.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Query("SELECT * " +
            "FROM user " +
            "WHERE login = :login")
    LiveData<User> findByLogin(String login);
}
