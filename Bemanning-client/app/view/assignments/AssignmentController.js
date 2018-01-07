Ext.define('Bemaning.view.assignments.AssignmentController', {
//    extend: 'Ext.app.ViewController',
    extend: 'Bemaning.view.BasicController',

    alias: 'controller.assignmentlist',

    onCreate: function()
    {
        var grid = this.getView(),
//			thisYear = grid.getViewModel().get('current.year.label');
			thisYear = grid.getViewModel().get('current.year');
			thisDept = grid.getViewModel().get('cuDept');
        
        grid.plugins[0].cancelEdit();

        // Create a model instance
        var r = Ext.create('Bemanning.model.Assignment');
        r.set('year',thisYear);
        r.set('assigningDept',thisDept);
		var rec = grid.getStore().insert(0, r);
        grid.plugins[0].startEdit(rec[0]);
        
    },

   	onBeforeRender: function (grid) {
   	},
   	
   	init: function (view) {

		var tb = view.down('toolbar');
		
		tb.insert(2,
			{
				xtype: 'cycle',
				showText: true,
				tooltip:'Växlar mellan att kunna redigera andra institutioners poster och ha dem låsta',
				menu: {
					items: [{
						itemId: 'dgr-1',
						text:'Är försiktig',
						checked:true
					},{
						itemId: 'dgr-2',
						text:'Lever farligt'
					}],
				},
				changeHandler:function(cycleBtn, activeItem){
					if (activeItem.itemId == 'dgr-2') {
						this.up('grid').getViewModel().set('current.deptEditable',true);
					} else {
						this.up('grid').getViewModel().set('current.deptEditable',false);
					}
				}
			});			
		tb.insert(2, 
			{
				xtype: 'cycle',
				showText: true,
				tooltip:'Växlar mellan att bara se poster skapade vi egna institutionen och alla poster',
				menu: {
					items: [{
						itemId: 'ego-1',
						text:'Visar allas poster',
						checked:true
					},{
						itemId: 'ego-2',
						text:'Visar endast egna poster',
					}],
				},
				changeHandler:function(cycleBtn, activeItem){

				
					if (activeItem.itemId == 'ego-2') {
						console.log('Set filter'); 
						this.up('grid').getStore().addFilter({id:'deptFilter', property: 'assigningDept', value:'IOB'});

					} else {
						this.up('grid').getStore().removeFilter('deptFilter');
						console.log('Clear filter');        
					}

				}
			});

        view.plugins[0].addListener('beforeEdit', function(rowEditing, context) {
			var desigCol = context.grid.lookupReference('deptCol');
			if (!context.grid.getViewModel().get('current.deptEditable')) {
				desigCol.setEditor( '' );
			} else {
				desigCol.setEditor( 'textfield' ); 		
			}

			return true;


        });
        
	}

    
});
