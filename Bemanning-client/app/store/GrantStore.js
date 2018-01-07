Ext.define('Bemanning.store.GrantStore', {
    extend: 'Ext.data.Store',
    requires: ['Ext.data.proxy.Rest', 'Ext.data.reader.Json'],
    alias: 'store.grants',
	model: 'Bemanning.model.CourseGrant',
    autoLoad: true
});

//Ext.create('PorTableClient.store.UserStore').load();
