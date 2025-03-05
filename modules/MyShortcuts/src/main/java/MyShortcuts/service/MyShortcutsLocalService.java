package MyShortcuts.service;

import MyShortcuts.model.MyShortcuts;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

import java.util.List;

/**
 * Local service interface for MyShortcuts database operations
 */
public interface MyShortcutsLocalService {
    
    /**
     * Adds a new shortcut
     * 
     * @param userId the user ID
     * @param scopeGroupId the scope group ID
     * @param linkUrl the link URL
     * @param linkTitle the link title
     * @return the new shortcut
     * @throws SystemException if a system exception occurred
     * @throws PortalException if a portal exception occurred
     */
    public MyShortcuts addMyShortcuts(long userId, long scopeGroupId, String linkUrl, 
                                      String linkTitle) throws SystemException, PortalException;
                                      
    /**
     * Updates a shortcut
     * 
     * @param linkId the link ID
     * @param linkUrl the link URL
     * @param linkTitle the link title
     * @return the updated shortcut
     * @throws SystemException if a system exception occurred
     * @throws PortalException if a portal exception occurred
     */
    public MyShortcuts updateMyShortcuts(long linkId, String linkUrl, 
                                        String linkTitle) throws SystemException, PortalException;
                                        
    /**
     * Deletes a shortcut
     * 
     * @param linkId the link ID
     * @return the deleted shortcut
     * @throws SystemException if a system exception occurred
     * @throws PortalException if a portal exception occurred
     */
    public MyShortcuts deleteMyShortcuts(long linkId) throws SystemException, PortalException;
    
    /**
     * Returns a shortcut by primary key
     * 
     * @param linkId the link ID
     * @return the shortcut
     * @throws SystemException if a system exception occurred
     * @throws PortalException if a portal exception occurred
     */
    public MyShortcuts getMyShortcuts(long linkId) throws SystemException, PortalException;
    
    /**
     * Returns all shortcuts for a user
     * 
     * @param userId the user ID
     * @return list of shortcuts
     * @throws SystemException if a system exception occurred
     */
    public List<MyShortcuts> getShortcutsByUserId(long userId) throws SystemException;
    
    /**
     * Returns all shortcuts for a scope group
     * 
     * @param scopeGroupId the scope group ID
     * @return list of shortcuts
     * @throws SystemException if a system exception occurred
     */
    public List<MyShortcuts> getShortcutsByScopeGroupId(long scopeGroupId) throws SystemException;
    
    /**
     * Returns all shortcuts for a user and scope group
     * 
     * @param userId the user ID
     * @param scopeGroupId the scope group ID
     * @return list of shortcuts
     * @throws SystemException if a system exception occurred
     */
    public List<MyShortcuts> getShortcutsByG_U(long userId, long scopeGroupId) throws SystemException;
}