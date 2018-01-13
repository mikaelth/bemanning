Ext.define('Bemaning.view.ous.YoHController', {
//    extend: 'Ext.app.ViewController',
    extend: 'Bemaning.view.BasicController',

    alias: 'controller.yohlist',

    
    onCreate: function()
    {
        var grid = this.getView(),
        	ouId = this.getViewModel().get('currentOU.id');
        grid.plugins[0].cancelEdit();
		
        // Create a model instance
        var r = Ext.create('Bemanning.model.YoH');
        r.set('unitId',ouId);
        
		grid.getStore().insert(0, r);
        grid.plugins[0].startEdit(0, 1);
        
    },

   	onBeforeRender: function (grid) {
   	}

    
});
