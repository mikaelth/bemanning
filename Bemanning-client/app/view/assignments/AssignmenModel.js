Ext.define('Bemanning.view.assignments.AssignmenModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.assignments',

    data: {
		current : {
			year : '',
			deptEditable: false,
//			asnDept: 'IOB',
			user : null
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
			groupField: 'staffId',
			filters: [{property: 'year', value: '{current.year}', exactMatch: true}]
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
			filters: [{property: 'year', value: '{current.year}', exactMatch: true}],
			sorters: {property:'formName', direction: 'ASC'},
		},    	
		cistore: {
			type: 'chained',
			source: 'CourseInstanceStore',
			filters: [{property: 'year', value: '{current.year}', exactMatch: true}],
			sorters: [{property:'courseName', direction: 'ASC'}]
		},    	
		cuser: {
			type: 'chained',
			source: 'CurrentUserStore'
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
        },
        currentUser: {
            // We need to bind deep to be notified on each model change
            bind: {
                bindTo: '{cuser}', //--> reference configurated on the grid view (reference: comboCurrentYear)
                deep: true
            },
            get: function(user) {
            	this.set('current.user', user.getAt(0));
                return user.getAt(0);
            }
        },
        cuDept: {
            // We need to bind deep to be notified on each model change
            bind: {
                depts: '{current.user.principalDepts}',
                year: '{current.year}'
            },
            get: function(data) {
                return data.depts[data.year];
            }
        }
 
 
	}
});
