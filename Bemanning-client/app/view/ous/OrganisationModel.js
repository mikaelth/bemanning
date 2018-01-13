Ext.define('Bemanning.view.ous.OrganisationModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.ous',

    data: {
		current : {
			ou : null,
			yoh : null
		}
    },
    
    stores: {
/* 
		coursegroups: {
			xtype: 'store.store',
			fields: [
				{name: 'label', type: 'string'},
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
 */
		ous : 'OUStore',
		superOu: {
			type: 'chained',
			source: 'OUStore',
			sorters: {property:'svName', direction: 'ASC'}
		},    	
		ouyohs: {
			type: 'chained',
			source: 'YoHStore',
			filters: [{property: 'unitId', value: '{current.ou.id}', exactMatch: true}]
		}    	
	},
	
 
	formulas: {
        currentOU: {
            // We need to bind deep to be notified on each model change
            bind: {
                bindTo: '{ouList.selection}', //--> reference configurated on the grid view (reference: ouList)
                deep: true
            },
            get: function(ou) {
            	this.set('current.ou', ou);
                return ou;
            },
/* 
           set: function(record) {

               if(!record.isModel) {
                    record = this.get('records').getById(record);
                }

                this.set('{currentOU', record);
            }
 */ 
        },
        currentYoH: {
            // We need to bind deep to be notified on each model change
            bind: {
                bindTo: '{yohList.selection}', //--> reference configurated on the grid view (reference: ouList)
                deep: true
            },
            get: function(yoh) {
            	this.set('current.yoh', yoh);
                return yoh;
            }
        }
	}


});
