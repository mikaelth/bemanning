Ext.define('Bemaning.view.staff.PersonController', {
//    extend: 'Ext.app.ViewController',
    extend: 'Bemaning.view.BasicController',

    alias: 'controller.personlist',

/* 
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
 */
    
    onCreate: function()
    {
        var grid = this.getView();
         grid.plugins[0].cancelEdit();

        // Create a model instance
        var r = Ext.create('Bemanning.model.Person');
		var rec = grid.getStore().insert(0, r);
        grid.plugins[0].startEdit(rec[0]);
        
    },

   	onBeforeRender: function (grid) {
   	}

    
});
