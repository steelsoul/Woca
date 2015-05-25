package com.altair.apushkar.woca.imp;

import com.altair.apushkar.woca.api.ILanguageDB;

/**
 * Created by apushkar on 25.05.2015.
 */
class Wocabulary implements ILanguageDB {
    public Wocabulary() {

    }

    @Override
    public Integer addLanguage(String lang) {
        return null;
    }

    @Override
    public void removeLanguage(String lang) {

    }

    @Override
    public void changeLanguage(String lang) {

    }

    @Override
    public Integer getLanguage(String lang) {
        return null;
    }

    @Override
    public Integer addWord(Integer langID, String word) {
        return null;
    }

    @Override
    public Integer getWordID(Integer langID, String word) {
        return null;
    }

    @Override
    public boolean removeWord(Integer wordID) {
        return false;
    }

    @Override
    public Integer addTranslation(Integer fromID, Integer toID) {
        return null;
    }

    @Override
    public boolean removeTranslation(Integer translationID) {
        return false;
    }

    @Override
    public String translateFrom(String from) {
        return null;
    }

    @Override
    public String translateTo(String to) {
        return null;
    }
}
