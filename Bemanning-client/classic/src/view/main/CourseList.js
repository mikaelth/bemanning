Ext.define('Bemanning.view.main.CourseList', {
    extend: 'Bemanning.view.main.BasicListGrid',
    requires: [
    ],
    xtype: 'courselist',
	reference: 'courselist',

    title: 'Kurser',

	controller: 'courselist',
	viewModel: 'courses',


	store: 'CourseStore',
	
	features: [{ ftype: 'grouping',startCollapsed: true }],

    columns: [
		{ text: 'Kurskod', dataIndex: 'code', editor: 'textfield', align: 'left', width: 100 },
		{ text: 'Benämning', dataIndex: 'seName', editor: 'textfield', align: 'left', flex: 2},
		{ text: 'Engelsk benämning', dataIndex: 'enName', editor: 'textfield', align: 'left', flex: 1 },
		{ text: 'Kursgrupp', dataIndex: 'courseGroup', align: 'left', flex: 1,
			editor: new Ext.form.field.ComboBox({
				typeAhead: true,
				triggerAction: 'all',
				bind: {store: '{coursegroup}'},
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
		{ text: 'Kursperiod', dataIndex: 'period', editor: 'textfield', align: 'left', width: 80 },
		{ text: 'Poäng', dataIndex: 'credits', editor: 'textfield', align: 'left', width: 80 },
		{ text: 'Anteckningar', dataIndex: 'note', editor: 'textfield', align: 'left', flex: 3 }

	],

	config : {
	}


});

