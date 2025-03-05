package MyShortcuts.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Interface for MyShortcuts model
 */
public interface MyShortcuts extends Serializable {
    
    /**
     * Returns the primary key of this shortcut
     * @return the primary key
     */
    public long getPrimaryKey();
    
    /**
     * Sets the primary key of this shortcut
     * @param primaryKey the primary key to set
     */
    public void setPrimaryKey(long primaryKey);
    
    /**
     * Returns the link ID of this shortcut
     * @return the link ID
     */
    public long getLinkId();
    
    /**
     * Sets the link ID of this shortcut
     * @param linkId the link ID to set
     */
    public void setLinkId(long linkId);
    
    /**
     * Returns the scope group ID of this shortcut
     * @return the scope group ID
     */
    public long getScopeGroupId();
    
    /**
     * Sets the scope group ID of this shortcut
     * @param scopeGroupId the scope group ID to set
     */
    public void setScopeGroupId(long scopeGroupId);
    
    /**
     * Returns the user ID of this shortcut
     * @return the user ID
     */
    public long getUserId();
    
    /**
     * Sets the user ID of this shortcut
     * @param userId the user ID to set
     */
    public void setUserId(long userId);
    
    /**
     * Returns the link URL of this shortcut
     * @return the link URL
     */
    public String getLinkUrl();
    
    /**
     * Sets the link URL of this shortcut
     * @param linkUrl the link URL to set
     */
    public void setLinkUrl(String linkUrl);
    
    /**
     * Returns the link title of this shortcut
     * @return the link title
     */
    public String getLinkTitle();
    
    /**
     * Sets the link title of this shortcut
     * @param linkTitle the link title to set
     */
    public void setLinkTitle(String linkTitle);
    
    /**
     * Returns the create date of this shortcut
     * @return the create date
     */
    public Date getCreateDate();
    
    /**
     * Sets the create date of this shortcut
     * @param createDate the create date to set
     */
    public void setCreateDate(Date createDate);
    
    /**
     * Returns the modified date of this shortcut
     * @return the modified date
     */
    public Date getModifiedDate();
    
    /**
     * Sets the modified date of this shortcut
     * @param modifiedDate the modified date to set
     */
    public void setModifiedDate(Date modifiedDate);
}