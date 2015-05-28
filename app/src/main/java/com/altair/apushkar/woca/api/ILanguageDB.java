package com.altair.apushkar.woca.api;

import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Modifier;

/**
 * Created by apushkar on 25.05.2015.
 */
public interface ILanguageDB {
    public enum eWord_Type {
        eWT_Unknown(0x00),
        eWT_Noun(0x01),
        eWT_Verb(0x02),
        eWT_Adj(0x04),
        eWT_Adverb(0x08)
        ;

        eWord_Type(int id) {
            this.id = id;
        }

        private int id;

        public int getID() { return this.id; }

        public void add(eWord_Type t) {
            this.id |= t.getID();
        }

        public void remove(eWord_Type t) {
            this.id &= ~t.getID();
        }

        public boolean has(eWord_Type t) {
            return (t.getID() & this.id) == t.getID();
        }

        public static String getClassName() {
            return Modifier.class.getName();
        }
    };


    public long clear();

    public long addLanguage(String lang);
    public void removeLanguage(String lang);
    public void changeLanguage(String lang);
    public Integer getLanguage(String lang);

    public long addWord(Integer langID, eWord_Type wordType, String word);
    public Integer getWordID(Integer langID, String word);
    public boolean removeWord(Integer wordID);

    public Integer addTranslation(Integer fromID, Integer toID);
    public boolean removeTranslation(Integer translationID);
    public String translateFrom(String from);
    public String translateTo(String to);

    public void onCreate(SQLiteDatabase db);
    public void onUpgrade(SQLiteDatabase db);
}
