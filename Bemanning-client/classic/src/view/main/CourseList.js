Ext.define('Bemanning.view.main.CourseList', {
    extend: 'Bemanning.view.main.BasicListGrid',
    requires: [
    ],
    xtype: 'courselist',
	reference: 'courselist',

    title: 'Kurser',

	controller: 'courselist',
	viewModel: {type:'coursemodel'},


	store: 'CourseStore',
	
	features: [{ ftype: 'grouping',startCollapsed: false }],

    columns: [
		{ text: 'Kurskod', dataIndex: 'code', editor: 'textfield', align: 'left', width: 100 },
		{ text: 'Benämning', dataIndex: 'seName', editor: 'textfield', align: 'left', flex: 2},
		{ text: 'Engelsk benämning', dataIndex: 'enName', editor: 'textfield', align: 'left', flex: 1 },
		{ text: 'Kursgrupp', dataIndex: 'courseGroup', align: 'left', flex: 1,
			editor: {
				xtype: 'combobox',
				typeAhead: true,
				triggerAction: 'all',
				bind: {store: '{coursegroups}'},
				queryMode: 'local',
				lastQuery: '',
				displayField: 'label',
			    valueField: 'label',
			}
		},
		{ text: 'Kursperiod', dataIndex: 'period', editor: 'textfield', align: 'left', width: 80 },
		{ text: 'Poäng', dataIndex: 'credits', editor: 'textfield', align: 'left', width: 80 },
		{ text: 'Anteckningar', dataIndex: 'note', editor: 'textfield', align: 'left', flex: 3 }

	],

	config : {
	}


});

