/**
 * The main application class. An instance of this class is created by app.js when it
 * calls Ext.application(). This is the ideal place to handle application launch and
 * initialization details.
 */
Ext.define('Bemanning.Application', {
    extend: 'Ext.app.Application',
    
    name: 'Bemanning',

    stores: [
		'PersonStore', 'CourseStore'		
    ],
    
    launch: function () {
        // TODO - Launch the application

       Ext.Ajax.on('requestexception', function (connection, response, requestOptions, listenerOptions) {
   			console.log("RequestException: " + response.status);
			if (response.status == 401) {
				window.location.replace(PorTableClient.data.Constants.CORE_URL.concat('index.html'));
			} else {
				Ext.MessageBox.alert('Status', 'RESTful interaction was seriously f-cked up: ' + response.statusText + ' Do not blame me when things subsequently do not work!');
			}
    	});
    },

    onAppUpdate: function () {
        Ext.Msg.confirm('Application Update', 'This application has an update, reload?',
            function (choice) {
                if (choice === 'yes') {
                    window.location.reload();
                }
            }
        );
    }
});
