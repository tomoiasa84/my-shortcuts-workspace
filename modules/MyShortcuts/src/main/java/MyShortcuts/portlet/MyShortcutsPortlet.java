package MyShortcuts.portlet;

import MyShortcuts.constants.MyShortcutsPortletKeys;
import MyShortcuts.model.MyShortcuts;
import MyShortcuts.model.impl.MyShortcutsImpl;
import MyShortcuts.service.MyShortcutsLocalService;

import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author alin
 */
@Component(
    property = {
        "com.liferay.portlet.display-category=category.sample",
        "com.liferay.portlet.header-portlet-css=/css/main.css",
        "com.liferay.portlet.instanceable=true",
        "javax.portlet.display-name=MyShortcuts",
        "javax.portlet.init-param.template-path=/",
        "javax.portlet.init-param.view-template=/html/my-shortcuts/view.jsp",
        "javax.portlet.name=" + MyShortcutsPortletKeys.MYSHORTCUTS,
        "javax.portlet.resource-bundle=content.Language",
        "javax.portlet.security-role-ref=power-user,user"
    },
    service = Portlet.class
)
public class MyShortcutsPortlet extends MVCPortlet {
    
    private static final Log _log = LogFactoryUtil.getLog(MyShortcutsPortlet.class);
    
    protected String addNewShortcutJSP = "/html/my-shortcuts/edit_shortcut.jsp";
    protected String viewShortcutJSP = "/html/my-shortcuts/view.jsp";
    
    // Flag to track whether we've tried to add sample data
    private boolean sampleDataChecked = false;
    
    private static final String TABLE_NAME = "Marketplace_MyShortcuts";
    private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private static final String CHECK_TABLE_EXISTS = "SELECT COUNT(*) FROM " + TABLE_NAME;
    private static final String COUNT_USER_SHORTCUTS = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE userId = ?";
    private static final String CREATE_TABLE_SQL = 
            "CREATE TABLE " + TABLE_NAME + " (" +
            "linkId BIGINT NOT NULL PRIMARY KEY, " +
            "scopeGroupId BIGINT, " +
            "userId BIGINT, " +
            "linkTitle VARCHAR(75) NULL, " +
            "linkUrl VARCHAR(75) NULL, " +
            "createDate DATETIME NULL, " +
            "modifiedDate DATETIME NULL)";
    
    @Reference
    private volatile MyShortcutsLocalService myShortcutsLocalService;
    
    @Activate
    protected void activate() {
        _log.info("MyShortcutsPortlet activated. Checking table existence...");
        checkTableAndInitialize();
    }
    
    /**
     * Checks if the shortcuts table exists and creates it if it doesn't.
     */
    protected void checkTableAndInitialize() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean tableExists = false;
        
