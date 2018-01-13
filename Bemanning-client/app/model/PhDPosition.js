Ext.define('Bemanning.model.PhDPosition', {
    extend: 'Ext.data.Model',
	proxy: {
		type: 'rest',
		url: Bemanning.data.Constants.BASE_URL.concat('phdpositions'),
		reader: {
			type: 'json',
			rootProperty: 'phdpositions'
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
		{name: 'program', type: 'string'},
		{name: 'start', type: 'date', format: 'yy-MM-dd'},
		{name: 'dissertation', type: 'date', format: 'yy-MM-dd'},
		{name: 'note', type: 'string'},
		{name: 'inactive', type: 'boolean'},
		{name: 'predictedFinishDate', type: 'date', format: 'yy-MM-dd'},
		{name: 'predictedHalfTime', type: 'date', format: 'yy-MM-dd'},
		{name: 'predicted80Percent', type: 'date', format: 'yy-MM-dd'},
		{name: 'currentRemainingProjectTime', type: 'float'},
		{name: 'done', type: 'float', calculate: function (data){return (48-data.currentRemainingProjectTime)/48;}}

    ]

});


