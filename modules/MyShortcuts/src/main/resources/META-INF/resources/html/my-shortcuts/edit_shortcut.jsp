<%@include file="init.jsp" %>
<%@ page import="MyShortcuts.model.impl.MyShortcutsImpl" %>

<%
    // Get the shortcut to edit from request attribute
    MyShortcuts myLink = (MyShortcuts)request.getAttribute("myLink");
    
    // Set defaults if null
    if (myLink == null) {
        myLink = new MyShortcutsImpl();
    }
%>

<%
    // Create cancel URL
    javax.portlet.PortletURL cancelURL = renderResponse.createRenderURL();
    cancelURL.setParameter("jspPage", "/html/my-shortcuts/view.jsp");
    
    // Create save URL with proper action parameter
    javax.portlet.PortletURL saveURL = renderResponse.createActionURL();
    saveURL.setParameter("javax.portlet.action", "saveNewShortcut");
%>

<h2><%=(myLink.getPrimaryKey() > 0) ? "Edit" : "Add"%> Shortcut</h2>

<div class="shortcut-form" style="max-width: 500px; margin: 20px 0;">
    <form action="<%= saveURL.toString() %>" method="post">
        <!-- Hidden namespace input to ensure proper form processing -->
        <input type="hidden" name="<portlet:namespace/>javax.portlet.action" value="saveNewShortcut" />
        
        <!-- Link ID - important for update operation -->
        <input type="hidden" name="<portlet:namespace/>linkId" value="<%= myLink.getPrimaryKey() %>" />
        
        <div style="margin-bottom: 15px;">
            <label for="<portlet:namespace/>link" style="display: block; margin-bottom: 5px; font-weight: bold;">Link Title:</label>
            <input type="text" name="<portlet:namespace/>link" id="<portlet:namespace/>link" 
                   value="<%= myLink.getLinkTitle() %>" 
                   style="width: 100%; padding: 5px;" required />
        </div>
        
        <div style="margin-bottom: 15px;">
            <label for="<portlet:namespace/>url" style="display: block; margin-bottom: 5px; font-weight: bold;">Link URL:</label>
            <input type="text" name="<portlet:namespace/>url" id="<portlet:namespace/>url" 
                   value="<%= myLink.getLinkUrl() %>" 
                   style="width: 100%; padding: 5px;" required />
        </div>
        
        <div style="margin-top: 20px;">
            <button type="submit" 
                    style="background-color: #4CAF50; color: white; padding: 6px 12px; border: none; border-radius: 3px; cursor: pointer; margin-right: 10px;">
                Save
            </button>
            
            <a href="<%= cancelURL.toString() %>" 
               style="background-color: #f44336; color: white; padding: 6px 12px; text-decoration: none; border-radius: 3px;">
                Cancel
            </a>
        </div>
    </form>
</div>

<script>
    // Focus on the title field when the page loads
    document.getElementById('<portlet:namespace/>link').focus();
</script>