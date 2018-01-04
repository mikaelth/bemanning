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


	
//	features: [{ ftype: 'grouping',startCollapsed: false }],
 
	features: [
/*	{
        groupHeaderTpl: 'Totalt',
        ftype: 'groupingsummary'
    }, 
*/
    { 
    	ftype: 'grouping',
    	startCollapsed: false 
    }
    ],

    
    columns: [
		{ text: 'Asn', dataIndex: 'assigningDept', editor: 'textfield', align: 'left', width: 80 },
		{ text: 'Lärare', dataIndex: 'staffId', editor: 'textfield', align: 'left', flex: 1,
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

/*		{ text: 'Enhet', dataIndex: 'organisationUnitId', align: 'left', width: 100, flex: 1,
         	renderer: function(value) {
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
				bind: {store: '{ous}'},
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
		{ text: 'Befattning', dataIndex: 'position', align: 'left', flex: 1,
			editor: {
				xtype: 'combobox',
				typeAhead: true,
				triggerAction: 'all',
				bind: {store: '{employmentTypes}'},
				queryMode: 'local',
				lastQuery: '',
				displayField: 'label',
			    valueField: 'label',
			    listeners: {
					// delete the previous query in the beforequery event or set
					// combo.lastQuery = null (this will reload the store the next time it expands)
					beforequery: function(qe){
						delete qe.combo.lastQuery;
					}							    
			    }				
			}
		},
		{ text: 'Timkostnad', dataIndex: 'hourlyCharge', editor: 'numberfield', align: 'left', flex: 1,
			renderer: function(value) {
				return Ext.util.Format.number(value, '0.00 kr/timma');
			}
		},
		{ text: 'Procent GU', dataIndex: 'percentGU', editor: 'numberfield', align: 'left', flex: 1,
			renderer: function(value) {
				return Ext.util.Format.number(value * 100, '0%');
			}
		},
*/		
		{ text: 'Totalt', dataIndex: 'totalHours', editor: 'textfield', align: 'left', width: 100, 
			summaryType: 'sum',
/* 
        	summaryRenderer: function(value){
            	return value;//Ext.String.format('{0} student{1}', value, value !== 1 ? 's' : '');
        	}
 */
 		},
		{ text: 'Admin', dataIndex: 'hoursAdmin', editor: 'textfield', align: 'left', width: 100 },
		{ text: 'Utveckling', dataIndex: 'hoursDevelopment', editor: 'textfield', align: 'left', width: 100 },
		{ text: 'Föreläsning', dataIndex: 'hoursLecture', editor: 'textfield', align: 'left', width: 100 },
		{ text: 'Laboration', dataIndex: 'hoursPractical', editor: 'textfield', align: 'left', width: 100 },
		{ text: 'Exkursion', dataIndex: 'hoursExcursion', editor: 'textfield', align: 'left', width: 100 },
		{ text: 'Handledning', dataIndex: 'hoursSeminar', editor: 'textfield', align: 'left', width: 100 },
		{ text: 'Kommentar', dataIndex: 'note', editor: 'textfield', align: 'left', flex: 2 }

	],

	config : {
	}


});

