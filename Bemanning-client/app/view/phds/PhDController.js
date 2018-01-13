Ext.define('Bemaning.view.phd.PhDController', {
    extend: 'Bemaning.view.BasicController',

    alias: 'controller.phdlist',

		   
    onCreate: function()
    {
        var grid = this.getView();
         grid.plugins[0].cancelEdit();

        // Create a model instance
        var r = Ext.create('Bemanning.model.PhDPosition');
		grid.getStore().insert(0, r);
        grid.plugins[0].startEdit(0, 1);
        
    },

   	onBeforeRender: function (grid) {
   	},
   	
   	init: function (view) {

		var tb = view.down('toolbar');
		
		tb.insert(1,
			{
				xtype: 'cycle',
				showText: true,
				tooltip:'Växlar mellan att visa samtliga doktorander och enbart aktiva',
				menu: {
					items: [{
						itemId: 'phd-show',
						text:'Visar alla',
						checked:true
					},{
						itemId: 'phd-hide',
						text:'Visar enbart aktiva'
					}],
				},
				changeHandler:function(cycleBtn, activeItem){
					if (activeItem.itemId == 'phd-show') {
						this.up('grid').store.clearFilter(false);
					} else {
						this.up('grid').store.filterBy(function(rec,id){
							return (rec.get('dissertation') < 2010-01-01 && !rec.get('inactive'));
						})
					}
				}
			});			
        
	}


    
});
