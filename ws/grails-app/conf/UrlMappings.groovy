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

        /* Repo Module */
        "/repo/web/$action?/$id?"(controller: "repo")
        "/repo/api/$action?/$id?"(controller: "repo")

        /* Token Management */
        "/token/web/$action?/$id?"(controller: "token")
        "/token/api/$action?/$id?"(controller: "token")
	}
}
