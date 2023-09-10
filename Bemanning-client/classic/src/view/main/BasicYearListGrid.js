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
//						text: 'Reload all',
						text: 'Hämta alla',
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
						fieldLabel: 'Välj år',
						labelWidth: 50,
						displayField: 'label',
						valueField: 'label',
						listeners: {
							select: function() {
								this.lookupReferenceHolder().lookupReference('btnCreate').enable();
							}
						}
					},{
						text: 'Skriv ut',
						reference: 'btnPrint',
						disabled: false,
						handler: function() {
							Ext.ux.grid.Printer.printAutomatically = false;
							Ext.ux.grid.Printer.closeAutomaticallyAfterPrint = false;
							Ext.ux.grid.Printer.print(this.up('grid'));   
						}
					}, '->', {
//						text: 'Remove',
						text: 'Tag bort post',
						reference: 'btnRemove',
						disabled: true,
						 listeners: {
							click: 'onRemove'
						}
					}, {
//						text: 'Create',
						text: 'Ny post',
						reference: 'btnCreate',
						disabled: true,
						listeners: {
							click: 'onCreate'
						}
					}, {
//						text: 'Save',
						text: 'Spara till db',
						reference: 'btnSave',
						listeners: {
							click: 'onSave'
						}
					}

			]
	}],
	
/* 
	constructor: function() {
		console.log('Initing BasicListGrid' + this);
	}
 */
});

