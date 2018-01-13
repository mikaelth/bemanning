Ext.define('Bemanning.view.main.ProgressList', {
    extend: 'Bemanning.view.main.BasicListGrid',
    requires: [
    ],
    xtype: 'progresslist',
	reference: 'progressList',

    title: 'Aktivitet och kontering',

	controller: 'progresslist',



	bind: {store: '{progress}'},
		
	features: [{ ftype: 'grouping',startCollapsed: true }],

    columns: [
		{ text: 'Datum', dataIndex: 'date', editor: {xtype: 'datefield', format: 'Y-m-d'}, xtype:'datecolumn', format: 'Y-m-d', align: 'left', width: 100 },
		{ text: 'Aktivitetsgrad', dataIndex: 'activity', editor: 'textfield', align: 'right', width: 120,
			renderer: function percentRenderer(value, metadata, record) {
				console.log(value);
				return [(value * 100).toFixed(0), '%'].join('');
			}
		},
		{ text: 'Andel FO', dataIndex: 'projectFraction', editor: 'textfield', align: 'right', width: 120,
			renderer: function percentRenderer(value, metadata, record) {
				console.log(value);
				return [(value * 100).toFixed(0), '%'].join('');
			}
		},
 		{ text: 'Andel GU', dataIndex: 'guFraction', editor: 'textfield', align: 'right', width: 120,
 			renderer: function percentRenderer(value, metadata, record) {
				console.log(value);
				return [(value * 100).toFixed(0), '%'].join('');
			}
		},
		{ xtype: 'checkcolumn', text: 'Till Primula', dataIndex: 'toEcoSys', editor: 'checkboxfield', editable: true, align: 'center', width: 90},
		{ xtype: 'checkcolumn', text: 'Till UpDok', dataIndex: 'toUpDok', editor: 'checkboxfield', editable: true, align: 'center', width: 90},
 		{ text: 'Återstående tid', dataIndex: 'remainingMonths', editor: 'textfield', align: 'right', width: 120 },
 		{ text: 'Extra tid', dataIndex: 'addedMonths', editor: 'textfield', align: 'right', width: 120 },
		{ text: 'Kommentar', dataIndex: 'note', editor: 'textfield', align: 'left', flex: 1 }

	],

	config : {


	}


});

