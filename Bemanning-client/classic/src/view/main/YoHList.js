Ext.define('Bemanning.view.main.YoHList', {
    extend: 'Bemanning.view.main.BasicListGrid',
    requires: [
    ],
    xtype: 'yohlist',
	reference: 'yohList',

    title: ' ',

	controller: 'yohlist',

	dockedItems: [{
		xtype: 'toolbar',
		items: [
		 			{
						text: 'Reload all',
						reference: 'btnReload',
						disabled: false,
						 listeners: {
							click: 'onReload'
						}
					}, 
					'->', {
						text: 'Remove',
						reference: 'btnRemove',
//						disabled: true,
//						bind:{disabled: '{((current.ou == null) || (current.ou.id != current.yoh.unitId))}'},
//						bind:{disabled: '{current.yoh == null}'},
						bind:{disabled: '{current.ou.id != current.yoh.unitId}'},
						 listeners: {
							click: 'onRemove'
						}
					}, {
						text: 'Create',
						reference: 'btnCreate',
						bind:{disabled: '{current.ou == null}'},
						listeners: {
							click: 'onCreate'
						}
					}, {
						text: 'Save',
						reference: 'btnSave',
						listeners: {
							click: 'onSave'
						}
					}

			]
	}],


	bind: {
		store: '{ouyohs}',
		title: '{current.ou.svName}'
	},
	
	features: [{ ftype: 'grouping',startCollapsed: true }],

    columns: [
		{ text: 'Första året', dataIndex: 'firstYear', editor: 'textfield', align: 'left', width: 150 },
		{ text: 'Sista året', dataIndex: 'lastYear', editor: 'textfield', align: 'left', width: 150 },
		{ text: 'Överordnad enhet', dataIndex: 'superUnitId', align: 'left', width: 500, 
			renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
				if (Ext.getStore('OUStore').getById(value) != undefined) {
					return Ext.getStore('OUStore').getById(value).get('svName');
				} else {
					return value;
				}
			},
			editor: {
 				xtype: 'combobox',
				typeAhead: true,
				triggerAction: 'all',
				bind:{store: '{superOu}'},
				queryMode: 'local',
				lastQuery: '',
				displayField: 'svName',
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
		{ text: 'Anteckningar', dataIndex: 'note', editor: 'textfield', align: 'left', flex: 3 }
	],

	config : {
	}


});

