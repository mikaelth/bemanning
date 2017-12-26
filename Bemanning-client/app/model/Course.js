Ext.define('Bemanning.model.Course', {
    extend: 'Ext.data.Model',
	proxy: {
		type: 'rest',
		url: Bemanning.data.Constants.BASE_URL.concat('courses'),
		reader: {
			type: 'json',
			rootProperty: 'courses'
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
		{name: 'code', type: 'string'},
		{name: 'courseGroup', type: 'string'},
		{name: 'enName', type: 'string'},
		{name: 'seName', type: 'string'},
		{name: 'credits', type: 'float'},
		{name: 'period', type: 'string'},
		{name: 'note', type: 'string'}

    ]

/* 
    validators: {
    	code: 'presence',
    	seName: 'presence'
    }
 */

});


