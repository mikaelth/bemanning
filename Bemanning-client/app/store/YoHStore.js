Ext.define('Bemanning.store.YoHStore', {
    extend: 'Ext.data.Store',
    requires: ['Ext.data.proxy.Rest', 'Ext.data.reader.Json'],
    alias: 'store.yoh',
	model: 'Bemanning.model.YoH',
    autoLoad: true
});
