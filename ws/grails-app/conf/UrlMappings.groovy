class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')

        /* Repo Module */
        "/repo/$action?/$id?"(controller: "repo")

        /* Token Management */
        "/token/$action?/$id?"(controller: "token")
	}
}
