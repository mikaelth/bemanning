Ext.define('Bemanning.model.Person', {
    extend: 'Ext.data.Model',
	proxy: {
		type: 'rest',
		url: Bemanning.data.Constants.BASE_URL.concat('people'),
		reader: {
			type: 'json',
			rootProperty: 'people'
         },
		writer: {
			type: 'json',
			clientIdProperty: 'clientId',
			writeAllFields: true,
			dateFormat: 'Y-m-d'
         }

     }, 
	idProperty: 'id',
    fields: [
		{name: 'id', type: 'int'},
		{name: 'username', type: 'string'},
		{name: 'givenName', type: 'string'},
		{name: 'familyName', type: 'string'},
		{name: 'familyFirst', type: 'boolean'},
		{name: 'isActive', type: 'boolean'},
		{name: 'note', type: 'string'},
		{name: 'userRoles', type: 'auto'}

    ],

    validators: {
    	username: 'presence',
    	userRoles: 'presence'
    }

});


