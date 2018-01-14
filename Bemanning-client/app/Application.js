/**
 * The main application class. An instance of this class is created by app.js when it
 * calls Ext.application(). This is the ideal place to handle application launch and
 * initialization details.
 */
Ext.define('Bemanning.Application', {
    extend: 'Ext.app.Application',
    
    name: 'Bemanning',

    stores: [
		'PersonStore', 'CourseStore','StaffStore', 'OUStore', 'YoHStore', 'AssignmentStore', 
		'CourseInstanceStore', 'GrantStore', 'CurrentUserStore', 'PhDStore','ProgressStore'		
    ],
    
    launch: function () {
        // TODO - Launch the application

		Ext.ariaWarn = Ext.emptyFn;

		Ext.Ajax.on('requestexception', function (connection, response, requestOptions, listenerOptions) {
			console.log("RequestException: " + response.status);
			if (response.status == 401) {
				if(requestOptions.method == 'GET') {
					window.location.replace(Bemanning.data.Constants.CORE_URL.concat('loginredirect.html'));
				} else {
					Ext.MessageBox.alert('Status', 'Du saknar behörighet för detta, eller har blivit utloggad');
				}
			} else {
				Ext.MessageBox.alert('Status', 'RESTful-kommunikation gick inte som det skulle: ' + response.statusText + ' Skyll inte på mig om saker inte fungerar från och med nu!');
			}
		});
		
		
		Ext.getStore('CurrentUserStore').load();

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
