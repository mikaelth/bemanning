Ext.define('Bemanning.view.staff.StaffModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.staff',

    data: {
        workingYear: '2017'
    },
    
    stores: {
		person : 'PersonStore',
		employmentTypes: {
			xtype: 'store.store',
			fields: [
				{name: 'label', type: 'string'}
			],	
			proxy: {
				 type: 'rest',
				 url: Bemanning.data.Constants.BASE_URL.concat('employmenttypes'),
				 reader: {
					 type: 'json'
				 }
			 }, 
			autoLoad: true
    			
		},    	
		ous: {
			xtype: 'store.store',
			fields: [
				{name: 'label', type: 'string'}
			],	
			proxy: {
				 type: 'rest',
				 url: Bemanning.data.Constants.BASE_URL.concat('employmenttypes'),
				 reader: {
					 type: 'json'
				 }
			 }, 
			autoLoad: true
    			
		},    	
		usedYears: {
			xtype: 'store.store',
			fields: [
				{name: 'label', type: 'string'}
			],	
			proxy: {
				 type: 'rest',
				 url: Bemanning.data.Constants.BASE_URL.concat('years'),
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
