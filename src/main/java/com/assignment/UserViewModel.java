package com.assignment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private UserRepository repository;
    private LiveData<List<User>> users;

    public UserViewModel(@NonNull Application application) {
        super(application);
        UserDao userDao = UserDatabase.getDatabase(application).userDao();
        repository = new UserRepository(RetrofitInstance.getApiService(), userDao);
      users = repository.getUsers();
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public void insertUser(UserEntity user) {
        repository.insertUser(user);
    }

    public LiveData<List<UserEntity>> getAllUsers() {

        return repository.getAllUsers();
    }
}