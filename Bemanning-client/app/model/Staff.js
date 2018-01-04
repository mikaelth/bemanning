Ext.define('Bemanning.model.Staff', {
    extend: 'Ext.data.Model',
	proxy: {
		type: 'rest',
		url: Bemanning.data.Constants.BASE_URL.concat('staff'),
		reader: {
			type: 'json',
			rootProperty: 'staff'
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
		{name: 'personId', type: 'int'},
		{name: 'organisationUnitId', type: 'int'},
		{name: 'program', type: 'string'},
		{name: 'percentGU', type: 'float'},
		{name: 'hoursGU', type: 'int'},
		{name: 'position', type: 'string'},
		{name: 'hourlyCharge', type: 'float'},
		{name: 'year', type: 'string'},
		{name: 'note', type: 'string'},
		{name: 'ib', type: 'float'},
		{name: 'ubLastYear', type: 'float'},
		{name: 'name', type: 'string'},
		{name: 'formName', type: 'string'},
		{name: 'ouDesignation', type: 'string'},
		{name: 'accumulatedHours', type: 'float'},
		{name: 'maxHourlyCharge', type: 'float'},
		{name: 'hours', type: 'float', mapping: 'percentGU', convert: function (v, record){return v/100*1700;}}

    ]


});


