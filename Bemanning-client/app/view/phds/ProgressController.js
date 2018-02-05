Ext.define('Bemaning.view.phds.ProgressController', {
    extend: 'Bemaning.view.BasicController',

    alias: 'controller.progresslist',

    
    onCreate: function()
    {
        var grid = this.getView(),
        	phd = this.getViewModel().get('currentPhD');
        grid.plugins[0].cancelEdit();
		

		if(!phd.phantom) {
			// Create a model instance
			var r = Ext.create('Bemanning.model.Progress');
			r.set('phdPositionId',phd.id);
		
			var rec = grid.getStore().insert(0, r);
			grid.plugins[0].startEdit(rec[0]);
 		}       
    },

   	onBeforeRender: function (grid) {
   	}
    
});
