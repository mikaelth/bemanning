/**
 * This class is the view model for the Main view of the application.
 */
Ext.define('Bemanning.view.main.MainModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.main',

    data: {
        name: 'Bemanning',
		current: {
			user : null,
		},
		hidden : {
			assignment: false,
			staff: false,
			cis: false,
			persons: false,
			courses: false,
			ous: false,
			phdstudents: true
		},
		required : {
			assignment: ['slask'],
			staff: ['DirectorOfStudies'],
			cis: ['DirectorOfStudies'],
			persons: ['DirectorOfStudies'],
			courses: ['DirectorOfStudies'],
			ous: ['DirectorOfStudies'],
			phdstudents: ['PhDAdmin']
		},
    },
    
    stores: {
		cuser: {
			type: 'chained',
			source: 'CurrentUserStore'
		}    	
    },

	formulas: {
        currentUser: {
            // We need to bind deep to be notified on each model change
            bind: {
                bindTo: '{cuser}', //--> reference configurated on the grid view (reference: comboCurrentYear)
                deep: true
            },
            get: function(user) {
            	this.set('current.user', user.getAt(0));
                return user.getAt(0);
            }
        },
        authorized: {
            // We need to bind deep to be notified on each model change
            bind: {
                auths: '{current.user.userRoles}',
                reqs: '{required}',
                autd : '{current.authzed}'
            },
            get: function(data) {
				this.set('authzed','!{reqs.assignment} in {auths}');
                return data.autd.assignment;
            }
        },
        authAssignment: {
            // We need to bind deep to be notified on each model change
            bind: {
                auths: '{current.user.userRoles}',
                req: '{required.assignment}',
                visible : '{current.visible.assignment}'
            },
            get: function(data) {
				var findOne = function (haystack, arr) {
					return arr.some(function (v) {
						return haystack.indexOf(v) >= 0;
					});
				};
            	if (findOne(data.auths, data.req)) {
            		this.set('current.visible.assignment',true);	
            	}
                return data.autd;
            }
        }
 
	}
    //TODO - add data, formulas and/or methods to support your view
});
