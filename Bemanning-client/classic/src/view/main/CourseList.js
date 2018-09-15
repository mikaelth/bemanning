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
		{xtype:'actioncolumn',
            width:40,
            items: [{
				iconCls: 'x-fa fa-list-alt',
				tooltip: 'Kursplan',
				handler: function(grid, rowIndex, colIndex){
					var rec = grid.getStore().getAt(rowIndex);
					window.open(Bemanning.data.Constants.SELMA_URL.concat(rec.get('code')));				
				}
			}]
        },
		{ text: 'Kurskod', dataIndex: 'code', editor: 'textfield', filter: 'string', align: 'left', width: 100 },
		{ text: 'Benämning', dataIndex: 'seName', editor: 'textfield', filter: 'string', align: 'left', flex: 2},
		{ text: 'Engelsk benämning', dataIndex: 'enName', editor: 'textfield', filter: 'string', align: 'left', flex: 1 },
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
		{ text: 'Anteckningar', dataIndex: 'note', editor: 'textfield', filter: 'string', align: 'left', flex: 3 }

	],

	config : {
	}


});

