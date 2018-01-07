Ext.define('Bemanning.model.User', {
    extend: 'Ext.data.Model',
	proxy: {
		type: 'rest',
		url: Bemanning.data.Constants.BASE_URL.concat('currentuser'),
		reader: {
			type: 'json',
			rootProperty: 'currentuser'
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
		{name: 'formName', type: 'string'},
		{name: 'name', type: 'string'},
		{name: 'userRoles', type: 'auto'}
	],
	hasMany: [{
		model: 'principalDepts',			// name of model
		name: 'principalDepts',           	// name of method to access store
		associationKey: 'principalDepts'  			// name of property in data
	}]

 
});


