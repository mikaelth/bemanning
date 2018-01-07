Ext.define('Bemanning.view.main.BasicYearListGrid', {
    extend: 'Bemanning.view.main.BasicListGrid',
    requires: [
        'Ext.selection.RowModel',
        'Ext.Editor',
        'Ext.grid.*',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*'
    ],
	xtype: 'basicyearlistgrid',


	dockedItems: [{
		xtype: 'toolbar',
		items: [
		 			{
						text: 'Reload all',
						reference: 'btnReload',
						disabled: false,
						 listeners: {
							click: 'onReload'
						}
					}, 
					{

						xtype: 'combobox',
						reference: 'comboCurrentYear',
						bind: {value: '{workingYear}', store: '{usedYears}'},
						width: 200,
						typeAhead: true,
						triggerAction: 'all',
						queryMode: 'local',
						lastQuery: '',
						displayField: 'label',
						valueField: 'label',
						listeners: {
							select: function() {
								this.lookupReferenceHolder().lookupReference('btnCreate').enable();
							}
						}
					},
					'->', {
						text: 'Remove',
						reference: 'btnRemove',
						disabled: true,
						 listeners: {
							click: 'onRemove'
						}
					}, {
						text: 'Create',
						reference: 'btnCreate',
						disabled: true,
						listeners: {
							click: 'onCreate'
						}
					}, {
						text: 'Save',
						reference: 'btnSave',
						listeners: {
							click: 'onSave'
						}
					}

			]
	}]
});

