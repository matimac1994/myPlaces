package com.maciejak.myplaces.repositories;

import com.maciejak.myplaces.model.User;
import com.maciejak.myplaces.model.User_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * Created by Mati on 28.11.2017.
 */

public class UserRepository {

    public User createNewUser(Long id, String username, String token){
        User user = findUserById(id);
        if (user == null)
            user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setToken(token);
        user.save();
        return user;
    }

    public User findUserById(Long id){
        return SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }
}
