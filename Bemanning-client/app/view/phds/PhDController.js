Ext.define('Bemaning.view.phd.PhDController', {
    extend: 'Bemaning.view.BasicController',

    alias: 'controller.phdlist',

		   
    onCreate: function()
    {
        var grid = this.getView();
         grid.plugins[0].cancelEdit();

        // Create a model instance
        var r = Ext.create('Bemanning.model.PhDPosition');
		var rec = grid.getStore().insert(0, r);
        grid.plugins[0].startEdit(rec[0]);
        
    },

    onSave: function()
    {
    	var store = this.getView().getStore().hasOwnProperty('source') ? this.getView().getStore().getSource() : this.getView().getStore(),
    		otherStore = this.getViewModel().getStore('progress').hasOwnProperty('source') ? this.getViewModel().getStore('progress').getSource() :this.getViewModel().getStore('progress');
    	
        store.sync({success: function(b, o){
        		if (o.operations['create']!= null && o.operations['create'].length) {
        			otherStore.sync();
        			otherStore.load();
        		}
        	}
        });
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
						itemId: 'phd-hide',
						text:'Visar enbart aktiva',
						checked:true
					},{
						itemId: 'phd-show',
						text:'Visar alla'
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
