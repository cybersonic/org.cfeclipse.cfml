package org.cfeclipse.cfml.views.dictionary;

import org.cfeclipse.cfml.dictionary.Tag;

public class CTagItem extends DictionaryItem {
    
    
    /**
     * @param name
     */
    public CTagItem(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }
    
    public String getHelp(){
    
        return super.getDictionary().getTag(super.getName()).getHelp();
    }
   
    public Tag getTag(){
    	return super.getDictionary().getTag(super.getName());
    }
}