package MyShortcuts.model;

import java.io.Serializable;

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
}