Ext.define('Bemanning.view.main.BasicListGrid', {
    extend: 'Ext.grid.Panel',
    requires: [
        'Ext.selection.RowModel',
        'Ext.Editor',
        'Ext.grid.*',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*'
    ],
	xtype: 'basiclistgrid',
	height: 600,

	frame: true,

    bbar: {
        items: [{
            xtype: 'component',
            itemId: 'order'
        }]
    },
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
					'->', {
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

    selModel: 'rowmodel',
    plugins: [{
        ptype: 'rowediting',
        clicksToEdit: 2,
		autoCancel: false,
		reference: 'theRowEditor',
        listeners: {
            cancelEdit: function(rowEditing, context) {
                // Canceling editing of a locally added, unsaved record: remove it
                if (context.record.phantom) {
                	if (context.store.hasOwnProperty('store')) {
                    	context.store.store.remove(context.record); //chained store
                    } else {
						context.store.remove(context.record);
					}
                }
            },
/* 
            beforeEdit: function(rowEditing, context) {
            	console.log(context);
            }
 */
        }
    },
    'gridfilters'	
    ],
	
	listeners: {
     	selectionchange: 'onSelectionChange',
		beforerender: 'onBeforeRender'
     }

});

