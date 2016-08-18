package com.example.mare.findgo.database;


import android.provider.BaseColumns;

public class TablesDeclaration {

    public TablesDeclaration() {}

    /* Inner class that defines Friends table contents */
    public static abstract class Friends implements BaseColumns {
        public static final String TABLE_NAME = "Friends";
        public static final String COLUMN_NAME_FRIENDS_ID = "FriendsId";
        public static final String COLUMN_NAME_FRIENDS_IME = "FriendsIme";
        public static final String COLUMN_NAME_FRIENDS_PREZIME = "FriendsPrezime";
        public static final String COLUMN_NAME_FRIENDS_USERNAME = "FriendsUsername";
    }
}
