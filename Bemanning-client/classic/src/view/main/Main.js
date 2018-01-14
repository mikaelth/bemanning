/**
 * This class is the main view for the application. It is specified in app.js as the
 * "mainView" property. That setting automatically applies the "viewport"
 * plugin causing this view to become the body element (i.e., the viewport).
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */
Ext.define('Bemanning.view.main.Main', {
    extend: 'Ext.tab.Panel',
    xtype: 'app-main',

    requires: [
        'Ext.plugin.Viewport',
        'Ext.window.MessageBox',

        'Bemanning.view.main.MainController',
        'Bemanning.view.main.MainModel'
    ],

    controller: 'main',
    viewModel: 'main',

    ui: 'navigation',

    tabBarHeaderPosition: 1,
    titleRotation: 0,
    tabRotation: 0,

    header: {
        layout: {
            align: 'stretchmax'
        },
        title: {
            bind: {
                text: '{name}'
            },
            flex: 0
        },
        iconCls: 'fa-th-list'
    },

    tabBar: {
        flex: 1,
        layout: {
            align: 'stretch',
            overflowHandler: 'none'
        }
    },

    responsiveConfig: {
        tall: {
            headerPosition: 'top'
        },
        wide: {
            headerPosition: 'left'
        }
    },

    defaults: {
        bodyPadding: 20,
        tabConfig: {
            plugins: 'responsive',
            responsiveConfig: {
                wide: {
                    iconAlign: 'left',
                    textAlign: 'left'
                },
                tall: {
                    iconAlign: 'top',
                    textAlign: 'center',
                    width: 120
                }
            }
        }
    },

    items: [{
        title: 'Bemanning',
        reference: 'tabAssignment',
        iconCls: 'fa-hourglass-2',
        items: [{
        	xtype: 'assignmentlist',
        	height: 900
        }]
    },{
        title: 'Personal',
//        iconCls: 'fa-graduation-cap',
        iconCls: 'fa-group',
        items: [{
            xtype: 'stafflist', 
			height: 900
        }]
    },{
        title: 'Kurstillfällen',
        iconCls: 'fa-pencil',
		viewModel: {type:'coursemodel'},

        items: [{
            xtype: 'cilist', 
			height: 600
        },{
            xtype: 'grantlist', 
			height: 300
        }]

    }, {
        title: 'Personer',
        iconCls: 'fa-user',
        items: [{
            xtype: 'personlist', 
			height: 900
        }]
    }, {
        title: 'Kurser',
//        iconCls: 'fa-bank',
        iconCls: 'fa-book',
        items: [{
            xtype: 'courselist', 
			height: 900
        }]
    }, {
        title: 'Enheter',
        iconCls: 'fa-sitemap',
		viewModel: {type: 'ous'},

        items: [{
            xtype: 'oulist', 
			height: 600
        },{
            xtype: 'yohlist', 
			height: 300
        }]

    }, {
        title: 'Doktorander',
        iconCls: 'fa-graduation-cap',
		viewModel: 'phds',
		
        items: [{
            xtype: 'phdlist', 
			height: 600
        },{
            xtype: 'progresslist', 
			height: 300
        }]
    }, {
        title: 'Gå till startsidan',
        reference: 'goBackTab',
        iconCls: ' fa-map',
    }, {
        title: 'Logga ut',
        reference: 'logOutTab',
        iconCls: 'fa-cog',
    }],
     
	listeners: {
 		beforeTabChange: 'onTabChange'
 	}

});
