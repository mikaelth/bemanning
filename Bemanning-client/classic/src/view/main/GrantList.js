Ext.define('Bemanning.view.main.GrantList', {
    extend: 'Bemanning.view.main.BasicListGrid',
    requires: [
    ],
    xtype: 'grantlist',
	reference: 'grantList',

    title: ' ',

	controller: 'grantlist',
//	viewModel: {type: 'coursemodel'},


	bind: {
		store: '{cgs}',
		title: '{current.ci.courseName}'
	},
	

    columns: [
		{ text: 'Typ', dataIndex: 'type', editor: 'textfield', align: 'left', width: 150 },
		{ text: 'Betalande', dataIndex: 'debitUnitId', align: 'left', width: 150, 
			renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
				if (Ext.getStore('OUStore').getById(value) != undefined) {
					return Ext.getStore('OUStore').getById(value).get('abbreviation');
				} else {
					return value;
				}
			},
			editor: {
 				xtype: 'combobox',
				typeAhead: true,
				triggerAction: 'all',
//				bind:{store: '{cis}'},
				store: 'OUStore',
				queryMode: 'local',
				lastQuery: '',
				displayField: 'abbreviation',
			    valueField: 'id',
				listeners: {
					// delete the previous query in the beforequery event or set
					// combo.lastQuery = null (this will reload the store the next time it expands)
					beforequery: function(qe){
						delete qe.combo.lastQuery;
					}
				}
			}
		},
		{ text: 'Mottagande', dataIndex: 'departmentId', align: 'left', width: 150, 
			renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
				if (Ext.getStore('OUStore').getById(value) != undefined) {
					return Ext.getStore('OUStore').getById(value).get('abbreviation');
				} else {
					return value;
				}
			},
			editor: {
 				xtype: 'combobox',
				typeAhead: true,
				triggerAction: 'all',
//				bind:{store: '{cis}'},
				store: 'OUStore',
				queryMode: 'local',
				lastQuery: '',
				displayField: 'abbreviation',
			    valueField: 'id',
				listeners: {
					// delete the previous query in the beforequery event or set
					// combo.lastQuery = null (this will reload the store the next time it expands)
					beforequery: function(qe){
						delete qe.combo.lastQuery;
					}
				}
			}
		},
		{ text: 'Belopp', dataIndex: 'amount', editor: 'textfield', align: 'left', width: 150 },
		{ text: 'Reglerat',  dataIndex: 'setDate', editor: {xtype: 'datefield', format: 'Y-m-d'}, xtype:'datecolumn', format: 'Y-m-d', align: 'left', width: 200  },
		{ text: 'Kommentar', dataIndex: 'note', editor: 'textfield', align: 'left', flex: 3 }
	],

	config : {
	}


});

