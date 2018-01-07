Ext.define('Bemanning.view.main.AssignmentList', {
    extend: 'Bemanning.view.main.BasicYearListGrid',
    requires: [
    ],
    xtype: 'assignmentlist',
	reference: 'assignmentList',

    title: 'Bemanning',

	controller: 'assignmentlist',
	viewModel: 'assignments',

	bind: {store:'{assignments}'},
 

 
	features: [
		{ 
			ftype: 'grouping',
			startCollapsed: false
		}
    
    ],

/* 
 
	features: [{
		id: 'group',
		ftype: 'groupingsummary',
		groupHeaderTpl: '{name}',
		hideGroupedHeader: false,
		enableGroupingMenu: false
	}],
 
 */

    
    columns: [
		{ text: 'Asn', dataIndex: 'assigningDept', editor: 'textfield', align: 'left', width: 80, reference: 'deptCol' },
		{ text: 'Lärare', dataIndex: 'staffId', align: 'left', summaryType: 'count',flex: 1,
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
		{ text: 'Kurstillfälle', dataIndex: 'courseInstanceId', align: 'left', flex: 1,
         	renderer: function(value) {
				if (Ext.getStore('CourseInstanceStore').getById(value) != undefined) {
					return Ext.getStore('CourseInstanceStore').getById(value).get('courseName');
				} else {
					return value;
				}
        	},
			editor: {
				xtype: 'combobox',
				typeAhead: true,
				triggerAction: 'all',
				bind: {store: '{cistore}'},
				queryMode: 'local',
				lastQuery: '',
				displayField: 'courseName',
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
		{ text: 'Totalt', dataIndex: 'totalHours', align: 'left', width: 100, summaryType: 'sum'},
		{ text: 'Admin', dataIndex: 'hoursAdmin', editor: 'textfield', summaryType: 'sum',align: 'left', width: 100 },
		{ text: 'Utveckling', dataIndex: 'hoursDevelopment', editor: 'textfield', summaryType: 'sum',align: 'left', width: 100 },
		{ text: 'Föreläsning', dataIndex: 'hoursLecture', editor: 'textfield', summaryType: 'sum',align: 'left', width: 100 },
		{ text: 'Laboration', dataIndex: 'hoursPractical', editor: 'textfield', summaryType: 'sum',align: 'left', width: 100 },
		{ text: 'Exkursion', dataIndex: 'hoursExcursion', editor: 'textfield', summaryType: 'sum',align: 'left', width: 100 },
		{ text: 'Handledning', dataIndex: 'hoursSeminar', editor: 'textfield', summaryType: 'sum',align: 'left', width: 100 },
		{ text: 'Kommentar', dataIndex: 'note', editor: 'textfield', align: 'left', flex: 2 }

	],

	config : {
	}


});

