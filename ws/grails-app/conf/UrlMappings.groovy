class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        //"/"(view:"/index")
        "/"(view:"/home")
        "500"(view:'/error')

        //region Repo Module
        "/repo/web/$action?/$id?"(controller: "repo")
        "/repo/api/$action?/$id?"(controller: "repo")
        //endregion

        //region Token Management
        "/token/web/$action?/$id?"(controller: "token")
        "/token/api/$action?/$id?"(controller: "token")
        //endregion

        //region Messaging Module
        "/message/web/canned/add"(controller: "message", action: "addCanned")
        "/message/api/canned/add"(controller: "message", action: "addCanned")

        "/message/web/canned/response/$id"(controller: "message", action: "cannedResponse")
        "/message/api/canned/response/$id"(controller: "message", action: "cannedResponse")

        "/message/web/$action?/$id?"(controller: "message")
        "/message/api/$action?/$id?"(controller: "message")
        //endregion
	}
}
