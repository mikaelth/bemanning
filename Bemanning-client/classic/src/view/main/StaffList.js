Ext.define('Bemanning.view.main.StaffList', {
    extend: 'Bemanning.view.main.BasicYearListGrid',
    requires: [
    ],
    xtype: 'stafflist',
	reference: 'stafflist',

    title: 'Personal',

	controller: 'stafflist',
	viewModel: 'staff',


	store: 'StaffStore',


	
	features: [{ ftype: 'grouping',startCollapsed: true }],

    columns: [
		{ text: 'Namn', dataIndex: 'personId', editor: 'textfield', align: 'left', width: 150,
         	renderer: function(value) {
				if (this.getViewModel().getStore('person').getById(value) != undefined) {
					return this.getViewModel().getStore('person').getById(value).get('name');
				} else {
					return value;
				}
        	},
			editor: new Ext.form.field.ComboBox({
				typeAhead: true,
				triggerAction: 'all',
				bind: {store: '{person}'},
				queryMode: 'local',
				lastQuery: '',
				displayField: 'formName',
			    valueField: 'id',
/* 
			    listeners: {
					// delete the previous query in the beforequery event or set
					// combo.lastQuery = null (this will reload the store the next time it expands)
					beforequery: function(qe){
						delete qe.combo.lastQuery;
					}							    
			    }				
 */
			})
		},
		{ text: 'Enhet', dataIndex: 'organisationUnitId', align: 'left', width: 100, flex: 1,
			editor: new Ext.form.field.ComboBox({
				typeAhead: true,
				triggerAction: 'all',
				bind: {store: '{employmentTypes}'},
				queryMode: 'local',
				lastQuery: '',
				displayField: 'label',
			    valueField: 'label',
/* 
			    listeners: {
					// delete the previous query in the beforequery event or set
					// combo.lastQuery = null (this will reload the store the next time it expands)
					beforequery: function(qe){
						delete qe.combo.lastQuery;
					}							    
			    }				
 */
			})
		},
		{ text: 'Befattning', dataIndex: 'position', align: 'left', flex: 1,
			editor: new Ext.form.field.ComboBox({
				typeAhead: true,
				triggerAction: 'all',
				bind: {store: '{employmentTypes}'},
				queryMode: 'local',
				lastQuery: '',
				displayField: 'label',
			    valueField: 'label',
/* 
			    listeners: {
					// delete the previous query in the beforequery event or set
					// combo.lastQuery = null (this will reload the store the next time it expands)
					beforequery: function(qe){
						delete qe.combo.lastQuery;
					}							    
			    }				
 */
			})
		},
		{ text: 'Timkostnad', dataIndex: 'hourlyCharge', editor: 'textfield', align: 'left', flex: 1 },
		{ text: 'Procent GU', dataIndex: 'percentGU', editor: 'numberfield', align: 'left', flex: 1,
			renderer: function(value) {
				return Ext.util.Format.number(value * 100, '0%');
			}
		},
		{ text: 'Timmar GU', dataIndex: 'hoursGU', editor: 'textfield', align: 'left', flex: 1 },
		{ text: 'IB timmar', dataIndex: 'ib', editor: 'textfield', align: 'left', flex: 1 },
		{ text: 'UB föregående år', dataIndex: 'ubLastYear', align: 'left', flex: 1 },
		{ text: 'Accumulerade timmar', dataIndex: 'accumulatedHours', align: 'left', flex: 1 },
		{ text: 'Kommentar', dataIndex: 'note', editor: 'textfield', align: 'left', flex: 2 }

	],

	config : {
	}


});

