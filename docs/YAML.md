## YAML Configuration Sample

When creating request/response data for the stub server, the config data should be specified in valid YAML 1.1 syntax.
Submit `POST` requests to `http://<host>:<admin_port>/stubdata/new` or load a data file (`-d` or `--data`) with the following structure for each endpoint:

* `request`: (REQUIRED) describes the client's call to the server
   * `method`: (REQUIRED) GET/POST/PUT/DELETE/etc. Can be array of multiple HTTP methods
   * `headers`: (OPTIONAL) a key/value map of HTTP headers the server should read from the request.
      * The key (header name) must be specified in lower case
      * If stubbed headers are a subset of headers in HTTP request, then the match is successful (`left outer join` concept)
   * `query`: (OPTIONAL) a key/value map of query string params the server should read from the URI
      * The key (param name) must have the letter case as the query string param name, ie: `paRamNaME=12` -> `paRamNaME: 12`
      * The order query string params does not matter. In other words the `/param1=1&param2=2` is the same as `/param2=2&param1=1`
      * If stubbed query params are a subset of query params in HTTP request, then the match is successful (`left outer join` concept)
      * query param can also be an array with quoted/un-quoted elements: `attributes=["id","uuid"]` or `attributes=[id,uuid]`. Please note no spaces between the CSV
   * `url`: (REQUIRED) the URI string.
      * If you include query string, it WILL BE stripped. If you have query params, include them in the `query` attribute
   * `file`: (OPTIONAL) if specified (an absolute path or path relative to the YAML in `-d` or `--data`), returns the contents of the given file as the `request` POST content.
   	* If the `file` was not provided, stubby fallsback to value from `post` property.
      * If `post` was not provided, it is assumed that POST body was not provided at all.
      * Use `file` for large POST content that otherwise inconvenient to configure as a one-liner. 
      * Please keep in mind: `SnakeYAML` lib (used by stubby4j) parser ruins multi-line strings by not preserving system line breaks. If `file` is used, the file content loaded as-is, in other words - it does not go through `SnakeYAML` parser. stubby4j stub server is dumb and does not use smart matching mechanism (ie:. don't match line separators or don't match any white space characters). Therefore its better to load POST content for `request` using `file` attribute
   * `post`: (OPTIONAL) a string matching the textual body of the POST request.
* `response`: (REQUIRED) describes the server's response to the client
   * `headers`: (OPTIONAL) a key/value map of headers the server should respond with
   * `latency`: (OPTIONAL) delay in milliseconds the server should wait before responding
   * `file`: (OPTIONAL) if specified (an absolute path or path relative to the YAML in `-d` or `--data`),
      returns the contents of the given file as the response body. It can be ascii of binary file (PDF, images, etc.)
      * If the `file` was not provided, stubby fallsback to value from `body` property. 
      * If `body` was not provided, an empty string is returned by default
   * `body`: (OPTIONAL) the textual body of the server's response to the client
   * `status`: (REQUIRED) the numerical HTTP status code (200 for OK, 404 for NOT FOUND, etc.)

## Various Configuration Examples

```yaml
-  request:
      method: GET
      url: /some/uri
      query:
         param: true
         anotherParam: false
      headers:
         authorization: bob:secret
         
   response:
      status: 200
      body: This is a single line text response


-  request:
      method: [GET, HEAD]
      url: /uri/simple
      headers:
         Authorization: bob:secret

   response:
      headers:
         content-type: application/json
         access-control-allow-origin: "*"
      status: 200
      body: >
         {
             "name": "alex"
         }

-  request:
      method: GET
      url: /pdf/release-notes
   response:
      headers:
         content-type: application/pdf
         content-disposition: "attachment; filename=release-notes.pdf"
         pragma: no-cache
      status: 200
      file: ../release-notes.pdf


-  request:
      method: GET
      url: /images/alex
   response:
      headers:
         content-type: image/png
         content-disposition: "attachment; filename=alexander.zagniotov.png"
      status: 200
      file: ../alexander.zagniotov.png


-  request:
      method: POST
      headers:
         content-type: application/json
      file: ../data/post-body-as-file.json

   response:
      headers:
         content-type: application/json
      status: 200
      body: OK


-  request:
      method: GET
      url: /entity.find
      query:
         type_name: user
         client_id: id
         client_secret: secret
         attributes: '["id","uuid","created","lastUpdated","displayName","email","givenName","familyName"]'

   response:
      status: 200
      body: >
         {"status": "hello world"}
      headers:
         content-type: application/json


-  request:
      method: POST
      url: /some/uri
      headers:
         content-type: application/json
      post: >
         {
            "name": "value",
            "param": "description"
         }

   response:
      headers:
         content-type: application/json
      status: 200
      body: >
         {"status" : "OK"}


-  request:
      method: GET
      url: /some/uri
      query:
         param: true
         anotherParam: false
      headers:
         authorization: bob:secret

   response:
      status: 200
      file: /home/development/application/testing/data/create-account-soap-response.xml


-  request:
      url: /some/uri
      query:
         paramTwo: 12345
         paramOne: valueOne
      method: POST
      headers:
         authorization: bob:secret
      post: this is some post data in textual format
   
   response:
      headers:
         content-type: application/json
      latency: 1000
      status: 200
      body: You're request was successfully processed!


-  request:
      method: GET
      url: /some/uri
      query:
         paramTwo: 12345
         paramOne: valueOne

   response:
      status: 200
      file: ../data/create-service-soap-response.xml
      latency: 1000


-  request:
      url: /some/uri
      query:
         firstParam: 1
         secondParam: 2
      method: POST
      headers:
         authorization: bob:secret

   response:
      headers:
         content-type: text/plain
      status: 200
      body: Success!


-  request:
      method: GET
      url: /some/uri
      
   response:
      headers:
         content-type: application/json
         access-control-allow-origin: "*"
      body: >
         {"status" : "success"}
      latency: 5000
      status: 201


-  request:
      method: GET
      headers:
         content-type: application/json
      url: /some/uri

   response:
      headers:
         content-type: application/text
         access-control-allow-origin: "*"
      latency: 1000
      body: >
         This is a text response, that can span across 
         multiple lines as long as appropriate indentation is in place.
      status: 200


-  request:
      method: GET
      headers:
         content-type: application/json
      url: /some/uri

   response:
      headers:
         content-type: application/xml
         access-control-allow-origin: "*"
      latency: 1000
      body: >
         <?xml version="1.0" encoding="UTF-8"?>
		 	<Response>
         	<Play loop="10">https://api.twilio.com/cowbell.mp3</Play>
         </Response>
      status: 200
      
      
-  request:
      method: GET
      url: /some/redirecting/uri

   response:
      latency: 1000
      status: 301
      headers:
         location: /some/other/uri
      body:
```
