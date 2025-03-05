package MyShortcuts.model.impl;

import MyShortcuts.model.MyShortcuts;

/**
 * Implementation of the MyShortcuts interface
 */
public class MyShortcutsImpl implements MyShortcuts {
    
    private static final long serialVersionUID = 1L;
    
    private long _primaryKey;
    private String _linkUrl;
    private String _linkTitle;
    
    public MyShortcutsImpl() {
        _primaryKey = 0;
        _linkUrl = "";
        _linkTitle = "";
    }
    
    @Override
    public long getPrimaryKey() {
        return _primaryKey;
    }
    
    @Override
    public void setPrimaryKey(long primaryKey) {
        _primaryKey = primaryKey;
    }
    
    @Override
    public String getLinkUrl() {
        return _linkUrl;
    }
    
    @Override
    public void setLinkUrl(String linkUrl) {
        _linkUrl = linkUrl;
    }
    
    @Override
    public String getLinkTitle() {
        return _linkTitle;
    }
    
    @Override
    public void setLinkTitle(String linkTitle) {
        _linkTitle = linkTitle;
    }
}