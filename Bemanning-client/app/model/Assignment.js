Ext.define('Bemanning.model.Assignment', {
    extend: 'Ext.data.Model',
	proxy: {
		type: 'rest',
		url: Bemanning.data.Constants.BASE_URL.concat('assignments'),
		reader: {
			type: 'json',
			rootProperty: 'assignments'
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
		{name: 'staffId', type: 'int'},
		{name: 'courseInstanceId', type: 'int'},
		{name: 'assigningDept', type: 'string'},
		{name: 'hoursAdmin', type: 'float'},
		{name: 'hoursDevelopment', type: 'float'},
		{name: 'hoursLecture', type: 'float'},
		{name: 'hoursPractical', type: 'float'},
		{name: 'hoursExcursion', type: 'float'},
		{name: 'hoursSeminar', type: 'float'},
		{name: 'note', type: 'string'},
		{name: 'totalHours', type: 'float'},
		{name: 'year', type: 'string'}

    ]


});


