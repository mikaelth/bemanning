Ext.define('Bemanning.model.Progress', {
    extend: 'Ext.data.Model',
	proxy: {
		type: 'rest',
		url: Bemanning.data.Constants.BASE_URL.concat('progress'),
		reader: {
			type: 'json',
			rootProperty: 'progress'
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
		{name: 'phdPositionId', type: 'int'},
		{name: 'date', type: 'date', format: 'yy-MM-dd'},
		{name: 'activity', type: 'float'},
		{name: 'projectFraction', type: 'float'},
		{name: 'guFraction', type: 'float'},
		{name: 'toEcoSys', type: 'boolean'},
		{name: 'remainingMonths', type: 'float'},
		{name: 'toUpDok', type: 'boolean'},
		{name: 'note', type: 'string'},
		{name: 'addedMonths', type: 'float'}

    ]

/* 
    validators: {
    	code: 'presence',
    	seName: 'presence'
    }
 */

});


