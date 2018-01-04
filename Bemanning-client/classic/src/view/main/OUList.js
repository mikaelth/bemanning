Ext.define('Bemanning.view.main.OUList', {
    extend: 'Bemanning.view.main.BasicListGrid',
    requires: [
    ],
    xtype: 'oulist',
	reference: 'ouList',

    title: 'Organisatoriska enheter',

	controller: 'oulist',


	store: 'OUStore',
	
	features: [{ ftype: 'grouping',startCollapsed: true }],

    columns: [
		{ text: 'Förkortning', dataIndex: 'abbreviation', editor: 'textfield', align: 'left', width: 100 },
		{ text: 'Svensk benämning', dataIndex: 'svName', editor: 'textfield', align: 'left', flex: 1},
		{ text: 'Engelsk benämning', dataIndex: 'enName', editor: 'textfield', align: 'left', flex: 1 },
 		{ text: 'Enhetstyp', dataIndex: 'unitKind', editor: 'textfield', align: 'left', width: 150 },
		{ xtype: 'checkcolumn', text: 'BP', dataIndex: 'inSystem', editor: 'checkboxfield', editable: true, align: 'center', width: 70},
		{ xtype: 'checkcolumn', text: 'Ek.', dataIndex: 'courseEconomyHolder', editor: 'checkboxfield', editable: true, align: 'center', width: 70},
		{ xtype: 'checkcolumn', text: 'Arv', dataIndex: 'legacyUnit', editor: 'checkboxfield', editable: true, align: 'center', width: 70}
//		{ text: 'Anteckningar', dataIndex: 'note', editor: 'textfield', align: 'left', flex: 3 }

	],

	config : {
	}


});

