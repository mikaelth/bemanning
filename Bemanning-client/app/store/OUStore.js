Ext.define('Bemanning.store.OUStore', {
    extend: 'Ext.data.Store',
    requires: ['Ext.data.proxy.Rest', 'Ext.data.reader.Json'],
    alias: 'store.ous',
	model: 'Bemanning.model.OrganisationUnit',
//    autoLoad: true
    autoLoad: false
});
