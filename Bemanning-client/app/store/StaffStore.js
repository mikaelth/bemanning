Ext.define('Bemanning.store.StaffStore', {
    extend: 'Ext.data.Store',
    requires: ['Ext.data.proxy.Rest', 'Ext.data.reader.Json'],
    alias: 'store.staff',
	model: 'Bemanning.model.Staff',
//	sorters: {property:'familyName', direction: 'ASC'},
//	groupField: 'organisationUnitId',
//    autoLoad: true
    autoLoad: false
});

//Ext.create('PorTableClient.store.UserStore').load();
