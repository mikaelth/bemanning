Ext.define('Bemanning.view.staff.CourseModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.courses',

    data: {
    },
    
    stores: {
		coursegroups: {
			xtype: 'store.store',
			fields: [
				{name: 'label', type: 'string'},
//				{name: 'displayName', type: 'string'}
			],	
			proxy: {
				 type: 'rest',
				 url: Bemanning.data.Constants.BASE_URL.concat('coursegroups'),
				 reader: {
					 type: 'json'
				 }
			 }, 
			autoLoad: true
    			
		}    	
	},
	
/* 
	formulas: {
		selection: {
			bind: '{contigListGrid.selection}',
			get: function(selection){
				return selection;
			}
		}
	}
 */

});
