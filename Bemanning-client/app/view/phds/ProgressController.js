Ext.define('Bemaning.view.phds.ProgressController', {
    extend: 'Bemaning.view.BasicController',

    alias: 'controller.progresslist',

    
    onCreate: function()
    {
        var grid = this.getView(),
        	phdId = this.getViewModel().get('currentPhD.id');
        grid.plugins[0].cancelEdit();
		
        // Create a model instance
        var r = Ext.create('Bemanning.model.Progress');
        r.set('phdPositionId',phdId);
        
		grid.getStore().insert(0, r);
        grid.plugins[0].startEdit(0, 1);
        
    },

   	onBeforeRender: function (grid) {
   	}
    
});
