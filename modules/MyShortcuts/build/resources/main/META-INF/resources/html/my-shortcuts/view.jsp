<%@include file="init.jsp" %>

<%
    List<MyShortcuts> userLinks = (List<MyShortcuts>)renderRequest.getAttribute("shortcutsList");
    if (userLinks == null) {
        userLinks = new ArrayList<MyShortcuts>();
    }
    
    String linkSaved = (String)request.getAttribute("link-saved-successfully");
    String linkUpdated = (String)request.getAttribute("link-updated-successfully");
    String linkDeleted = (String)request.getAttribute("link-deleted-successfully");
%>

<div class="myshortcuts-portlet">
    <% if (linkSaved != null || linkUpdated != null || linkDeleted != null) { %>
        <div class="shortcuts-messages">
            <% if (linkSaved != null) { %>
                <div class="portlet-msg-success">Shortcut saved successfully</div>
            <% } %>
            
            <% if (linkUpdated != null) { %>
                <div class="portlet-msg-success">Shortcut updated successfully</div>
            <% } %>
            
            <% if (linkDeleted != null) { %>
                <div class="portlet-msg-success">Shortcut deleted successfully</div>
            <% } %>
        </div>
    <% } %>

    <div class="shortcuts-header">
        <%
            // Create add URL using PortletURL
            javax.portlet.PortletURL addURL = renderResponse.createActionURL();
            addURL.setParameter("javax.portlet.action", "addNewShortcut");
        %>
        
        <div class="add-new-container">
            <a href="<%= addURL.toString() %>" class="add-new-link">Add New Shortcut</a>
        </div>
    </div>

    <% if(userLinks.isEmpty()) { %>
        <div class="empty-message">Add your first shortcut</div>
    <% } else { %>
        <div class="shortcuts-list">
            <table class="shortcuts-table">
                <tbody>
                <% for (MyShortcuts userLink : userLinks) { %>
                    <tr class="shortcut-row">
                        <td class="shortcut-title">
                            <a class="shortcut-link" href="<%= userLink.getLinkUrl() %>" target="_blank">
                                <%= userLink.getLinkTitle() %>
                            </a>
                        </td>
                        <td class="shortcut-actions">
                            <%
                                // Create edit URL
                                javax.portlet.PortletURL editURL = renderResponse.createActionURL();
                                editURL.setParameter("javax.portlet.action", "editShortcut");
                                editURL.setParameter("linkId", String.valueOf(userLink.getPrimaryKey()));
                            %>
                            <a href="<%= editURL.toString() %>" class="edit-icon" title="Edit"> <svg width="16" height="16" viewBox="0 0 24 24">
        <path fill="#666" d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
    </svg></a>
                            
                            <%
                                // Create delete URL
                                javax.portlet.PortletURL deleteURL = renderResponse.createActionURL();
                                deleteURL.setParameter("javax.portlet.action", "deleteShortcut");
                                deleteURL.setParameter("linkId", String.valueOf(userLink.getPrimaryKey()));
                            %>
                            <a href="<%= deleteURL.toString() %>" class="delete-icon" title="Delete"> <svg width="16" height="16" viewBox="0 0 24 24">
        <path fill="#666" d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
    </svg></a>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    <% } %>
</div>

<style>
/* Main portlet styles */
.myshortcuts-portlet {
    font-family: Arial, sans-serif;
    padding: 0;
    background-color: #f5f5f5;
    border: 1px solid #e3e3e3;
    border-radius: 4px;
}

/* Header styles */
.shortcuts-header {
    position: relative;
    border-bottom: 1px solid #ddd;
    padding: 5px 10px;
    background-color: #f9f9f9;
    text-align: right;
}

.add-new-container {
    display: inline-block;
}

.add-new-link {
    font-size: 12px;
    color: #5b677d;
    text-decoration: none;
}

/* Message styles */
.shortcuts-messages {
    padding: 8px;
}

.portlet-msg-success {
    padding: 8px;
    margin-bottom: 10px;
    border-radius: 4px;
    background-color: #dff0d8;
    border: 1px solid #d6e9c6;
    color: #3c763d;
}

.empty-message {
    margin: 10px;
    padding: 8px;
    color: #555;
}

/* Shortcuts list styles */
.shortcuts-list {
    padding: 0;
}

.shortcuts-table {
    width: 100%;
    border-collapse: collapse;
}

.shortcut-row {
    border-bottom: 1px solid #f0f0f0;
}

.shortcut-row:last-child {
    border-bottom: none;
}

.shortcut-title {
    padding: 8px 12px;
    width: 85%;
}

.shortcut-actions {
    padding: 8px 12px;
    text-align: right;
    white-space: nowrap;
    width: 15%;
}

.shortcut-link {
    color: #0066cc;
    text-decoration: none;
    font-weight: normal;
}

.shortcut-link:hover {
    text-decoration: underline;
}

.edit-icon{
    margin-left: 5px;
    display: inline-block;
    width: 16px;
    height: 16px;
    line-height: 16px;
    text-align: center;
    font-weight: bold;
    font-size: 10px;
    background-color: #4CAF50;
    color: white;
    text-decoration: none;
    border-radius: 2px;
}

.delete-icon {
    margin-left: 5px;
    display: inline-block;
    width: 16px;
    height: 16px;
    line-height: 16px;
    text-align: center;
    font-weight: bold;
    font-size: 10px;
    background-color: #f44336;
    color: white;
    text-decoration: none;
    border-radius: 2px;
}

.edit-icon:hover, 
.delete-icon:hover {
    background-color: #b0b0b0;
}
</style>