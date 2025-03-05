package MyShortcuts.service.impl;

import MyShortcuts.model.MyShortcuts;
import MyShortcuts.model.impl.MyShortcutsImpl;
import MyShortcuts.service.MyShortcutsLocalService;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * Implementation of the MyShortcutsLocalService interface
 * with actual database operations
 */
@Component(
    immediate = true,
    property = {},
    service = MyShortcutsLocalService.class
)
public class MyShortcutsLocalServiceImpl implements MyShortcutsLocalService {
    
    private static final Log _log = LogFactoryUtil.getLog(MyShortcutsLocalServiceImpl.class);
    
    // SQL queries for database operations
    private static final String SQL_INSERT_SHORTCUT = 
        "INSERT INTO Marketplace_MyShortcuts (linkId, scopeGroupId, userId, linkTitle, linkUrl, createDate, modifiedDate) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SQL_UPDATE_SHORTCUT = 
        "UPDATE Marketplace_MyShortcuts SET linkTitle = ?, linkUrl = ?, modifiedDate = ? " +
        "WHERE linkId = ?";
    
    private static final String SQL_DELETE_SHORTCUT = 
        "DELETE FROM Marketplace_MyShortcuts WHERE linkId = ?";
    
    private static final String SQL_GET_SHORTCUT = 
        "SELECT * FROM Marketplace_MyShortcuts WHERE linkId = ?";
    
    private static final String SQL_GET_SHORTCUTS_BY_USER = 
        "SELECT * FROM Marketplace_MyShortcuts WHERE userId = ? ORDER BY linkTitle ASC";
    
    private static final String SQL_GET_SHORTCUTS_BY_GROUP = 
        "SELECT * FROM Marketplace_MyShortcuts WHERE scopeGroupId = ? ORDER BY linkTitle ASC";
    
    private static final String SQL_GET_SHORTCUTS_BY_USER_AND_GROUP = 
        "SELECT * FROM Marketplace_MyShortcuts WHERE userId = ? AND scopeGroupId = ? ORDER BY linkTitle ASC";
    
    @Override
    public MyShortcuts addMyShortcuts(long userId, long scopeGroupId, String linkUrl, String linkTitle) 
            throws SystemException, PortalException {
        
        // Get a new ID for the shortcut
        long linkId = CounterLocalServiceUtil.increment(MyShortcuts.class.getName());
        
        // Create a new shortcut
        MyShortcuts myShortcuts = new MyShortcutsImpl();
        myShortcuts.setLinkId(linkId);
        myShortcuts.setScopeGroupId(scopeGroupId);
        myShortcuts.setUserId(userId);
        myShortcuts.setLinkTitle(linkTitle);
        myShortcuts.setLinkUrl(linkUrl);
        
        Date now = new Date();
        myShortcuts.setCreateDate(now);
        myShortcuts.setModifiedDate(now);
        
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            // Get connection to database
            con = DataAccess.getConnection();
            
            // Prepare statement
            ps = con.prepareStatement(SQL_INSERT_SHORTCUT);
            ps.setLong(1, linkId);
            ps.setLong(2, scopeGroupId);
            ps.setLong(3, userId);
            ps.setString(4, linkTitle);
            ps.setString(5, linkUrl);
            ps.setTimestamp(6, new Timestamp(now.getTime()));
            ps.setTimestamp(7, new Timestamp(now.getTime()));
            
            // Execute insert
            ps.executeUpdate();
            
            _log.info("Added new shortcut: " + linkTitle + " [ID: " + linkId + "]");
        } catch (Exception e) {
            _log.error("Error adding shortcut", e);
            throw new SystemException("Error adding shortcut", e);
        } finally {
            // Close resources
            DataAccess.cleanUp(con, ps);
        }
        
