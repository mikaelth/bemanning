Ext.define('Bemanning.view.main.CourseInstanceList', {
    extend: 'Bemanning.view.main.BasicYearListGrid',
    requires: [
    ],
    xtype: 'cilist',
	reference: 'ciList',

    title: 'Kurstillfällen',

	controller: 'courseinstancelist',
//	viewModel: {type:'coursemodel'},


	bind: {store: '{cistore}'},
//	store: 'CIStore',
	
	features: [{ ftype: 'grouping',startCollapsed: false }],

    columns: [
		{ text: 'Kursgrupp', dataIndex: 'courseGroup', align: 'left', width: 150 },
		{ text: 'Kurs', dataIndex: 'courseId', align: 'left', flex: 2,
         	renderer: function(value) {
				if (Ext.getStore('CourseStore').getById(value) != undefined) {
					return Ext.getStore('CourseStore').getById(value).get('formName');
				} else {
					return value;
				}
        	},
			editor: {
				xtype: 'combobox',
				typeAhead: true,
				triggerAction: 'all',
				bind: {store: '{courses}'},
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
			}
		},
		{ text: 'Extra benämning', dataIndex: 'extraDesignation', editor: 'textfield', filter: 'string', align: 'left', flex: 1 },
		{ text: 'Kursledare', dataIndex: 'courseLeaderId', editor: 'textfield', align: 'left', width: 200,
         	renderer: function(value) {
				if (Ext.getStore('StaffStore').getById(value) != undefined) {
					return Ext.getStore('StaffStore').getById(value).get('name');
				} else {
					return value;
				}
        	},
			editor: {
				xtype: 'combobox',
				typeAhead: true,
				triggerAction: 'all',
				bind: {store: '{staff}'},
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
			}
		},
		{ text: 'Start', dataIndex: 'startDate', editor: {xtype: 'datefield', format: 'Y-m-d'}, xtype:'datecolumn', format: 'Y-m-d', filter: 'date', align: 'left', width: 100 },
		{ text: 'Slut', dataIndex: 'endDate', editor: {xtype: 'datefield', format: 'Y-m-d'}, xtype:'datecolumn', format: 'Y-m-d', filter: 'date', align: 'left', width: 100 },
		{ text: 'Studentantal', dataIndex: 'numberOfStudents', editor: 'numberfield', align: 'left', width: 80 },
		{ text: 'Kommentarer', dataIndex: 'note', editor: 'textfield', filter: 'string', align: 'left', flex: 3 }

	],

	config : {
	}


});

