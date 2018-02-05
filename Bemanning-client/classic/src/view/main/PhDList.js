Ext.define('Bemanning.view.main.PhDList', {
    extend: 'Bemanning.view.main.BasicListGrid',
    requires: [
    ],
    xtype: 'phdlist',
	reference: 'phdList',

    title: 'Organisatoriska enheter',

	controller: 'phdlist',

	bind: {store: '{phds}'},
		
	features: [{ ftype: 'grouping',startCollapsed: false }],

    columns: [
		{ text: 'Program', dataIndex: 'program', align: 'left', width: 200 },
		{ text: 'Person', dataIndex: 'personId', editor: 'textfield', align: 'left', flex: 1,
		    renderer: function(value) {
				if (Ext.getStore('PersonStore').getById(value) != undefined) {
					return Ext.getStore('PersonStore').getById(value).get('formName');
				} else {
					return value;
				}
        	},
			editor: {
				xtype: 'combobox',
				typeAhead: true,
				triggerAction: 'all',
				bind: {store: '{person}'},
				queryMode: 'local',
				lastQuery: '',
				displayField: 'formName',
			    valueField: 'id'
			}
		},
		{ text: 'Antagningsdatum', dataIndex: 'start', editor: {xtype: 'datefield', format: 'Y-m-d'}, xtype:'datecolumn', format: 'Y-m-d', align: 'left', width: 100},
 		{ text: 'Disputationsdatum', dataIndex: 'dissertation', editor: {xtype: 'datefield', format: 'Y-m-d'}, xtype:'datecolumn', format: 'Y-m-d', align: 'left', width: 100 },
 		{ text: 'Beräknat slutdatum', dataIndex: 'predictedFinishDate', /* editor: {xtype: 'datefield', format: 'Y-m-d'}, */ xtype:'datecolumn', format: 'Y-m-d', align: 'left', width: 100 },
 		{ text: 'Återstående doktorandtid', dataIndex: 'currentRemainingProjectTime',  align: 'right', width: 100, renderer: Ext.util.Format.numberRenderer('0.0') },
 		{ text: 'Avklarad andel', dataIndex: 'done', align: 'right', width: 100,
			renderer: function percentRenderer(value, metadata, record) {
//				console.log(value);
				return [(value * 100).toFixed(0), '%'].join('');
			}
		},
		{ xtype: 'checkcolumn', text: 'Inaktiv', dataIndex: 'inactive', editor: 'checkboxfield', editable: true, align: 'center', width: 70},
		{ text: 'Kommentar', dataIndex: 'note', editor: 'textfield', align: 'left', flex: 2 }

	],

	config : {
	}


});

