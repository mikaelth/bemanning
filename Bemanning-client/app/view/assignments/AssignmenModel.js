Ext.define('Bemanning.view.assignments.AssignmenModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.assignments',

    data: {
		current : {
			year : ''
		}
    },
    
    stores: {
/* 
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
 */
		assignments: {
			type: 'chained',
			source: 'AssignmentStore',
			filters: [{property: 'year', value: '{current.year.label}', exactMatch: true}]
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
/* 
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
 */
		staff: {
			type: 'chained',
			source: 'StaffStore',
			filters: [{property: 'year', value: '{current.year.label}', exactMatch: true}],
			sorters: {property:'formName', direction: 'ASC'},
		}    	
	},
	
	formulas: {
        workingYear: {
            // We need to bind deep to be notified on each model change
            bind: {
                bindTo: '{comboCurrentYear.selection}', //--> reference configurated on the grid view (reference: comboCurrentYear)
                deep: true
            },
            get: function(year) {
            	this.set('current.year', year);
                return year;
            }
        }
	}
});