        try {
            // Get connection to database
            con = DataAccess.getConnection();
            
            // First try to query the table to see if it exists and is accessible
            try {
                ps = con.prepareStatement(CHECK_TABLE_EXISTS);
                rs = ps.executeQuery();
                // If we get here without exception, the table exists
                _log.info("Table " + TABLE_NAME + " exists and is accessible.");
                tableExists = true;
                DataAccess.cleanUp(null, ps, rs);
            } catch (Exception e) {
                // Table likely doesn't exist or has issues
                _log.info("Table " + TABLE_NAME + " doesn't exist or has issues: " + e.getMessage());
                tableExists = false;
                DataAccess.cleanUp(null, ps, rs);
            }
            
            if (!tableExists) {
                // Table doesn't exist or has issues, create/recreate it
                try {
                    // Try to drop the table first in case it exists but has issues
                    ps = con.prepareStatement(DROP_TABLE_SQL);
                    ps.executeUpdate();
                    DataAccess.cleanUp(null, ps, null);
                    _log.info("Dropped existing table if it existed.");
                } catch (Exception e) {
                    // Ignore errors here as the table might not exist
                    DataAccess.cleanUp(null, ps, null);
                }
                
                // Create the table
                ps = con.prepareStatement(CREATE_TABLE_SQL);
                ps.executeUpdate();
                _log.info("Table " + TABLE_NAME + " created successfully.");
            }
            
        } catch (Exception e) {
            _log.error("Error checking/creating table: " + e.getMessage(), e);
        } finally {
            DataAccess.cleanUp(con, ps, rs);
        }
    }
    
    /**
     * Adds sample shortcuts for a user if they don't have any
     */
    protected void addSampleShortcutsIfEmpty(long userId, long scopeGroupId) {
        if (sampleDataChecked) {
            return; // Already checked/added sample data for this portlet instance
        }
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean hasShortcuts = false;
        
        try {
            // Get connection to database
            con = DataAccess.getConnection();
            
            // Check if user has any shortcuts
            ps = con.prepareStatement(COUNT_USER_SHORTCUTS);
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                hasShortcuts = (rs.getInt(1) > 0);
            }
            
            if (!hasShortcuts) {
                _log.info("User " + userId + " has no shortcuts. Adding sample data.");
                
                // Explicitly add sample shortcuts
                try {
                    // Add Google
                    myShortcutsLocalService.addMyShortcuts(userId, scopeGroupId, 
                            "https://www.google.com", "Google Search");
                    _log.info("Added Google shortcut for user " + userId);
                    
                    // Add GitHub
                    myShortcutsLocalService.addMyShortcuts(userId, scopeGroupId, 
                            "https://www.github.com", "GitHub");
                    _log.info("Added GitHub shortcut for user " + userId);
                    
                    // Add Liferay
                    myShortcutsLocalService.addMyShortcuts(userId, scopeGroupId, 
                            "https://www.liferay.com", "Liferay");
                    _log.info("Added Liferay shortcut for user " + userId);
                    
                    // Add Stack Overflow
                    myShortcutsLocalService.addMyShortcuts(userId, scopeGroupId, 
                            "https://stackoverflow.com", "Stack Overflow");
                    _log.info("Added Stack Overflow shortcut for user " + userId);
                    
                    // Add YouTube
                    myShortcutsLocalService.addMyShortcuts(userId, scopeGroupId, 
                            "https://www.youtube.com", "YouTube");
                    _log.info("Added YouTube shortcut for user " + userId);
                    
                    _log.info("ALL Sample shortcuts added for user " + userId);
                } catch (Exception e) {
                    _log.error("Error adding sample shortcuts: " + e.getMessage(), e);
                }
            } else {
                _log.info("User " + userId + " already has " + rs.getInt(1) + " shortcuts.");
            }
        } catch (Exception e) {
            _log.error("Error checking user shortcuts: " + e.getMessage(), e);
        } finally {
            DataAccess.cleanUp(con, ps, rs);
            sampleDataChecked = true; // Mark as checked regardless of outcome
        }
    }
    
   
    @Override
    public void render(RenderRequest renderRequest, RenderResponse renderResponse) 
            throws PortletException, IOException {
        
        ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
        
        try {
            // Check if user has shortcuts and add sample data if necessary
            if (themeDisplay != null && themeDisplay.isSignedIn()) {
                addSampleShortcutsIfEmpty(themeDisplay.getUserId(), themeDisplay.getScopeGroupId());
            }
            
            // Get shortcuts for the current user and scope group
            List<MyShortcuts> shortcutsList = myShortcutsLocalService.getShortcutsByG_U(
                themeDisplay.getUserId(), themeDisplay.getScopeGroupId());
            
            // Make the list empty button available if no shortcuts exist
            renderRequest.setAttribute("hasShortcuts", !shortcutsList.isEmpty());
            renderRequest.setAttribute("shortcutsList", shortcutsList);
            _log.debug("Retrieved " + shortcutsList.size() + " shortcuts for user " + themeDisplay.getUserId());
        } catch (SystemException e) {
            // Log error and provide empty list in case of failure
            _log.error("Error retrieving shortcuts: " + e.getMessage(), e);
            renderRequest.setAttribute("shortcutsList", new ArrayList<MyShortcuts>());
            renderRequest.setAttribute("hasShortcuts", false);
        }
        
        super.render(renderRequest, renderResponse);
    }
    
    /**
     * Navigate to add shortcut page
     */
    public void addNewShortcut(ActionRequest actionRequest, ActionResponse actionResponse) {
        MyShortcuts myLink = new MyShortcutsImpl();
        actionRequest.setAttribute("myLink", myLink); 
        actionResponse.setRenderParameter("jspPage", addNewShortcutJSP);
        _log.debug("Navigating to add new shortcut page");
    }
    
    /**
     * Save a new or updated shortcut
     */
    public void saveNewShortcut(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
        ThemeDisplay themeDisplay =
            (ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
        
        String linkTitle = ParamUtil.getString(actionRequest, "link");
        String linkUrl = ParamUtil.getString(actionRequest, "url");
        
        long linkId = ParamUtil.getLong(actionRequest, "linkId");
        long groupId = themeDisplay.getScopeGroupId();
        long userId = themeDisplay.getUserId();
        
        _log.debug("Saving shortcut - Title: " + linkTitle + ", URL: " + linkUrl + ", ID: " + linkId);
        
        ArrayList<String> errors = new ArrayList<String>();
            
        if (validateLinks(linkUrl, linkTitle, errors)) {
            
            if (Validator.isNull(linkId) || linkId == 0) {
                // Add new shortcut
                linkUrl = checkURL(linkUrl);
                myShortcutsLocalService.addMyShortcuts(userId, groupId, linkUrl, linkTitle);
                actionRequest.setAttribute("link-saved-successfully", "link-saved-successfully");
                _log.info("New shortcut added: " + linkTitle);
            } else {
                // Update existing shortcut
                linkUrl = checkURL(linkUrl);
                myShortcutsLocalService.updateMyShortcuts(linkId, linkUrl, linkTitle);
                actionRequest.setAttribute("link-updated-successfully", "link-updated-successfully");
                _log.info("Shortcut updated: " + linkTitle + " [ID: " + linkId + "]");
            }
            
            actionResponse.sendRedirect(PortalUtil.getLayoutURL(themeDisplay));
        } else {
            // Validation failed
            MyShortcuts myLink = new MyShortcutsImpl();
            myLink.setLinkTitle(linkTitle);
            myLink.setLinkUrl(linkUrl);
            actionRequest.setAttribute("myLink", myLink); 
            
            for (String error : errors) {
                SessionErrors.add(actionRequest, error);
                _log.debug("Validation error: " + error);
            }
            actionResponse.setRenderParameter("jspPage", addNewShortcutJSP);
        }
    }
    
    /**
     * Delete a shortcut
     */
    public void deleteShortcut(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
        String linkIdStr = ParamUtil.getString(actionRequest, "linkId");
        ThemeDisplay themeDisplay =
            (ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
        
        _log.debug("Deleting shortcut with ID: " + linkIdStr);
        
        if (Validator.isNotNull(linkIdStr)) {
            try {
                long linkId = Long.parseLong(linkIdStr);
                myShortcutsLocalService.deleteMyShortcuts(linkId);
                actionRequest.setAttribute("link-deleted-successfully", "link-deleted-successfully");
                _log.info("Shortcut deleted: " + linkId);
            } catch (Exception ex) {
                actionRequest.setAttribute("link-not-deleted-successfully", "link-not-deleted-successfully");
                _log.error("Error deleting shortcut: " + ex.getMessage(), ex);
            }
            
            actionResponse.sendRedirect(PortalUtil.getLayoutURL(themeDisplay));
        } else {
            actionRequest.setAttribute("link-not-deleted-successfully", "link-not-deleted-successfully"); 
            actionResponse.sendRedirect(PortalUtil.getLayoutURL(themeDisplay));
        }
    }
    
    /**
     * Edit an existing shortcut
     */
    public void editShortcut(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
        long linkId = ParamUtil.getLong(actionRequest, "linkId");
        
        _log.debug("Editing shortcut with ID: " + linkId);
        
        if (Validator.isNotNull(linkId)) { 
            try {
                MyShortcuts myLink = myShortcutsLocalService.getMyShortcuts(linkId);
                actionRequest.setAttribute("myLink", myLink);
                actionResponse.setRenderParameter("jspPage", addNewShortcutJSP);
                _log.debug("Retrieved shortcut for editing: " + myLink.getLinkTitle());
            } catch (PortalException e) {
                _log.error("Error retrieving shortcut with ID " + linkId + ": " + e.getMessage(), e);
                actionResponse.setRenderParameter("jspPage", viewShortcutJSP);
            }
        }
    }
    
    /**
     * Validate links
     */
    private boolean validateLinks(String url, String title, List<String> errors) {
        boolean isValid = true;
        
        if (Validator.isNull(url)) {
            errors.add("url-required");
            isValid = false;
        } else if (!isValidUrl(url)) {
            errors.add("url-invalid");
            isValid = false;
        }
        
        if (Validator.isNull(title)) {
            errors.add("title-required");
            isValid = false;
        }
        
        return isValid;
    }
    
    private String checkURL(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url;
        }
        return url;
    }
    
    private boolean isValidUrl(String url) {
        // Basic URL validation
        return url.contains(".");
    }
}