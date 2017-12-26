Ext.define('Bemaning.view.BasicController', {
    extend: 'Ext.app.ViewController',

    onItemSelected: function (sender, record) {
        Ext.Msg.confirm('Confirm', 'Are you sure?', 'onConfirm', this);
    },

    onSelectionChange: function(sm, rec)
    {
		console.log(rec);

        this.lookupReference('btnRemove').enable();
    },

    onReload: function()
    {
        if (this.getView().getStore().hasOwnProperty('source')) {
        	this.getView().getStore().getSource().reload();
        } else {
        	this.getView().getStore().reload();
        }
    },

    onSave: function()
    {
    	var store = this.getView().getStore().hasOwnProperty('source') ? this.getView().getStore().getSource() : this.getView().getStore();
        store.sync();
    },

   onRemove: function()
    {
        var grid = this.getView();
        var sm = grid.getSelectionModel();
            grid.plugins[0].cancelEdit();
            grid.getStore().remove(sm.getSelection());
            if (grid.getStore().getCount() > 0) {
                sm.select(0);
            }
    },
    
   	onBeforeRender: function (grid) {
   	}

    
});
