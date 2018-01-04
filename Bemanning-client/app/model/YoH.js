Ext.define('Bemanning.model.YoH', {
    extend: 'Ext.data.Model',
	proxy: {
		type: 'rest',
		url: Bemanning.data.Constants.BASE_URL.concat('yoh'),
		reader: {
			type: 'json',
			rootProperty: 'yoh'
         },
		writer: {
			type: 'json',
			clientIdProperty: 'clientId',
			writeAllFields: true,
			dateFormat: 'Y-m-d',
            transform: {
                fn: function(data, request) {
					data.lastYear = data.lastYear == '' ? null : data.lastYear;	
                    return data;
                },
                scope: this
            }
         }

     }, 
	idProperty: 'id',
    fields: [
		{name: 'id', type: 'int'},
		{name: 'firstYear', type: 'int'},
		{name: 'lastYear', type: 'string'},
		{name: 'superUnitId', type: 'int'},
		{name: 'unitId', type: 'int'},
		{name: 'note', type: 'string'},
	]
});


