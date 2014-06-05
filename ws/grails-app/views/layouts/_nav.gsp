<ul>
    <sec:ifLoggedIn>
    <li>
        <div class="outerContainer">
            <div class="innerContainer">
                <adtec:link uri="/" match="{url}">Home</adtec:link>
            </div>
        </div>
    </li>
    <sec:ifAnyGranted roles="ROLE_REPO_READ">
    <li>
        <div class="outerContainer">
            <div class="innerContainer">
                <adtec:link controller="repo" match="{url}.*">Repository</adtec:link>
            </div>
        </div>
    </li>
    </sec:ifAnyGranted>
    <sec:ifAnyGranted roles="ROLE_ADMIN">
    <li>
        <div class="outerContainer">
            <div class="innerContainer">
                <adtec:link controller="token" match="{url}.*">Token Management</adtec:link>
            </div>
        </div>
    </li>
    </sec:ifAnyGranted>
    <sec:ifAnyGranted roles="ROLE_MESSAGING_USER">
    <li>
        <div class="outerContainer">
            <div class="innerContainer">
                <adtec:link controller="message" match="{url}.*">Messaging</adtec:link>
            </div>
        </div>
    </li>
    </sec:ifAnyGranted>
    <li>
        <div class="outerContainer">
            <div class="innerContainer">
                <adtec:link uri="/logout">Logout</adtec:link>
            </div>
        </div>
    </li>
    </sec:ifLoggedIn>
</ul>