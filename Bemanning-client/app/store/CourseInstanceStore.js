Ext.define('Bemanning.store.CourseInstanceStore', {
    extend: 'Ext.data.Store',
    requires: ['Ext.data.proxy.Rest', 'Ext.data.reader.Json'],
    alias: 'store.courseinstances',
	model: 'Bemanning.model.CourseInstance', 
//    autoLoad: true
    autoLoad: false
});

//Ext.create('PorTableClient.store.UserStore').load();
