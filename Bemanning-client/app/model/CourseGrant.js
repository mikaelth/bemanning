Ext.define('Bemanning.model.CourseGrant', {
    extend: 'Ext.data.Model',
	proxy: {
		type: 'rest',
		url: Bemanning.data.Constants.BASE_URL.concat('cgs'),
		reader: {
			type: 'json',
			rootProperty: 'cgs'
         },
		writer: {
			type: 'json',
			clientIdProperty: 'clientId',
			writeAllFields: true,
			dateFormat: 'Y-m-d',
  /* 
          transform: {
                fn: function(data, request) {
					data.setDate = data.setDate == '' ? null : data.setDate;	
                    return data;
                },
                scope: this
            }
 */
         }

     }, 
	idProperty: 'id',
    fields: [
		{name: 'id', type: 'int'},
		{name: 'courseInstanceId', type: 'int'},
		{name: 'debitUnitId', type: 'int'},
		{name: 'departmentId', type: 'int'},
		{name: 'amount', type: 'int'},
		{name: 'type', type: 'string'},
		{name: 'note', type: 'string'},
		{name: 'setDate', type: 'date', format: 'yy-MM-dd'}
//		{name: 'setDate', type: 'string'}

    ]

/* 
    validators: {
    	code: 'presence',
    	seName: 'presence'
    }
 */

});


