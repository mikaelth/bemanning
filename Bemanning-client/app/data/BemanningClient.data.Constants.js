Ext.define("BemanningClient.data.Constants", {
         singleton  : true,   
		
		AUTH_URL : "https://cas.weblogin.uu.se/cas/login?service=http://localhost:8080/PorTableClient/index.html",
		CORE_URL : "http://localhost:8080/",
		BASE_URL : "http://localhost:8080/rest/",
//		CORE_URL : "http://localhost:8080/portable/",
//		BASE_URL : "http://localhost:8080/portable/rest/"
// 		CORE_URL : "https://portax.ebc.uu.se/portable/",
// 		BASE_URL : "https://portax.ebc.uu.se/portable/rest/",
		SEQ_REPO_URLS : {
			GenBank : function(accNo){
				return 'https://www.ncbi.nlm.nih.gov/nuccore/'.concat(accNo);
			}
		}
 });