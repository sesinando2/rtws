class UrlMappings {

	static mappings = {
        "/"(controller: 'home')
        "500"(view:'/error')

        //region Repo Module
        "/repo/web/info"(controller: "repo", action: "info")

        "/repo/web"(controller: "repo", action: "index")
        "/repo/web/$token/$id-thumb-${width}x${height}.jpg"(controller: "repo", action: "thumb")
        "/repo/web/$id-thumb-${width}x${height}.jpg"(controller: "repo", action: "thumb")
        "/repo/web/$token/$id-square-${thumb}.jpg"(controller: "repo", action: "square")
        "/repo/web/$token/$id-rect-${width}x${height}.jpg"(controller: "repo", action: "rect")
        "/repo/web/$token/$id-height-${height}.jpg"(controller: "repo", action: "height")

        "/repo/web/$token/view/$id"(controller: "repo", action: "view")
        "/repo/web/view/$id"(controller: "repo", action: "view")

        "/repo/web/$action?/$id?"(controller: "repo")
        "/repo/api/$action?/$id?"(controller: "repo")
        //endregion

        //region Token Management
        "/login/token"(controller: "token", action: "tokenLoginForm")
        "/token/web/login"(controller: "token", action: "login")

        "/token/web/"(controller: "token", action: "index")
        "/token/web/add/$type"(controller: "token", action: "addToken")
        "/token/web/$id"(controller: "token", action: "view")
        "/token/web/$id/restriction"(controller: "token", action: "addTokenRestriction")

        "/token/api/request/tracked/download"(controller: "token", action: "requestTrackedDownloadToken")
        "/token/tracking/${token}/${tokenAction}.jpg"(controller: "token", action: "trackToken")

        "/token/web/$action?/$id?"(controller: "token")
        "/token/api/$action?/$id?"(controller: "token")
        //endregion

        //region Messaging Module
        "/message/web/canned/add"(controller: "message", action: "addCanned")
        "/message/api/canned/add"(controller: "message", action: "addCanned")

        "/message/web/$token/canned/response/$id/$cannedResponse"(controller: "message", action: "cannedResponse")
        "/message/api/canned/response/$id"(controller: "message", action: "cannedResponse")

        "/message/web/$action?/$id?"(controller: "message")
        "/message/api/$action?/$id?"(controller: "message")
        //endregion

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
	}
}
