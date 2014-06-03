class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: 'home')
        "500"(view:'/error')

        "/login/token"(view: "login/token")

        //region Repo Module
        "/repo/web/$action?/$id?"(controller: "repo")
        "/repo/api/$action?/$id?"(controller: "repo")

        "/repo/web/$token/$id-thumb-${width}x${height}.jpg"(controller: "repo", action: "thumb")
        "/repo/web/$token/$id-square-${thumb}.jpg"(controller: "repo", action: "square")
        "/repo/web/$token/$id-rect-${width}x${height}.jpg"(controller: "repo", action: "rect")
        "/repo/web/$token/$id-height-${height}.jpg"(controller: "repo", action: "height")

        "/repo/web/$token/view/$id"(controller: "repo", action: "view")

        "/repo/web/test"(view: "repo/test")
        //endregion

        //region Token Management
        "/token/web/$action?/$id?"(controller: "token")
        "/token/api/$action?/$id?"(controller: "token")

        "/token/web/"(controller: "token", action: "index")
        "/token/web/$id"(controller: "token", action: "view")

        "/token/api/request/tracked/download"(controller: "token", action: "requestTrackedDownloadToken")
        "/token/tracking/${token}/${tokenAction}.jpg"(controller: "token", action: "trackToken")
        //endregion

        //region Messaging Module
        "/message/web/canned/add"(controller: "message", action: "addCanned")
        "/message/api/canned/add"(controller: "message", action: "addCanned")

        "/message/web/$token/canned/response/$id/$cannedResponse"(controller: "message", action: "cannedResponse")
        "/message/api/canned/response/$id"(controller: "message", action: "cannedResponse")

        "/message/web/$action?/$id?"(controller: "message")
        "/message/api/$action?/$id?"(controller: "message")
        //endregion
	}
}
