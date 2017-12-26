Ext.define('Bemanning.store.PersonStore', {
    extend: 'Ext.data.Store',
    requires: ['Ext.data.proxy.Rest', 'Ext.data.reader.Json'],
    alias: 'store.people',
	model: 'Bemanning.model.Person',
	sorters: {property:'familyName', direction: 'ASC'},
    autoLoad: true
});

//Ext.create('PorTableClient.store.UserStore').load();
