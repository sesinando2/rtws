package au.com.adtec.realtime.webservice

class AdtecTagLib {

    // static defaultEncodeAs = "raw"

    static namespace = "adtec"

    // static encodeAsForTags = [tagName: 'raw']

    def link = { attrs, body ->
        String url = g.createLink(attrs, body)
        String match = attrs?.match.toString().replaceAll(/\{url\}/, url);
        def active = ''
        if (request.requestURI ==~ "^${match}") {
            active = 'class="active"'
        }
        out << "<a href=\"$url\" $active>$body</a>"
    }
}
