Ext.define('Bemanning.store.CourseStore', {
    extend: 'Ext.data.Store',
    requires: ['Ext.data.proxy.Rest', 'Ext.data.reader.Json'],
    alias: 'store.courses',
	model: 'Bemanning.model.Course',
	sorters: {property:'code', direction: 'ASC'},
    autoLoad: true
});

//Ext.create('PorTableClient.store.UserStore').load();
