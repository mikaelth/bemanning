Ext.define('Bemanning.view.phds.PhDModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.phds',

    data: {
		current : {
			phd : null,
			progress : null,
			activeOnly: false
		}
    },
    
    stores: {
		phds : {
			type: 'chained',
			source: 'PhDStore',
			groupField: 'program'
		},
		person: 'PersonStore',
		progress: {
			type: 'chained',
			source: 'ProgressStore',
			sorters: {property:'date', direction: 'DESC'},
			filters: [{property: 'phdPositionId', value: '{current.phd.id}', exactMatch: true}]
		}    	
	},
	
 
	formulas: {
        currentPhD: {
            // We need to bind deep to be notified on each model change
            bind: {
                bindTo: '{phdList.selection}', //--> reference configurated on the grid view (reference: ouList)
                deep: true
            },
            get: function(phd) {
            	this.set('current.phd', phd);
                return phd;
            }
        },
        currentProgress: {
            // We need to bind deep to be notified on each model change
            bind: {
                bindTo: '{progressList.selection}', //--> reference configurated on the grid view (reference: ouList)
                deep: true
            },
            get: function(progress) {
            	this.set('current.progress', progress);
                return progress;
            }
        }
	}


});
