Ext.define('Bemaning.view.courses.CourseGrantController', {
//    extend: 'Ext.app.ViewController',
    extend: 'Bemaning.view.BasicController',

    alias: 'controller.grantlist',

    
    onCreate: function()
    {
        var grid = this.getView(),
        	ci = this.getViewModel().get('currentCI');

         grid.plugins[0].cancelEdit();

		if(!ci.phantom) {
			// Create a model instance
			var r = Ext.create('Bemanning.model.CourseGrant');

			r.set('courseInstanceId',ci.id);

			var rec = grid.getStore().insert(0, r);
			grid.plugins[0].startEdit(rec[0]);
  		}       
       
    },

   	onBeforeRender: function (grid) {
   	}

    
});
