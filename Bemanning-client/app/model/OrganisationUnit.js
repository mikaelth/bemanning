Ext.define('Bemanning.model.OrganisationUnit', {
    extend: 'Ext.data.Model',
	proxy: {
		type: 'rest',
		url: Bemanning.data.Constants.BASE_URL.concat('ous'),
		reader: {
			type: 'json',
			rootProperty: 'ous'
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
		{name: 'svName', type: 'string'},
		{name: 'enName', type: 'string'},
		{name: 'unitKind', type: 'string'},
		{name: 'abbreviation', type: 'string'},
		{name: 'inSystem', type: 'boolean'},
		{name: 'legacyUnit', type: 'boolean'},
		{name: 'courseEconomyHolder', type: 'boolean'}
	]
});


