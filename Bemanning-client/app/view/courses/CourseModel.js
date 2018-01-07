Ext.define('Bemanning.view.courses.CourseModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.coursemodel',

    data: {
		current : {
			year : '',
			ci : null
		}
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
		cistore: {
			type: 'chained',
			source: 'CourseInstanceStore',
			filters: [{property: 'year', value: '{current.year}', exactMatch: true}],
			sorters: [{property:'courseName', direction: 'ASC'}],
			groupField: 'courseGroup'
		},    	
		courses: {
			type: 'chained',
			source: 'CourseStore'
		},
		staff: {
			type: 'chained',
			source: 'StaffStore',
			sorters: {property:'formName', direction: 'ASC'},
			filters: [{property: 'year', value: '{current.year}', exactMatch: true}],
		},
 
		cgs: {
			type: 'chained',
			source:'GrantStore',
			filters:[{property: 'courseInstanceId', value: '{current.ci.id}', exactMatch: true}]
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
 
        currentCI: {
            // We need to bind deep to be notified on each model change
            bind: {
                bindTo: '{ciList.selection}', //--> reference configurated on the grid view (reference: ouList)
                deep: true
            },
            get: function(ci) {
            	this.set('current.ci', ci);
                return ci;
            },
        }
 

	}

});
