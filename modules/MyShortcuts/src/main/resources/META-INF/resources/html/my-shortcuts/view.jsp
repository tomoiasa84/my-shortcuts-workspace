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

<% if (linkSaved != null) { %>
    <div class="alert alert-success">Shortcut saved successfully</div>
<% } %>

<% if (linkUpdated != null) { %>
    <div class="alert alert-success">Shortcut updated successfully</div>
<% } %>

<% if (linkDeleted != null) { %>
    <div class="alert alert-success">Shortcut deleted successfully</div>
<% } %>

<%
    // Create add URL using PortletURL
    javax.portlet.PortletURL addURL = renderResponse.createActionURL();
    addURL.setParameter("javax.portlet.action", "addNewShortcut");
%>

<div class="addShortcut">
    <a href="<%= addURL.toString() %>">Add New Shortcut</a>
</div>

<% if(userLinks.isEmpty()) { %>
    <br><br>
    <div class="alert alert-info">Add your first shortcut</div>
<% } else { %>
    <div id="userShortcuts">
        <ul class="list-unstyled">
        <% for (MyShortcuts userLink : userLinks) { %>
            <li class="mb-2" style="margin-bottom: 10px; padding: 5px; border-bottom: 1px solid #eee;">
                <div style="display: inline-block; width: 70%;">
                    <a href="<%= userLink.getLinkUrl() %>" target="_blank">
                        <%= userLink.getLinkTitle() %>
                    </a>
                </div>
                
                <%
                    // Create delete URL
                    javax.portlet.PortletURL deleteURL = renderResponse.createActionURL();
                    deleteURL.setParameter("javax.portlet.action", "deleteShortcut");
                    deleteURL.setParameter("linkId", String.valueOf(userLink.getPrimaryKey()));
                    
                    // Create edit URL
                    javax.portlet.PortletURL editURL = renderResponse.createActionURL();
                    editURL.setParameter("javax.portlet.action", "editShortcut");
                    editURL.setParameter("linkId", String.valueOf(userLink.getPrimaryKey()));
                %>
                    
                <div style="display: inline-block; margin-left: 10px;">
                    <a href="<%= deleteURL.toString() %>" 
                       style="background-color: #f44336; color: white; padding: 2px 6px; text-decoration: none; border-radius: 3px;"
                       onclick="return confirm('Are you sure you want to delete this shortcut?');">
                        Delete
                    </a>
                </div>
                
                <div style="display: inline-block; margin-left: 10px;">
                    <a href="<%= editURL.toString() %>"
                       style="background-color: #4CAF50; color: white; padding: 2px 6px; text-decoration: none; border-radius: 3px;">
                        Edit
                    </a>
                </div>
                
                <!-- Debug information (uncomment to see actual URLs) -->
                <!-- 
                <div style="font-size: 10px; color: #999; margin-top: 5px;">
                    Delete URL: <%= deleteURL.toString() %><br>
                    Edit URL: <%= editURL.toString() %>
                </div>
                -->
            </li>
        <% } %>
        </ul>
    </div>
<% } %>