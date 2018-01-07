Ext.define('Bemaning.view.courses.CourseInstanceController', {
//    extend: 'Ext.app.ViewController',
    extend: 'Bemaning.view.BasicController',

    alias: 'controller.courseinstancelist',

    
    onCreate: function()
    {
        var grid = this.getView(),
			thisYear = this.getViewModel().get('current.year');
		grid.plugins[0].cancelEdit();

        // Create a model instance
        var r = Ext.create('Bemanning.model.CourseInstance');

        r.set('year',thisYear);
		var rec = grid.getStore().insert(0, r);
        grid.plugins[0].startEdit(rec[0]);
        
    },

   	onBeforeRender: function (grid) {
   	}

    
});
