Ext.define('Bemaning.view.courses.CourseController', {
//    extend: 'Ext.app.ViewController',
    extend: 'Bemaning.view.BasicController',

    alias: 'controller.courselist',

    
    onCreate: function()
    {
        var grid = this.getView();
         grid.plugins[0].cancelEdit();

        // Create a model instance
        var r = Ext.create('Bemanning.model.Course');
		grid.getStore().insert(0, r);
        grid.plugins[0].startEdit(0, 1);
        
    },

   	onBeforeRender: function (grid) {
   	}

    
});
