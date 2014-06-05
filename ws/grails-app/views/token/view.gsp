<%@ page import="au.com.adtec.realtime.webservice.messaging.MessagingService; au.com.adtec.realtime.webservice.repo.RepoService; au.com.adtec.realtime.webservice.messaging.Message; au.com.adtec.realtime.webservice.repo.FileData" %>
<g:set var="token" value="${restTokenInstance}" />
<g:set var="upload" value="${token?.login == RepoService.USER_REPO_UPLOAD}" />
<g:set var="download" value="${token?.login == RepoService.USER_REPO_UPLOAD || token?.login == RepoService.USER_REPO_READ || token?.login == MessagingService.USER_MESSAGING_REPO_READ}" />
<g:set var="message" value="${token?.login == MessagingService.USER_MESSAGING_USER || token?.login == MessagingService.USER_MESSAGING_REPO_READ}" />
<g:set var="uploadUser" value="${token?.login == RepoService.USER_REPO_UPLOAD}" />
<g:set var="downloadUser" value="${token?.login == RepoService.USER_REPO_READ}" />
<g:set var="messagingUser" value="${token?.login == MessagingService.USER_MESSAGING_USER || token?.login == MessagingService.USER_MESSAGING_REPO_READ}" />
<html>
<head>
    <meta name="layout" content="main">
    <title>Token</title>
</head>

<body>
<div id="page_heading" class="grid_24" role="page_heading">
    <h1 class="page_title">Token: ${token.id}</h1>
    <div class="page_nav">
        <g:link action="index">Back to List</g:link>
        <g:link action="delete" id="${token?.id}">Delete</g:link>
    </div>
</div>
<div id="token_details_section" class="grid_ 24 content token_view" role="main">
    <form id="token_details">
        <div>
            <div class="label"><label>Token Value:</label></div>
            <div><p>${token.token}</p></div>
        </div>
        <div>
            <div class="label"><label>Date Created:</label></div>
            <div><p>${token.dateCreated}</p></div>
        </div>
    </form>
</div>
<div class="grid_24 section_heading" role="section_heading">
    <h3 class="section_heading">Authority</h3>
</div>
<div id="token_authority_section" class="grid_ 24 content token_view" role="section">
    <form id="token_authority_details">
        <div>
            <div class="label"><label>Login:</label></div>
            <div><p>${token.login}</p></div>
        </div>
        <div>
            <div class="label"><label>Roles:</label></div>
            <div><p>${roles}</p></div>
        </div>
    </form>
    <div id="token_restriction">
        <table>
            <thead>
                <tr>
                    <th>Restriction</th>
                    <th>Resource</th>
                    <th>Details</th>
                    <th></th>
                </tr>
            </thead>
            <tbody
            <g:each in="${restrictions}" var="r">
                <tr>
                    <td>${r.class.simpleName}</td>
                    <td>${r.restrictedResource}</td>
                    <td>${r.restrictionDetails}</td>
                    <td>
                        <g:link action="deleteTokenRestriction" id="${r.id}" params="[token: token.id]">Delete</g:link>
                    </td>
                </tr>
            </g:each>
        </table>
    </table>
    </div>
</div>
<div class="grid_24 section_heading" role="section_heading">
    <h3 class="section_heading">Add Token Restriction</h3>
    <g:if test="${flash?.message}">
    <p>${flash?.message}</p>
    </g:if>
</div>
<div id="add_token_restriction_section" class="grid_ 24 content token_view" role="section">
    <div id="add_token_restriction_form_container">
        <g:form name="add_token_restriction" method="put" action="addTokenRestriction" id="${token?.id}" useToken="true">
            <div>
                <div class="label"><label>Restriction Type:</label></div>
                <div>
                    <g:if test="${message}">
                        <g:radio name="type" value="MESSAGE" onchange="tokenRestrictionTypeChange(this.form)" checked="${messagingUser}"/>
                        <label>Message</label>
                    </g:if>
                    <g:if test="${upload}">
                        <g:radio name="type" value="UPLOAD" onchange="tokenRestrictionTypeChange(this.form)" checked="${uploadUser}"/>
                        <label>Upload</label>
                    </g:if>
                    <g:if test="${download}">
                        <g:radio name="type" value="DOWNLOAD" onchange="tokenRestrictionTypeChange(this.form)" checked="${downloadUser}"/>
                        <label>Download</label>
                    </g:if>
                </div>
            </div>
            <g:if test="${upload}">
            <div class="upload_field${uploadUser ? '' : ' hidden'}">
                <div class="label"><label>File Count:</label></div>
                <div>
                    <input type="number" name="fileCount" ${uploadUser ? '' : 'disabled="disabled"'} value="0">
                </div>
            </div>
            </g:if>
            <g:if test="${download}">
            <div class="download_field${downloadUser ? '' : ' hidden'}">
                <div class="label"><label>File:</label></div>
                <div>
                    <g:select name="file" from="${FileData.list()}" optionKey="id" disabled="${!downloadUser}"></g:select>
                </div>
            </div>
            <div class="download_field${downloadUser ? '' : ' hidden'}">
                <div class="label"><label>Download Count:</label></div>
                <div><input type="number" name="downloadCount" ${downloadUser ? '' : 'disabled="disabled"'} value="0"></div>
            </div>
            </g:if>
            <g:if test="${message}">
            <div class="message_field${messagingUser ? '' : ' hidden'}">
                <div class="label"><label>Message:</label></div>
                <div>
                    <g:select name="message" from="${Message.list()}" optionKey="id" disabled="${!messagingUser}"></g:select>
                </div>
            </div>
            <div class="message_field${messagingUser ? '' : ' hidden'}">
                <div class="label"><label>Read Count:</label></div>
                <div><input type="number" name="readCount" ${messagingUser ? '' : 'disabled="disabled"'} value="0"></div>
            </div>
            <div class="message_field${messagingUser ? '' : ' hidden'}">
                <div class="label"><label>Response Count:</label></div>
                <div><input type="number" name="responseCount" ${messagingUser ? '' : 'disabled="disabled"'} value="0"></div>
            </div>
            </g:if>
            <div class="submit">
                <div></div>
                <div><input type="submit" value="Add"></div>
            </div>
        </g:form>
    </div>
</div>
</body>
</html>