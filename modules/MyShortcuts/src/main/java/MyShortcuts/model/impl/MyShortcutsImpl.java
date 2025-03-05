package MyShortcuts.model.impl;

import MyShortcuts.model.MyShortcuts;

import java.util.Date;

/**
 * Implementation of the MyShortcuts interface
 */
public class MyShortcutsImpl implements MyShortcuts {
    
    private static final long serialVersionUID = 1L;
    
    private long _linkId;
    private long _scopeGroupId;
    private long _userId;
    private String _linkTitle;
    private String _linkUrl;
    private Date _createDate;
    private Date _modifiedDate;
    
    public MyShortcutsImpl() {
        _linkId = 0;
        _scopeGroupId = 0;
        _userId = 0;
        _linkTitle = "";
        _linkUrl = "";
        _createDate = new Date();
        _modifiedDate = new Date();
    }
    
    @Override
    public long getPrimaryKey() {
        return _linkId;
    }
    
    @Override
    public void setPrimaryKey(long primaryKey) {
        _linkId = primaryKey;
    }
    
    @Override
    public long getLinkId() {
        return _linkId;
    }
    
    @Override
    public void setLinkId(long linkId) {
        _linkId = linkId;
    }
    
    @Override
    public long getScopeGroupId() {
        return _scopeGroupId;
    }
    
    @Override
    public void setScopeGroupId(long scopeGroupId) {
        _scopeGroupId = scopeGroupId;
    }
    
    @Override
    public long getUserId() {
        return _userId;
    }
    
    @Override
    public void setUserId(long userId) {
        _userId = userId;
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
    
    @Override
    public Date getCreateDate() {
        return _createDate;
    }
    
    @Override
    public void setCreateDate(Date createDate) {
        _createDate = createDate;
    }
    
    @Override
    public Date getModifiedDate() {
        return _modifiedDate;
    }
    
    @Override
    public void setModifiedDate(Date modifiedDate) {
        _modifiedDate = modifiedDate;
    }
}