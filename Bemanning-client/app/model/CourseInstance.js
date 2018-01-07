Ext.define('Bemanning.model.CourseInstance', {
    extend: 'Ext.data.Model',
	proxy: {
		type: 'rest',
		url: Bemanning.data.Constants.BASE_URL.concat('cis'),
		reader: {
			type: 'json',
			rootProperty: 'cis'
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
		{name: 'year', type: 'string'},
		{name: 'extraDesignation', type: 'string'},
		{name: 'startDate', type: 'date', format: 'yy-MM-dd'},
		{name: 'endDate', type: 'date', format: 'yy-MM-dd'},
		{name: 'note', type: 'string'},
		{name: 'numberOfStudents', type: 'int'},
		{name: 'courseId', type: 'int'},
		{name: 'courseLeaderId', type: 'int'},
		{name: 'courseName', type: 'string'},
		{name: 'courseLeaderName', type: 'string'},
		{name: 'courseGroup', type: 'string'}

    ]

/* 
    validators: {
    	code: 'presence',
    	seName: 'presence'
    }
 */

});


