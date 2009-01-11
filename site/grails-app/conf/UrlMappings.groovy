class UrlMappings {
    static mappings = {

        "/"(controller:"home")
        
        "/$controller/$action?/$id?"
                {
                    constraints {
                        // apply constraints here
                    }
                }
        "500"(view: '/error')
    }
}
