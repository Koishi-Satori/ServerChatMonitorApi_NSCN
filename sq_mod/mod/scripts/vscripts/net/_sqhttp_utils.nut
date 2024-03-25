untyped
global function sqhttp_utils_init
global function SqHttp_SendPostRequest
global function SqHttp_ReadGetResponse

void function sqhttp_utils_init() {
    // idk what need to do,
    // but lets just add it.
}

//
//  mmmm          mmmm         m    m       mmmmmmm       mmmmmmm        mmmmm
// #"   "        m"  "m        #    #          #             #           #   "#
// "#mmm         #    #        #mmmm#          #             #           #mmm#"
//     "#        #    #        #    #          #             #           #
// "mmm#"         #mm#"        #    #          #             #           #
//                   #
//

void function SqHttp_SendPostRequest(string url, table data) {
    printt("[KK_SQHTTP] try to send POST request with data.")
    thread NSHttpPostBody(url, EncodeJSON(data))
}

void function SqHttp_ReadGetResponse(string url, void functionref(HttpRequestResponse) succFunc, void functionref(HttpRequestFailure) failFunc) {
    printt("[KK_SQHTTP] try to read GET response.")
    HttpRequest request
    request.method = HttpRequestMethod.GET
    request.url = url
    NSHttpRequest( request, succFunc, failFunc )
}
