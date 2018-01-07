Ext.define('Bemanning.store.CurrentUserStore', {
    extend: 'Ext.data.Store',
	requires: ['Ext.data.proxy.Rest', 'Ext.data.reader.Json'],
	alias: 'store.cUser',
	model: 'Bemanning.model.User',
//	autoLoad: true
});

//Ext.create('PorTableClient.store.UserStore').load();