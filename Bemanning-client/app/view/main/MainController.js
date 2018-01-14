/**
 * This class is the controller for the main view for the application. It is specified as
 * the "controller" of the Main view class.
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */
Ext.define('Bemanning.view.main.MainController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.main',

    onItemSelected: function (sender, record) {
        Ext.Msg.confirm('Confirm', 'Are you sure?', 'onConfirm', this);
    },

    onConfirm: function (choice) {
        if (choice === 'yes') {
            //
        }
    },

   	onTabChange: function (tabPanel, newCard, oldCard, eOpts) {
//		console.log(newCard);
		if (newCard.getReference() == 'logOutTab') {
			window.location.replace('https://weblogin.uu.se/idp/profile/Logout');
		} else if (newCard.getReference() == 'goBackTab') {
			window.location.replace(Bemanning.data.Constants.CORE_URL);
		}
   	}
    
});
