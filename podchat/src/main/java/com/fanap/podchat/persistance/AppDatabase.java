package com.fanap.podchat.persistance;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.fanap.podchat.mainmodel.Contact;
import com.fanap.podchat.mainmodel.Inviter;
import com.fanap.podchat.mainmodel.LastMessageVO;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.ThreadVo;
import com.fanap.podchat.model.FileMetaDataContent;
import com.fanap.podchat.model.ForwardInfo;
import com.fanap.podchat.model.MessageVO;
import com.fanap.podchat.model.ReplyInfoVO;
import com.fanap.podchat.persistance.dao.MessageDao;

@Database(entities = {Contact.class,ThreadVo.class, LastMessageVO.class, ReplyInfoVO.class,
        Inviter.class, Participant.class, ForwardInfo.class,MessageVO.class, FileMetaDataContent.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_DB = "cache.db";
    private static AppDatabase appDatabase;

    public static AppDatabase getInstance(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, DATABASE_DB).allowMainThreadQueries().build();
        }
        return appDatabase;
    }

    public abstract MessageDao getMessageDao();
}
