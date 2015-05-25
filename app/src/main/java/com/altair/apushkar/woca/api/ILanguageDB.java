package com.altair.apushkar.woca.api;

/**
 * Created by apushkar on 25.05.2015.
 */
public interface ILanguageDB {
    public Integer addLanguage(String lang);
    public void removeLanguage(String lang);
    public void changeLanguage(String lang);
    public Integer getLanguage(String lang);

    public Integer addWord(Integer langID, String word);
    public Integer getWordID(Integer langID, String word);
    public boolean removeWord(Integer wordID);

    public Integer addTranslation(Integer fromID, Integer toID);
    public boolean removeTranslation(Integer translationID);
    public String translateFrom(String from);
    public String translateTo(String to);
}
