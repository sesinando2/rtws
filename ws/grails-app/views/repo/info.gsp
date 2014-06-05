<%@ page import="au.com.adtec.realtime.webservice.repo.FileData" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Repository</title>
</head>

<body>
<div id="page_heading" class="grid_24" role="page_heading">
    <h1 class="page_title">Repository API</h1>
    <div class="page_nav"><g:link action="index">Repo Home</g:link></div>
    <p>This module allow users to upload and download files through HTTP request</p>
</div>
<div id="repository" class="grid_24" role="main">
    <div class="content">
        <h3>Upload</h3>
        <ul>
            <li>URL: <code><g:createLink action="upload" absolute="true" /></code></li>
            <li>Method: <code>POST</code></li>
            <li>Header: <code>X-Auth-Token = [Token]</code></li>
            <li>Request Body: <code>multipart/form-data</code></li>
            <li>Response: JSON map of File ID and URL</li>
        </ul>
        <ul><i>Notes:</i>
            <li><i>Token to be provided by the server</i></li>
            <li><i>Token may have restrictions on how many files can be uploaded</i></li>
        </ul>
        <p>Sample Header: <pre>X-Auth-Token = ts7qh3171d8ckkv5qj4i5s594dhdj4mv</pre></p>
        <p>Sample Response: <pre><pre>{base:"<g:createLink action="download" absolute="true" />", items:[2, 3]}</pre></pre></p>

        <h3>Download</h3>
        <ul>
            <li>URL: <code><g:createLink action="download" absolute="true"/>/[id]</code></li>
            <li>Method: <code>GET</code></li>
            <li>Header: <code>X-Auth-Token = [Token]</code></li>
            <li>Response: Binary data of the file</li>
        </ul>
        <ul><i>Notes:</i>
            <li><i>Token to be provided by the server</i></li>
            <li><i>Token is to a particular file and may be restricted on how many times the resource can be accessed</i></li>
        </ul>
        <p>Sample Header: <pre>X-Auth-Token = ts7qh3171d8ckkv5qj4i5s594dhdj4mv</pre></p>
        <p>Sample Request: <pre>GET: <g:createLink action="download" id="1" absolute="true"/></pre></p>

        <h3>Thumbnail Functionality</h3>
        <table>
            <tbody>
                <tr class="odd">
                    <td><code>GET:<g:createLink uri="/repo/web/{token}/{id}-thumb-{width}x{height}.jpg" absolute="true"/></code></td>
                    <td>
                        <p><code>token</code>: Generated token to access the file</p>
                        <p><code>id</code>: ID of the file to access</p>
                        <p><code>width</code>: Maximum width</p>
                        <p><code>height</code>: Maximum height</p>
                    </td>
                    <td>Create thumbnail for the file having a maximum width and height</td>
                </tr>
                <tr>
                    <td><code>GET:<g:createLink uri="/repo/web/{token}/{id}-square-{thumb}.jpg" absolute="true"/></code></td>
                    <td>
                        <p><code>token</code>: Generated token to access the file</p>
                        <p><code>id</code>: ID of the file to access</p>
                        <p><code>thumb</code>: Length</p>
                    </td>
                    <td>Create thumbnail for the file cropped in a square with the provided length</td>
                </tr>
                <tr class="odd">
                    <td><code>GET:<g:createLink uri="/repo/web/{token}/{id}-rect-{width}x{height}.jpg" absolute="true"/></code></td>
                    <td>
                        <p><code>token</code>: Generated token to access the file</p>
                        <p><code>id</code>: ID of the file to access</p>
                        <p><code>width</code>: Maximum width</p>
                        <p><code>height</code>: Maximum height</p>
                    </td>
                    <td>Create thumbnail for the file cropped in a rectangle with the provided width and height</td>
                </tr>
                <tr>
                    <td><code>GET:<g:createLink uri="/repo/web/{token}/{id}-height-{height}.jpg" absolute="true"/></code></td>
                    <td>
                        <p><code>token</code>: Generated token to access the file</p>
                        <p><code>id</code>: ID of the file to access</p>
                        <p><code>height</code>: height</p>
                    </td>
                    <td>Create thumbnail for the file resized to the specified height</td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>