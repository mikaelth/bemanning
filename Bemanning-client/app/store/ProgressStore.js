Ext.define('Bemanning.store.ProgressStore', {
    extend: 'Ext.data.Store',
    requires: ['Ext.data.proxy.Rest', 'Ext.data.reader.Json'],
    alias: 'store.progress',
	model: 'Bemanning.model.Progress',
//	sorters: {property:'familyName', direction: 'ASC'},
//    autoLoad: true
    autoLoad: false
});

//Ext.create('PorTableClient.store.UserStore').load();
