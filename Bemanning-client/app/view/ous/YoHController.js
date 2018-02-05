Ext.define('Bemaning.view.ous.YoHController', {
//    extend: 'Ext.app.ViewController',
    extend: 'Bemaning.view.BasicController',

    alias: 'controller.yohlist',

    
    onCreate: function()
    {
        var grid = this.getView(),
        	ou = this.getViewModel().get('currentOU');
        grid.plugins[0].cancelEdit();
		
		if(!ou.phantom) {		
			// Create a model instance
			var r = Ext.create('Bemanning.model.YoH');
			r.set('unitId',ou.id);
		
			var rec = grid.getStore().insert(0, r);
			grid.plugins[0].startEdit(rec[0]);
        }
    },

   	onBeforeRender: function (grid) {
   	}

    
});
