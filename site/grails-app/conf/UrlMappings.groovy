class UrlMappings {
    static mappings = {

        "/"(controller: "home", action: "index")

        "/documentation/$version"(controller: "documentation", action: "dispatch")

        "/$controller/$action?/$id?"()

        "500"(view: '/error')
    }
}
