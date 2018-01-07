Ext.define('Bemanning.view.staff.StaffModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.staff',

    data: {
		current : {
			year : ''
		}
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
			type: 'chained',
			source: 'OUStore',
			sorters: {property:'svName', direction: 'ASC'}
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
    			
		},    	
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
			sorters: {property:'label', direction: 'ASC'},
			autoLoad: true
    			
		},    	
		staff: {
			type: 'chained',
			source: 'StaffStore',
			sorters: {property:'formName', direction: 'ASC'},
			filters: [{property: 'year', value: '{current.year}', exactMatch: true}],
//			groupField: 'ouDesignation'
			groupField: 'organisationUnitId'
		}    	
	},
	
	formulas: {
        workingYear: {
            // We need to bind deep to be notified on each model change
            bind: {
                bindTo: '{comboCurrentYear.selection.label}', //--> reference configurated on the grid view (reference: comboCurrentYear)
                deep: true
            },
            get: function(year) {
            	this.set('current.year', year);
                return year;
            }
        }
	}
});
