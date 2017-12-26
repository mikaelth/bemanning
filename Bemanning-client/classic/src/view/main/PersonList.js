Ext.define('Bemanning.view.main.PersonList', {
    extend: 'Bemanning.view.main.BasicListGrid',
    requires: [
    ],
    xtype: 'personlist',
	reference: 'personlist',

    title: 'Personer',

	controller: 'personlist',
	viewModel: 'staff',


	store: 'PersonStore',
	
	features: [{ ftype: 'grouping',startCollapsed: true }],

    columns: [
		{ xtype: 'checkcolumn', text: 'Aktiv', dataIndex: 'isActive', editor: 'checkboxfield', editable: true, align: 'center', width: 60},
		{ text: 'AKKA-id', dataIndex: 'username', editor: 'textfield', align: 'left', width: 150 },
		{ text: 'Förnamn', dataIndex: 'givenName', editor: 'textfield', align: 'left', width: 100, flex: 1},
		{ text: 'Efternamn', dataIndex: 'familyName', editor: 'textfield', align: 'left', flex: 1 },
		{ xtype: 'checkcolumn', text: 'Familjenamn först', dataIndex: 'familyFirst', editor: 'checkboxfield', editable: true, align: 'center', width: 80 },
		{ text: 'Behörigheter', dataIndex: 'userRoles', align: 'left', flex: 1,
			editor: new Ext.form.field.Tag({
				typeAhead: true,
				triggerAction: 'all',
				bind: {store: '{usertypes}'},
				queryMode: 'local',
				lastQuery: '',
				displayField: 'label',
			    valueField: 'label'
				
			})},
		{ text: 'Anteckningar', dataIndex: 'note', editor: 'textfield', align: 'left', flex: 2 }

	],

	config : {
	}


});

