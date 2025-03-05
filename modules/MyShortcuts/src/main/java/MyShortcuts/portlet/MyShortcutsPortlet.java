package MyShortcuts.portlet;

import MyShortcuts.constants.MyShortcutsPortletKeys;
import MyShortcuts.model.MyShortcuts;
import MyShortcuts.model.impl.MyShortcutsImpl;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

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
    
    protected String addNewShortcutJSP = "/html/my-shortcuts/edit_shortcut.jsp";
    protected String viewShortcutJSP = "/html/my-shortcuts/view.jsp";
    
    // Constants for session attributes
    private static final String SHORTCUTS_MAP_KEY = "MY_SHORTCUTS_MAP";
    private static final String SHORTCUTS_ID_COUNTER_KEY = "MY_SHORTCUTS_ID_COUNTER";
    
    /**
     * Initialize or get the current shortcuts map from the session
     */
    private Map<Long, MyShortcuts> getShortcutsMap(PortletSession session) {
        @SuppressWarnings("unchecked")
        Map<Long, MyShortcuts> shortcutsMap = (Map<Long, MyShortcuts>) session.getAttribute(
            SHORTCUTS_MAP_KEY, PortletSession.APPLICATION_SCOPE);
        
        if (shortcutsMap == null) {
            // Create and populate with mock data if not exists
            shortcutsMap = new ConcurrentHashMap<>();
            
            // Add mock data
            addMockShortcut(shortcutsMap, 1L, "https://www.google.com", "Google Search");
            addMockShortcut(shortcutsMap, 2L, "https://www.github.com", "GitHub");
            addMockShortcut(shortcutsMap, 3L, "https://www.stackoverflow.com", "Stack Overflow");
            addMockShortcut(shortcutsMap, 4L, "https://www.liferay.com", "Liferay Portal");
            addMockShortcut(shortcutsMap, 5L, "https://www.osgi.org", "OSGi Alliance");
            
            // Store in session
            session.setAttribute(SHORTCUTS_MAP_KEY, shortcutsMap, PortletSession.APPLICATION_SCOPE);
            session.setAttribute(SHORTCUTS_ID_COUNTER_KEY, new AtomicLong(6L), PortletSession.APPLICATION_SCOPE);
        }
        
        return shortcutsMap;
    }
    
    /**
     * Get the ID counter for generating new IDs
     */
    private AtomicLong getIdCounter(PortletSession session) {
        AtomicLong counter = (AtomicLong) session.getAttribute(
            SHORTCUTS_ID_COUNTER_KEY, PortletSession.APPLICATION_SCOPE);
        
        if (counter == null) {
            counter = new AtomicLong(1L);
            session.setAttribute(SHORTCUTS_ID_COUNTER_KEY, counter, PortletSession.APPLICATION_SCOPE);
        }
        
        return counter;
    }
    
    /**
     * Helper method to add a mock shortcut
     */
    private void addMockShortcut(Map<Long, MyShortcuts> map, Long id, String url, String title) {
        MyShortcuts shortcut = new MyShortcutsImpl();
        shortcut.setPrimaryKey(id);
        shortcut.setLinkUrl(url);
        shortcut.setLinkTitle(title);
        map.put(id, shortcut);
    }
    
    @Override
    public void render(RenderRequest renderRequest, RenderResponse renderResponse) 
            throws PortletException, IOException {
        
        Map<Long, MyShortcuts> shortcutsMap = getShortcutsMap(renderRequest.getPortletSession());
        
        // Convert map to list for easier rendering in JSP
        List<MyShortcuts> shortcutsList = new ArrayList<>(shortcutsMap.values());
        renderRequest.setAttribute("shortcutsList", shortcutsList);
        
        super.render(renderRequest, renderResponse);
    }
    
    /**
     * Navigate to add shortcut page
     */
    public void addNewShortcut(ActionRequest actionRequest, ActionResponse actionResponse) {
        MyShortcuts myLink = new MyShortcutsImpl();
        actionRequest.setAttribute("myLink", myLink); 
        actionResponse.setRenderParameter("jspPage", addNewShortcutJSP);
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
        
        // Debug information
        System.out.println("saveNewShortcut called with:");
        System.out.println("  linkId: " + linkId);
        System.out.println("  linkTitle: " + linkTitle);
        System.out.println("  linkUrl: " + linkUrl);
        
        ArrayList<String> errors = new ArrayList<String>();
            
        if (validateLinks(linkUrl, linkTitle, errors)) {
            PortletSession session = actionRequest.getPortletSession();
            Map<Long, MyShortcuts> shortcutsMap = getShortcutsMap(session);
            
            if (Validator.isNull(linkId) || linkId == 0) {
                // Add new shortcut
                linkUrl = checkURL(linkUrl);
                
                // Generate new ID
                AtomicLong counter = getIdCounter(session);
                long newId = counter.getAndIncrement();
                
                MyShortcuts newShortcut = new MyShortcutsImpl();
                newShortcut.setPrimaryKey(newId);
                newShortcut.setLinkUrl(linkUrl);
                newShortcut.setLinkTitle(linkTitle);
                
                // Add to map
                shortcutsMap.put(newId, newShortcut);
                
                actionRequest.setAttribute("link-saved-successfully", "link-saved-successfully");
                System.out.println("New shortcut created with ID: " + newId);
            } else {
                // Update existing shortcut
                MyShortcuts existingShortcut = shortcutsMap.get(linkId);
                
                if (existingShortcut != null) {
                    linkUrl = checkURL(linkUrl);
                    existingShortcut.setLinkUrl(linkUrl);
                    existingShortcut.setLinkTitle(linkTitle);
                    
                    // Update in map
                    shortcutsMap.put(linkId, existingShortcut);
                    
                    actionRequest.setAttribute("link-updated-successfully", "link-updated-successfully");
                    System.out.println("Shortcut updated with ID: " + linkId);
                } else {
                    System.out.println("Failed to find shortcut with ID: " + linkId);
                }
            }
            
            // Explicitly set the render parameter to go back to the view page
            actionResponse.setRenderParameter("jspPage", "/html/my-shortcuts/view.jsp");
            
            // Only try to redirect if we have ThemeDisplay
            if (themeDisplay != null) {
                try {
                    actionResponse.sendRedirect(PortalUtil.getLayoutURL(themeDisplay));
                } catch (Exception e) {
                    System.out.println("Failed to redirect: " + e.getMessage());
                    // Fallback to render parameter if redirect fails
                    actionResponse.setRenderParameter("jspPage", "/html/my-shortcuts/view.jsp");
                }
            } else {
                System.out.println("ThemeDisplay is null, using render parameter instead");
                actionResponse.setRenderParameter("jspPage", "/html/my-shortcuts/view.jsp");
            }
        } else {
            // Validation failed
            MyShortcuts myLink = new MyShortcutsImpl();
            myLink.setLinkTitle(linkTitle);
            myLink.setLinkUrl(linkUrl);
            actionRequest.setAttribute("myLink", myLink); 
            
            for (String error : errors) {
                SessionErrors.add(actionRequest, error);
            }
            actionResponse.setRenderParameter("jspPage", "/html/my-shortcuts/edit_shortcut.jsp");
            System.out.println("Validation failed, staying on edit page");
        }
    }
    
    /**
     * Delete a shortcut
     */
    public void deleteShortcut(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
        String linkIdStr = ParamUtil.getString(actionRequest, "linkId");
        ThemeDisplay themeDisplay =
            (ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
        
        if (Validator.isNotNull(linkIdStr)) {
            try {
                long linkId = Long.parseLong(linkIdStr);
                
                // Remove from map
                Map<Long, MyShortcuts> shortcutsMap = getShortcutsMap(actionRequest.getPortletSession());
                shortcutsMap.remove(linkId);
                
                actionRequest.setAttribute("link-deleted-successfully", "link-deleted-successfully");
            } catch (Exception ex) {
                actionRequest.setAttribute("link-not-deleted-successfully", "link-not-deleted-successfully");
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
    /**
     * Edit an existing shortcut
     */
    public void editShortcut(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
        long linkId = ParamUtil.getLong(actionRequest, "linkId");
        
        if (Validator.isNotNull(linkId)) {
            Map<Long, MyShortcuts> shortcutsMap = getShortcutsMap(actionRequest.getPortletSession());
            MyShortcuts myLink = shortcutsMap.get(linkId);
            
            if (myLink != null) {
                // Set the myLink object as a request attribute
                actionRequest.setAttribute("myLink", myLink);
                
                // Set the render parameter to navigate to the edit page
                actionResponse.setRenderParameter("jspPage", "/html/my-shortcuts/edit_shortcut.jsp");
                
                // Debug info
                System.out.println("Editing shortcut with ID: " + linkId);
                System.out.println("Setting jspPage to: " + "/html/my-shortcuts/edit_shortcut.jsp");
            } else {
                // Handle case where shortcut is not found
                System.out.println("Shortcut with ID " + linkId + " not found");
                actionResponse.setRenderParameter("jspPage", "/html/my-shortcuts/view.jsp");
            }
        } else {
            // Handle case where linkId is not provided
            System.out.println("No linkId provided for editing");
            actionResponse.setRenderParameter("jspPage", "/html/my-shortcuts/view.jsp");
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
            return "https://" + url;
        }
        return url;
    }
    
    private boolean isValidUrl(String url) {
        // Basic URL validation
        return url.contains(".");
    }
}