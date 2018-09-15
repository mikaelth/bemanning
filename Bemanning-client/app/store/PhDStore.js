Ext.define('Bemanning.store.PhDStore', {
    extend: 'Ext.data.Store',
    requires: ['Ext.data.proxy.Rest', 'Ext.data.reader.Json'],
    alias: 'store.phds',
	model: 'Bemanning.model.PhDPosition',
//	sorters: {property:'familyName', direction: 'ASC'},
//    autoLoad: true
    autoLoad: false
});

//Ext.create('PorTableClient.store.UserStore').load();