        return myShortcuts;
    }
    
    @Override
    public MyShortcuts updateMyShortcuts(long linkId, String linkUrl, String linkTitle) 
            throws SystemException, PortalException {
        
        // Get the existing shortcut
        MyShortcuts myShortcuts = getMyShortcuts(linkId);
        Date now = new Date();
        
        // Update the shortcut
        myShortcuts.setLinkTitle(linkTitle);
        myShortcuts.setLinkUrl(linkUrl);
        myShortcuts.setModifiedDate(now);
        
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            // Get connection to database
            con = DataAccess.getConnection();
            
            // Prepare statement
            ps = con.prepareStatement(SQL_UPDATE_SHORTCUT);
            ps.setString(1, linkTitle);
            ps.setString(2, linkUrl);
            ps.setTimestamp(3, new Timestamp(now.getTime()));
            ps.setLong(4, linkId);
            
            // Execute update
            ps.executeUpdate();
            
            _log.info("Updated shortcut: " + linkTitle + " [ID: " + linkId + "]");
        } catch (Exception e) {
            _log.error("Error updating shortcut", e);
            throw new SystemException("Error updating shortcut", e);
        } finally {
            // Close resources
            DataAccess.cleanUp(con, ps);
        }
        
        return myShortcuts;
    }
    
    @Override
    public MyShortcuts deleteMyShortcuts(long linkId) throws SystemException, PortalException {
        // Get the existing shortcut
        MyShortcuts myShortcuts = getMyShortcuts(linkId);
        
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            // Get connection to database
            con = DataAccess.getConnection();
            
            // Prepare statement
            ps = con.prepareStatement(SQL_DELETE_SHORTCUT);
            ps.setLong(1, linkId);
            
            // Execute delete
            ps.executeUpdate();
            
            _log.info("Deleted shortcut: " + myShortcuts.getLinkTitle() + " [ID: " + linkId + "]");
        } catch (Exception e) {
            _log.error("Error deleting shortcut", e);
            throw new SystemException("Error deleting shortcut", e);
        } finally {
            // Close resources
            DataAccess.cleanUp(con, ps);
        }
        
        return myShortcuts;
    }
    
    @Override
    public MyShortcuts getMyShortcuts(long linkId) throws SystemException, PortalException {
        MyShortcuts myShortcuts = null;
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // Get connection to database
            con = DataAccess.getConnection();
            
            // Prepare statement
            ps = con.prepareStatement(SQL_GET_SHORTCUT);
            ps.setLong(1, linkId);
            
            // Execute query
            rs = ps.executeQuery();
            
            if (rs.next()) {
                myShortcuts = new MyShortcutsImpl();
                myShortcuts.setLinkId(rs.getLong("linkId"));
                myShortcuts.setScopeGroupId(rs.getLong("scopeGroupId"));
                myShortcuts.setUserId(rs.getLong("userId"));
                myShortcuts.setLinkTitle(rs.getString("linkTitle"));
                myShortcuts.setLinkUrl(rs.getString("linkUrl"));
                myShortcuts.setCreateDate(rs.getTimestamp("createDate"));
                myShortcuts.setModifiedDate(rs.getTimestamp("modifiedDate"));
            }
        } catch (Exception e) {
            _log.error("Error getting shortcut", e);
            throw new SystemException("Error getting shortcut", e);
        } finally {
            // Close resources
            DataAccess.cleanUp(con, ps, rs);
        }
        
        if (myShortcuts == null) {
            throw new PortalException("Shortcut not found with ID: " + linkId);
        }
        
        return myShortcuts;
    }
    
    @Override
    public List<MyShortcuts> getShortcutsByUserId(long userId) throws SystemException {
        List<MyShortcuts> shortcuts = new ArrayList<>();
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // Get connection to database
            con = DataAccess.getConnection();
            
            // Prepare statement
            ps = con.prepareStatement(SQL_GET_SHORTCUTS_BY_USER);
            ps.setLong(1, userId);
            
            // Execute query
            rs = ps.executeQuery();
            
            while (rs.next()) {
                MyShortcuts myShortcuts = new MyShortcutsImpl();
                myShortcuts.setLinkId(rs.getLong("linkId"));
                myShortcuts.setScopeGroupId(rs.getLong("scopeGroupId"));
                myShortcuts.setUserId(rs.getLong("userId"));
                myShortcuts.setLinkTitle(rs.getString("linkTitle"));
                myShortcuts.setLinkUrl(rs.getString("linkUrl"));
                myShortcuts.setCreateDate(rs.getTimestamp("createDate"));
                myShortcuts.setModifiedDate(rs.getTimestamp("modifiedDate"));
                shortcuts.add(myShortcuts);
            }
        } catch (Exception e) {
            _log.error("Error getting shortcuts by user", e);
            throw new SystemException("Error getting shortcuts by user", e);
        } finally {
            // Close resources
            DataAccess.cleanUp(con, ps, rs);
        }
        
        return shortcuts;
    }
    
    @Override
    public List<MyShortcuts> getShortcutsByScopeGroupId(long scopeGroupId) throws SystemException {
        List<MyShortcuts> shortcuts = new ArrayList<>();
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // Get connection to database
            con = DataAccess.getConnection();
            
            // Prepare statement
            ps = con.prepareStatement(SQL_GET_SHORTCUTS_BY_GROUP);
            ps.setLong(1, scopeGroupId);
            
            // Execute query
            rs = ps.executeQuery();
            
            while (rs.next()) {
                MyShortcuts myShortcuts = new MyShortcutsImpl();
                myShortcuts.setLinkId(rs.getLong("linkId"));
                myShortcuts.setScopeGroupId(rs.getLong("scopeGroupId"));
                myShortcuts.setUserId(rs.getLong("userId"));
                myShortcuts.setLinkTitle(rs.getString("linkTitle"));
                myShortcuts.setLinkUrl(rs.getString("linkUrl"));
                myShortcuts.setCreateDate(rs.getTimestamp("createDate"));
                myShortcuts.setModifiedDate(rs.getTimestamp("modifiedDate"));
                shortcuts.add(myShortcuts);
            }
        } catch (Exception e) {
            _log.error("Error getting shortcuts by scope group", e);
            throw new SystemException("Error getting shortcuts by scope group", e);
        } finally {
            // Close resources
            DataAccess.cleanUp(con, ps, rs);
        }
        
        return shortcuts;
    }
    
    @Override
    public List<MyShortcuts> getShortcutsByG_U(long userId, long scopeGroupId) throws SystemException {
        List<MyShortcuts> shortcuts = new ArrayList<>();
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // Get connection to database
            con = DataAccess.getConnection();
            
            // Prepare statement
            ps = con.prepareStatement(SQL_GET_SHORTCUTS_BY_USER_AND_GROUP);
            ps.setLong(1, userId);
            ps.setLong(2, scopeGroupId);
            
            // Execute query
            rs = ps.executeQuery();
            
            while (rs.next()) {
                MyShortcuts myShortcuts = new MyShortcutsImpl();
                myShortcuts.setLinkId(rs.getLong("linkId"));
                myShortcuts.setScopeGroupId(rs.getLong("scopeGroupId"));
                myShortcuts.setUserId(rs.getLong("userId"));
                myShortcuts.setLinkTitle(rs.getString("linkTitle"));
                myShortcuts.setLinkUrl(rs.getString("linkUrl"));
                myShortcuts.setCreateDate(rs.getTimestamp("createDate"));
                myShortcuts.setModifiedDate(rs.getTimestamp("modifiedDate"));
                shortcuts.add(myShortcuts);
            }
        } catch (Exception e) {
            _log.error("Error getting shortcuts by user and scope group", e);
            throw new SystemException("Error getting shortcuts by user and scope group", e);
        } finally {
            // Close resources
            DataAccess.cleanUp(con, ps, rs);
        }
        
        return shortcuts;
    }
}