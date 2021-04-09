Ext.define("Bemanning.data.Constants", {
         singleton  : true,   
		
		SELMA_URL : "http://www.uu.se/utbildning/utbildningar/selma/kursplan/?type=1&kKod=",
		AUTH_URL : "https://cas.weblogin.uu.se/cas/login?service=http://localhost:8080/Bemanning/index.html",
		CORE_URL : Bemanning.data.Baseurl.URL_BASE,
		BASE_URL : Bemanning.data.Baseurl.URL_BASE.concat("rest/")
 });