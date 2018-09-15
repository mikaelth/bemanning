Ext.define('Bemanning.store.AssignmentStore', {
    extend: 'Ext.data.Store',
    requires: ['Ext.data.proxy.Rest', 'Ext.data.reader.Json'],
    alias: 'store.assignments',
	model: 'Bemanning.model.Assignment',
//    autoLoad: true
    autoLoad: false
});

//Ext.create('PorTableClient.store.UserStore').load();
