Ext.define('Bemanning.view.staff.StaffModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.staff',

    data: {
    },
    
    stores: {
		usertypes: {
			xtype: 'store.store',
			fields: [
				{name: 'label', type: 'string'}
			],	
			proxy: {
				 type: 'rest',
				 url: Bemanning.data.Constants.BASE_URL.concat('userroletypes'),
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
